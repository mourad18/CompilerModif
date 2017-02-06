package handler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

import struct.*;

/**
 * 
 * @author root
 * The main class for executing the compilation and the update process. It follows the logic :
 * 1- Read the parameters of the program.
*				2- Generate the lattice using coron program. Coron uses the algorithm dtouch and the method snow for lattice computation.
*				3- The compiler read the rules from ().RCF) file it will store them in a Map structure. The read process is implemented in RuleHandler.java
*				4- Extract the relevant concepts from the lattice. The process of extraction is implemented in TrieHandler.java
*				5- Generate the pipeline using the chosen concepts. The function is implemented in EntryHandler.java
*				6-  [*] If the first argument is "-compile". The compiler will build the Trie using CreateTrie() Methods. And it will serialize the object Trie in order to 									use for update. The serialized object is stored under. /tmp/ 
*				    [*] If the first argument is "-update". The compiler will first deserialize the original trie. After that It will call the update function 									UpdateTrie() implemented in Trie.java class.
 */


public class Compiler {
	public static void main(String[] args) throws InterruptedException, IOException  {
		//Input Params for the program
		String cmdline = args[0];
		String inputFilenameRCF = args[1];
		String inputFilenameRCF_up = args[2];
		int threshold = Integer.parseInt(args[3]);
		int objIdOff = 0;
		String ofcompilerpath = System.getProperty("user.dir")+"/";
		String tmpTrie = ofcompilerpath + "tmp/" + args[4];
		String adressedufichier = ofcompilerpath + "output/"+ args[4] +".json";
	
			

		/********* Temporary files**************/
		int LatticeId = 0;
		String latticeName = "Lattice";
		String LatticeXMLFileName = "lattice/latticeT.xml"; 
		
		/****We start by generating the Lattice*********/

		java.lang.Runtime rt = java.lang.Runtime.getRuntime();
		
		String coron = " -names -order -alg:dtouch -method:snow -ext -xml -of:";// /coron-0.8/Myexample/tmp0.xml
		String generatexml = new StringBuilder().append(ofcompilerpath).append("coron-0.8/core03_leco.sh ").append(ofcompilerpath).append(inputFilenameRCF+" ").append		(threshold).append(coron).append(ofcompilerpath).append(LatticeXMLFileName).toString();
		
		try {		
			Process p = rt.exec(generatexml);
			p.waitFor();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		Lattice lattice = new Lattice(LatticeId, latticeName, threshold);
		LatticeHandler latticeHandler = new LatticeHandler();
		lattice = latticeHandler.ReadLatticeXML(LatticeXMLFileName, LatticeId, latticeName, threshold, objIdOff);
		
		/********Get Rules from Lattice***********/ 
		RuleHandler rulehander = new RuleHandler();
		Map<Integer, Rule> ruleList = new HashMap<Integer, Rule>();
		ruleList = rulehander.GetRulesFromRCF(inputFilenameRCF);
		
		
		
		
		/** Extract significant concept from the set of all concepts **/
		ArrayList<Lat_Concept> favoriteConceptList = new ArrayList<Lat_Concept>();
		
		InterfChoix choix = new InterfChoix(lattice,favoriteConceptList);
		
		Scanner sc = new Scanner(System.in);
		System.out.print("Tapez une touche pour continuer\n");
		sc.nextLine();
		/*if (choix.getfavoriteConceptList().isEmpty())
			System.out.println("VIDE");
		else System.out.println("PLEIN");*/
		
		
		/** Generate Indexes **/
		TrieHandler trieHandler = new TrieHandler();
		ArrayList<Index> indexList = trieHandler.generateIndex(choix.getfavoriteConceptList());
		
		/**************** Generate Pipelines **************/
		EntryHandler entryHandler = new EntryHandler();
		ArrayList<Pipeline> pipelines= entryHandler.generateIndexPipeline(indexList); 
		Application app = entryHandler.appGenerator(lattice, indexList, ruleList);
		
		/** Update of lattice 
		 * First we generate a temporary lattice
		 * The second phase would be to fusion the two lattice
		 * The last part would be to update the trie **/
		// Lattice Initialization
		int lat_id = 1;
		Trie trie = new Trie();

		if (cmdline.equals("-compile") ) {
			/*** In the case of the first parameter equals -compile **/
			// Compute trie
			trie.CreateTrie( indexList);
			trie.CompressTrie(trie.root);
			//System.out.println(trie.root);
			
			// Save trie
			File file = new File(tmpTrie);
			 
			FileOutputStream fos = null;
			ObjectOutputStream oos = null;
			try {
				fos = new FileOutputStream(file);
			    oos = new ObjectOutputStream(fos);
			    oos.writeObject(trie);
			} catch (FileNotFoundException fnfe) {
			    System.out.println("Could not find file : " + tmpTrie);
			    fnfe.printStackTrace();
			} catch (IOException ioe) {
			    System.out.println("I/O Exception while writing to file");
			    ioe.printStackTrace();
			} finally {
				if (fos != null) {
					fos.close();
			    }
			  
			}
		
		}
		
		
		if (cmdline.equals("-update") ) {
		/*** In the case of the first parametre equals -update **/	
			//Load trie
			File file = new File(tmpTrie);
			
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(file);
			    ois = new ObjectInputStream(fis);
			     
		    	trie = (Trie) ois.readObject();
			  } catch (FileNotFoundException fnfe) {
				  System.out.println("Could not find file: "+ tmpTrie);
				  fnfe.printStackTrace();
			  } catch (ClassNotFoundException cnfe) {
				  System.out.println("File format is wrong :(");
				  cnfe.printStackTrace();
			  } catch (IOException ioe) {
				  System.out.println("I/O Exception while reading file");
				  ioe.printStackTrace();
			  } finally {
				  if (fis != null) {
					  fis.close();
			    }
			  }
			
			// Update trie
			RuleHandler rulehander2 = new RuleHandler();
			Map<Integer,Rule> updateList =  rulehander2.GetRulesFromRCF(inputFilenameRCF_up);
			for (Map.Entry<Integer,Rule> r_up : updateList.entrySet()){
				trie.UpdateTrie(r_up.getValue());
			}
		}
		
		String trieToText = trie.formatTrieJson(trie.root);//trie.formatTrie(trie.root);
		try
		{
			FileWriter fw = new FileWriter(adressedufichier, false);
			
			BufferedWriter output = new BufferedWriter(fw);
			String header = "{\n\t\"chart\": {\n\t\t\"rootOrientation\": \"WEST\",\n\t\t\"container\": \"#tree-simple\",\n\t\t\"padding\": 0," +
					"\n\t\t\"connectors\": {\n\t\t\t\"type\": \"curve\"\n\t\t},\n\t\t\"node\": {\n\t\t\t\"HTMLclass\": \"nodeExample1\"\n\t\t}\n\t},\n\n";
			//header+="\tnodeStructure:{\n";
			output.write(header);
			output.write(trieToText);
			String foot = "}";
			output.write(foot);
			
			output.flush();
			output.close();
			
//			System.out.print(header);
//			System.out.print(trieToText);
//			System.out.print(foot);
			
		}
		catch(IOException ioe){System.out.println("erreur : " + ioe );}
		
	}
}
