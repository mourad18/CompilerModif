package handler;

import java.util.ArrayList;

import struct.Index;
import struct.Lat_Attribute;
import struct.Lat_Concept;
import struct.Lat_Object;

public class TrieHandler {
	/**
	 * 
	 * @param ArrayList of Lattice Attribute
	 * @param ArrayList of Lattice Attribute
	 * @return True if the two lists are equal. False otherwise
	 */
	boolean compare(ArrayList<Lat_Attribute> gen, ArrayList<Lat_Attribute> ind){
		int nbElement = 0;
		if (gen.size() != ind.size()){
			return false;
		} else {
			int i = 0;
			while (i<ind.size()){
				if ((gen.get(i).type.equals(ind.get(i).type)) && (gen.get(i).value.equals(ind.get(i).value)))
					nbElement++;
				i++;
			}
		}
		if ( nbElement == gen.size()){
			return true;
		}
		return false;
	}
	/**
	 * 
	 * @param trie : The classification structure.
	 * @param indexOld : The list of indexes.
	 * @param objList : The list lattice objects.
	 * @return an arraylist of indexes
	 */
	public ArrayList<Index> updateIndex (ArrayList<Lat_Concept> trie, ArrayList<Index> indexOld, ArrayList<Lat_Object> objList ){
		ArrayList<Index> index = indexOld;
		ArrayList<Lat_Object> objStack = new ArrayList<Lat_Object>();
		for (int j=0; j<objList.size(); j++){
			if (!objStack.contains(objList.get(j))){
				for (int i=0; i<trie.size(); i++){
					if (trie.get(i).extent.contains(objList.get(j))){
						// Rajouter l'objet a l'index
						if (!trie.get(i).generators.isEmpty()){
							int indexID = -1;
							int cnt = -1;
							// Recuperer l'index dont le subset est le plus minimal
							for (int k=0; k<index.size(); k++){
								if (compare(trie.get(i).generators, index.get(k).key)){
									if (cnt < index.get(k).ruleSubset.size() || cnt < 0){
										cnt = index.get(k).ruleSubset.size();
										indexID = index.get(k).id;
									}
								}
							}
							// Add the rule to the minimal subset
							if (indexID >= 0){
								index.get(indexID).ruleSubset.add(objList.get(j));
								// Add the object the the stack.
								objStack.add(objList.get(j));
							}
						}
					}
				}
			}
		}
		return index;
	}
	/**
	 * 
	 * @param A list of lattice concepts.
	 * @return An arraylist of indexes.
	 */
	public ArrayList<Index> generateIndex (ArrayList<Lat_Concept> trie){
		ArrayList<Index> indexList = new ArrayList<Index>();
		for (int i=0; i<trie.size(); i++){
			int indexId = i;
			Index index = new Index(indexId);
			for (int j=0; j<trie.get(i).generators.size(); j++){
				index.key.add(trie.get(i).generators.get(j));
			}
			for (int j=0; j<trie.get(i).extent.size(); j++){
				index.ruleSubset.add(trie.get(i).extent.get(j));
			}
			indexList.add(index);
		}
		return indexList;
	}
}
