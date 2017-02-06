package handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import java.util.Iterator;

import struct.Application;
import struct.Entry;
import struct.AttributeType;
import struct.Index;
import struct.Lat_Attribute;
import struct.Lat_Object;
import struct.Lattice;
import struct.Pipeline;
import struct.Rule;
import struct.SubsetTable;
import struct.Table;
/**
 * 
 * @author root
 * The EntryHandler class implements funciton to manipulate the entries to read.
 */
public class EntryHandler {
	/**
	 * 
	 * @param lattice
	 * @param indexList
	 * @param rulesList
	 * @return The Arraylist of subsettables. Each subset table will include :
	 *  The rules of the subject.
	 */
	public ArrayList<SubsetTable> generateSubsetTables( Lattice lattice, ArrayList<Index> indexList, Map<Integer, Rule> rulesList ){
		ArrayList<SubsetTable> subsetTableList = new ArrayList<SubsetTable>();
		ArrayList<Lat_Attribute> allAttributsCache = new ArrayList<Lat_Attribute>();
		for (Map.Entry<Integer,Lat_Attribute> e : lattice.attributes.entrySet()){
			allAttributsCache.add(e.getValue());
		}
		for (int i=0; i<indexList.size(); i++){
			/** Get list of attribute of the index**/
			Set<Lat_Attribute> attrIndexCache = new HashSet<Lat_Attribute>();
			for (int j=0; j<indexList.get(i).key.size(); j++){
				attrIndexCache.add(indexList.get(i).key.get(j));
			}
			SubsetTable subsetTable = new SubsetTable (i+1000);
			// Recuperer les attributs pour completer la regle
			ArrayList<AttributeType> fieldTypeStack = new ArrayList<AttributeType>();
			ArrayList<Lat_Object> ruleSubset = indexList.get(i).ruleSubset;
			for (int j=0; j<ruleSubset.size(); j++){
//				////System.out.println("rule subset "+ruleSubset.get(j));
				Rule rule = rulesList.get( ruleSubset.get(j).id-1 );
				for (int k=0; k<allAttributsCache.size(); k++){
					Lat_Attribute attribut = allAttributsCache.get(k);
					if ( !fieldTypeStack.contains(attribut.type) ){
						fieldTypeStack.add(attribut.type);
						if (attrIndexCache.contains(attribut)){
							rule.matchfields.remove(attribut.id);
						} else {
							Lat_Attribute newAttribut = new Lat_Attribute(attribut.id);
							newAttribut.type = attribut.type;
							newAttribut.value = "*";
							rule.matchfields.put(newAttribut.id, newAttribut);
						}
					}
				}
				subsetTable.rules.add(rule);
			}
			subsetTableList.add(subsetTable);
		}
		return subsetTableList;
	}
	/** 
	 * @param indexList
	 * @return The arrayList of the pipelines.
	 *  
	 */
	public ArrayList<Pipeline> generateIndexPipeline(ArrayList<Index> indexList){
		ArrayList<Pipeline> pipelineList = new ArrayList<Pipeline>();
		Map<AttributeType, Table> tableList = new HashMap<AttributeType, Table>();
		int idTable = 0;
		for(int i=0;i<indexList.size();i++){
			Index index = indexList.get(i);
			Pipeline pipeline = new Pipeline(index.id);
			for(int j=0;j<index.key.size();j++){
				Lat_Attribute matchfield = index.key.get(j);
				Table table;
				if(tableList.containsKey(matchfield.type)){
					table = tableList.get(matchfield.type);
				}
				else{
					table = new Table(idTable, matchfield.type);
					idTable++;
				}
				// Create Entry
				String action;
				int tempId ;
				int in_metadata = 'N';
				int out_metadata = 'X';
				if(j+1<index.key.size()){					
					action ="GO_TO_TABLE";
					if(tableList.containsKey(index.key.get(j+1))){
						tempId	= tableList.get(index.key.get(j+1)).id; 						
					}
					else{
						tempId = idTable; 						
					}
					out_metadata = index.id;
				}
				else {
					tempId = index.id+1000; 
					action = "GO_TO_SUBTABLE";
				} 
				  if(j!=0) in_metadata = index.id;				  
					Entry entry = new Entry(index.id, matchfield, action, tempId, in_metadata,out_metadata);
				table.entry.add(entry);
				if(!tableList.containsKey(matchfield.type)){
					tableList.put(matchfield.type, table);
				}
					if(pipeline.tables.containsKey(table.id)) pipeline.tables.remove(table.id);
					pipeline.tables.put(table.id, table);
			} pipelineList.add(pipeline);
		}
		
		return pipelineList;
	}
	/**
	 * 
	 * @param pipelineList
	 * @return The matrix of chaining between the tables
	 */
	public int[][] generateMatrix(ArrayList<Pipeline> pipelineList){
		int[][] matrixTabTab = new int[32][32];
		int[][] matrixTabSub = new int[32][10000];
		Set<Integer> setTab = new HashSet<Integer>() ;
		Set<Integer> setSub = new HashSet<Integer>() ;
		Iterator<Pipeline> pipeIter = pipelineList.iterator();
		while(pipeIter.hasNext()){
			Pipeline pipeline = pipeIter.next();
			for(Map.Entry<Integer, Table> Element : pipeline.tables.entrySet()){
				Table table = Element.getValue();
				Iterator<Entry> entryIter = table.entry.iterator(); 
				while(entryIter.hasNext()){
					Entry entry = entryIter.next();
					String action = entry.action;
					if(action == "GO_TO_TABLE"){
						matrixTabTab[table.id][entry.nextTableId]=1;
						setTab.add(table.id);
						setTab.add(entry.nextTableId);
					}
					if(action == "GO_TO_SUBTABLE"){
						matrixTabSub[table.id][entry.nextTableId-1000]=1;
						setTab.add(table.id);
						setSub.add(entry.nextTableId);
					}
				}
			}
		};
		int[][] finalMatrix = new int[setTab.size()][setTab.size()+setSub.size()];
		for(int i=0;i<setTab.size();i++){
			for(int j=0;j<setSub.size()+setTab.size();j++){
				if(j<setTab.size()){
					finalMatrix[i][j]=matrixTabTab[i][j];
				}
				else{
					finalMatrix[i][j]=matrixTabSub[i][j-setTab.size()];
				}
			}
		}
		return finalMatrix;		
	}
	/**
	 * 
	 * @param lattice
	 * @param indexList
	 * @param rulesList
	 * @return The application. It includes :
	 *  The id of the application.
	 *	A map of tables.
	 *  The entries in each sub table	
	 *  The chaining between tables, it's a matrix that presents the 										
	 */
	public Application appGenerator( Lattice lattice, ArrayList<Index> indexList, Map<Integer, Rule> rulesList ){
		ArrayList<SubsetTable> SubTab = generateSubsetTables(lattice,indexList,rulesList);
		ArrayList<Pipeline> pipelineList = generateIndexPipeline(indexList);
		
		
		Map<Integer,SubsetTable> MapSubTab = new HashMap<Integer,SubsetTable>();
		Map<Integer,Table> MapTable = new HashMap<Integer,Table>();
		
		int e=0;
		Iterator<SubsetTable> subtTabIter = SubTab.iterator();
		while(subtTabIter.hasNext() && e<(SubTab.size()+1)){
			SubsetTable table = subtTabIter.next();
			MapSubTab.put(e, table);
			e++;
		}
		ArrayList<Pipeline> PipeList = generateIndexPipeline(indexList);
		e=0;
		Iterator<Pipeline> pipeIter = PipeList.iterator();
		while(pipeIter.hasNext() && e<100){
			e++;
			for(Map.Entry<Integer, Table> table : pipeIter.next().tables.entrySet()){
				MapTable.put(table.getValue().id, table.getValue());
			}
		}
		int[][] tableChaining = generateMatrix(pipelineList);
		Application app = new Application(0, MapTable, MapSubTab, tableChaining);
		return app;		
	}
}
