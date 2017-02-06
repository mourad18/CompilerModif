package handler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import struct.AttributeType;
import struct.Lat_Attribute;
import struct.Lat_Object;
import struct.Rule;

public class RuleHandler {
	/**
	 * 
	 * @param filename
	 * @return The number of RElations in .RCF file
	 */
	public int GetRelNumber(String filename){
		String filepath = filename;
		int j=0;
		try{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(filepath);
		NodeList RELList = doc.getElementsByTagName("REL");
		j=RELList.getLength();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
		return j;
	}
	/**
	 * 
	 * @param filename
	 * @return The number of matchfields in the RCF file.
	 */
	public int GetAttNumber(String filename){
		String filepath = filename;
		int j=0;
		try{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(filepath);
		NodeList ATTList = doc.getElementsByTagName("ATT");
		j=ATTList.getLength();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
		return j;
		
	}
	/**
	 * 
	 * @param filename
	 * @return The number of rules in the .RCF file
	 */
	public int GetObjNumber(String filename){
		String filepath = filename;
		int j=0;
		try{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(filepath);
		NodeList OBJList = doc.getElementsByTagName("OBJ");
		j=OBJList.getLength();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
		return j;		
	}
	
	/**
	 * 
	 * @param filename
	 * @return The list of rules.
	 */
	public  Map<Integer, Rule> GetRules( String filename ){
		
		Map<Integer, Rule> rules = new HashMap<Integer, Rule>(); 
		
		try {
			String filepath = filename;
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);

			NodeList ATTList = doc.getElementsByTagName("ATT");
			NodeList RELList = doc.getElementsByTagName("REL");
			NodeList OBJList = doc.getElementsByTagName("OBJ");
			
			Map<Integer, Lat_Object> MapObj = new HashMap<Integer, Lat_Object>(); 
			for(int i=0;i<OBJList.getLength();i++){
				NamedNodeMap OriginNodeAtt = OBJList.item(i).getAttributes();
				Node IdObj = OriginNodeAtt.getNamedItem("id");
				Lat_Object TempObject = new Lat_Object(Integer.parseInt(IdObj.getTextContent()),OBJList.item(i).getTextContent() ); 
				MapObj.put(i, TempObject);
			}
			
		    Map<Integer, Lat_Attribute> MapAttribute = new HashMap<Integer,Lat_Attribute>(ATTList.getLength()); 
			for(int i=0;i<ATTList.getLength();i++){
				NamedNodeMap OriginNodeAtt = ATTList.item(i).getAttributes();
				Node IdObj = OriginNodeAtt.getNamedItem("id");
				Lat_Attribute TempAttribute = new Lat_Attribute(Integer.parseInt(IdObj.getTextContent()));
				TempAttribute.setAttributeFieldType(ATTList.item(i).getTextContent());
				TempAttribute.setAttributeValue(ATTList.item(i).getTextContent());
				MapAttribute.put(i, TempAttribute);
			}
			
			Lat_Attribute TempLat = new Lat_Attribute(0);
			
			int obj=0;
			int  att=0;
			Rule temp = new Rule(0, "");
			String OriginObj = "test";
			int[] table = new int[ATTList.getLength()];
			for (int i = 0; i < RELList.getLength(); i++) {
				NamedNodeMap OriginNodeRel = RELList.item(i).getAttributes();
				Node OriginNodeObj = OriginNodeRel.getNamedItem("idObj");
				String CurrentObj1 = OriginNodeObj.getTextContent();
				Node CurrentNodeAtt = OriginNodeRel.getNamedItem("idAtt");
				String CurrentAtt = CurrentNodeAtt.getTextContent();
				if (!CurrentObj1.equals(OriginObj)) {
					if (!OriginObj.equals("test")) {	
						rules.put(obj, temp);
						obj++;
					}
					temp = new Rule(Integer.parseInt(CurrentObj1), MapObj.get(Integer.parseInt(CurrentObj1)).name);
					att =0;
				}
					temp.matchfields.put(att, MapAttribute.get(Integer.parseInt(CurrentAtt)));
					att++;
					OriginObj = CurrentObj1;
			
					if(i==(RELList.getLength()-1)){
						rules.put(obj, temp);
					}
			}
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}

		return rules;
				
	}
	/**
	 * 
	 * @param filename (.rcf)
	 * @return The list of rules in the file and store them in map structure.
	 * @throws FileNotFoundException
	 */
	public Map<Integer,Rule> GetRulesFromRCF(String filename) throws FileNotFoundException{
		Map<Integer,Rule> ruleList = new HashMap<Integer,Rule>();
		Scanner file = new Scanner(new File(filename));
		String line = null;
		for(int i=0;i<5;i++){
		line = file.nextLine();
		}
		int rulesNumber = line.split("\\|").length;
		String nameRule[] = line.split("\\|");
		line=file.nextLine();
		String[] ObjList = line.split("\\|");
		for (int j=0;j<rulesNumber;j++){
			Rule rule = new Rule(j, nameRule[j]);
			String[] rulemap = file.nextLine().split(" ");
			int attNumber = 0;
			for(int i=0;i<rulemap.length;i++){
				if(Integer.parseInt(rulemap[i])==0) continue;
				
				String[] attribute = ObjList[i].split("=");
				//System.out.println(ObjList[i]);
				AttributeType attType = AttributeType.valueOf(attribute[0].replaceAll("\\s",""));
				String attValue = attribute[1];
				Lat_Attribute lat_att = new Lat_Attribute(attNumber);
				attNumber++;
				lat_att.type = attType;
				lat_att.value = attValue;
				rule.matchfields.put(attNumber, lat_att);						
		}                      
		ruleList.put(rule.id, rule);
		}		
		file.close();
		return ruleList;
		
	}
	
	
	/**
	 * The function transform xml file to .rcf file
	 * @param xmlInput
	 * @param outputfile
	 */
	public void  XmlToRcf(String xmlInput, String outputfile) {
	
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(outputfile, "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		writer.println("[Relational Context]" + "\n" + "Default Name" + "\n"
				+ "[Binary Relation]" + "\n" + "Default Name");
	
		try {
			String filepath = xmlInput;
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(filepath);
	
			NodeList ATTList = doc.getElementsByTagName("ATT");
			NodeList RELList = doc.getElementsByTagName("REL");
			NodeList OBJList = doc.getElementsByTagName("OBJ");
			for(int i=0; i<OBJList.getLength();i++){
				String currentObj =OBJList.item(i).getTextContent();
				writer.print(currentObj+" | ");
			}
			writer.println();
			for (int i = 0; i < ATTList.getLength(); i++) {
				String CurrentATT = ATTList.item(i).getTextContent();
				writer.print(CurrentATT + " | ");
			}
			writer.println();
			String OriginObj = "aa";
			int[] table = new int[ATTList.getLength()];
			int p = 0;
			for (int i = 0; i < RELList.getLength(); i++) {
				NamedNodeMap OriginNodeRel = RELList.item(i).getAttributes();
				Node OriginNodeObj = OriginNodeRel.getNamedItem("idObj");
				String CurrentObj1 = OriginNodeObj.getTextContent();
				Node CurrentNodeAtt = OriginNodeRel.getNamedItem("idAtt");
				String CurrentAtt = CurrentNodeAtt.getTextContent();
	
				if (!CurrentObj1.equals(OriginObj)) {
					if (!OriginObj.equals("aa")) {
						for (int j = 0; j < ATTList.getLength(); j++) {
							writer.print(table[j] + " ");
						}
						writer.println();
						java.util.Arrays.fill(table, 0);
					} else {
						table[Integer.parseInt(CurrentAtt)] = 1;
					}
				}
				table[Integer.parseInt(CurrentAtt)] = 1;		
				OriginObj = CurrentObj1;
				if(i==(RELList.getLength()-1)){
					for (int j = 0; j < ATTList.getLength(); j++) {
						writer.print(table[j] + " ");
					}
					writer.println();
				}
			}
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (SAXException sae) {
			sae.printStackTrace();
		}
		writer.print("[END Relational Context]");
		writer.close();
	
	}
	
	
}