package struct;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

	/**
	 * @author root
	 * The trie structure is structure where the classification structure is stored.
	 * It includes :
	 *  id : The id of the node.
     *  leaf : A Boolean to indicate if the node is a leaf.
	*   key : The key of the node. The key is a matchfield.
	*   childs : The list of child nodes.
	*   parent : The parent node.
	*   level : an int of the level.
	*   ruleSubset : the rule subset of the node. This is not null only if the node is leaf.
	 */
class Node implements Serializable {
	int id;
	boolean leaf;
	Lat_Attribute key;
	ArrayList<Node> childs;
	Node parent;
	int level;
	ArrayList<Lat_Object> ruleSubset;
	public Node(boolean bool,Lat_Attribute key,ArrayList<Node> childs, Node parent, int level) {
		this.leaf = bool;
		this.key = key;
		this.childs = childs;
		this.parent = parent;
		this.level=level;
		this.ruleSubset = new ArrayList<Lat_Object>();
		id=-1;
	}
	public Node() {
		this.leaf = false;
		this.key = new Lat_Attribute(0);
		this.parent = null;
		this.childs = new ArrayList<Node>();
		this.level=0;
		this.ruleSubset = new ArrayList<Lat_Object>();
		id=0;
	}
	@Override
	public String toString() {
		String string = "";
			if(this.level==0){
				string = "*\n";
			}else{
				string =this.key.toString()+" "+this.ruleSubset+"\n";
				}
			for(int i=0;i<this.childs.size();i++){
				String l="";
				for(int j=0;j<this.childs.get(i).level-1;j++){
					l+="\t";
				} l+="|------*";
			string +=l+this.childs.get(i).toString()+"\n";
			}			
		return string;
	}	
}

public class Trie implements Serializable {
	public Node root;
	public int id;
	public String s;

	public Trie() {
		this.root = new Node();	
		this.id = 0;
		this.s ="";
	}
	/**
	 * 
	 * @param node
	 * It is used to compress the trie. The compression occurs only if a node has one child.
	 */
	public void CompressTrie(Node node){
		if(node.level!=0 && node.childs.size()==1){
			node.childs.get(0).level--;
			node.childs.get(0).key=node.key;
			node.parent.childs.remove(node);
			node.childs.get(0).parent = node.parent;
			node.parent.childs.add(node.childs.get(0));
			for(int i=0;i<node.parent.childs.size();i++){
				CompressTrie(node.parent.childs.get(i));
			}
		}else	{	
		for(int i=0;i<node.childs.size();i++){
			CompressTrie(node.childs.get(i));
		}
		}
	}
	
	/**
	 * 
	 * @param list_attr
	 * @return The attribute with maximum weight in evaluation
	 * This attribute will be used for index construction.
	 */
	public Lat_Attribute FindMaxAttr(ArrayList<Lat_Attribute> list_attr){
		int res =0;
		int max=0;
		
		for(int j=0;j<list_attr.size();j++){
			if(list_attr.get(j).weight>max){
				max=list_attr.get(j).weight;
				res=j;
			}
		}
		return list_attr.get(res);
	}	
	/**
	 * 
	 * @param indexList
	 * @return The index that is more shared in an indexList
	 * The construction start by using the more shared index.
	 */
	public Index FindMaxIndex(ArrayList<Index> indexList){
		int res=0;
		int max=0;
		for(int j=0;j<indexList.size();j++){
			if(FindMaxAttr(indexList.get(j).key).weight>max){
				max=FindMaxAttr(indexList.get(j).key).weight;
				res=j;		
			}
		}
		return indexList.get(res);
	}
	/**
	 * 
	 * @param node
	 * @param index
	 * @return The node of longest match with the index.
	 * The search strat always in the root node.
	 */
	public Node LookupIndex(Node node, Index index){
		//System.out.println(index);
		if(node.level==0){
			//This means we are in the root
			for(int e=0;e<node.childs.size();e++){
				if(index.key.contains(node.childs.get(e).key)){
					node=node.childs.get(e);
					LookupIndex(node,index);
				}
			}
			return node;
		}				
		if(index.key.contains(node.key)){
			if(node.leaf!=true){
				for(int e=0;e<node.childs.size();e++){
					if(index.key.contains(node.childs.get(e).key)){						
						LookupIndex(node.childs.get(e),index);
					}
				}
				return node;
			}			
		}		
			return node;
	}
	/**
	 * 
	 * @param indexList
	 * A void funciton that modify the list of indexes.
	 */
	 public void editIndexList(ArrayList<Index> indexList){
		 Index idx = indexList.get(2);
		 indexList.add(idx);
	    }
	 /**
	  * 
	  * @param indexList
	  * This function extend the root node to create a full tri structure
	  */
	public void CreateTrie(ArrayList<Index> indexList){
		//editIndexList(indexList);
		// fix weight problem and use it as base
		for(int i=0;i<indexList.size();i++){
			for(int j=i;j<indexList.size();j++){
				Index index1 = indexList.get(i);
				Index index2 = indexList.get(j);
				for(int e=0;e<index1.key.size();e++){					
					for(int f=0;f<index2.key.size();f++){
						if(index1.key.get(e).equals(index2.key.get(f))){
							index1.key.get(e).weight++;
						}
							
					}
				}
			}
		}
		
		
		
		HashMap<Integer, ArrayList<Index>> index_level = new HashMap<Integer, ArrayList<Index>>();
		java.util.Iterator<Index> iterindex = indexList.iterator();
		while(iterindex.hasNext()){
			Index index = iterindex.next();
			int num_fields = index.key.size();
			//System.out.println(num_fields);
			if(index_level.containsKey(num_fields)){
				index_level.get(num_fields).add(index);
			}
			else{
				ArrayList<Index> temp = new ArrayList<Index>();
				temp.add(index);
				index_level.put(num_fields, temp);
			}			
			if(num_fields==1){
				ArrayList<Node> tempNodeList = new ArrayList<Node>();
				Node node_tmp = new Node(true, index.key.get(0), tempNodeList, this.root,1);
				node_tmp.ruleSubset.addAll(index.ruleSubset);
				this.root.childs.add(node_tmp);
			}
		}
		for(int i=0;i<index_level.size()-1;i++){
		//for(int i=0;i<1;i++){
			//in i=0; we will retrieve the level 2.
			// the first level is already added so we need to start from level 2.
			ArrayList<Index> current_level = index_level.get(i+2);
			int til = current_level.size();
			for(int j=0;j<til;j++){
				Index index = FindMaxIndex(current_level);
				Node node = new Node();
				//node.parent = new Node();
				//System.out.println("\t\tindex to search "+index);
				node = LookupIndex(this.root,index);
				//System.out.println("\t\tEnd of search\n\n");
				//node = this.root.childs.get(0);
				//System.out.println("The result of the search is "+node.key);
				//System.out.println("THE SEARCH RESULT WAS "+node+"END OF NODE\n");
				   //System.out.println("And here "+this.root.childs.size());
				
						//Node nodet = LookupIndex(this.root,index);
						Node nodet = new Node();
						nodet = node;
						ArrayList<Lat_Attribute> node_index_list = new ArrayList<Lat_Attribute>();						
						//node_index_list.add(nodet.key);
						while(nodet.parent!=null){
							node_index_list.add(node.key);
							nodet = nodet.parent;
						}
						ArrayList<Lat_Attribute> remainAtt = new ArrayList<Lat_Attribute>();
						for(int k=0;k<index.key.size();k++){
							if(!node_index_list.contains(index.key.get(k))){
								remainAtt.add(index.key.get(k));
							}
						}
			//if(remainAtt.size()>2) continue;
			int cst = remainAtt.size();
			for(int k=0;k<cst;k++)	{
				//System.out.println("remain"+remainAtt);				
				Lat_Attribute lat_attr = FindMaxAttr(remainAtt);
				//if(k>0 && lat_attr.weight==1) break;
				ArrayList<Node> tempNodeList = new ArrayList<Node>();
				if(remainAtt.size()>1) {
					
					Node temp = new Node(true, lat_attr,tempNodeList,node,node.level+1);
					node.childs.add(temp);
					node.leaf=false;
					node=temp;					
				}
				if(remainAtt.size()==1){
					
					Node temp = new Node(true, lat_attr,tempNodeList,node,node.level+1);
					temp.ruleSubset.addAll(index.ruleSubset);
					node.childs.add(temp);
					node.leaf=false;
					node=temp;
				}
				
				remainAtt.remove(lat_attr);
			} current_level.remove(index);
			}
		}
	}
	
	public ArrayList<Lat_Attribute> ReturnMatch(Node node, Index index) {
		Node nodet = new Node();
		nodet = node;
		ArrayList<Lat_Attribute> node_index_list = new ArrayList<Lat_Attribute>();						
		while(nodet.parent!=null){
			node_index_list.add(node.key);
			nodet = nodet.parent;
		}
		ArrayList<Lat_Attribute> remainAtt = new ArrayList<Lat_Attribute>();
		for(int k=0;k<index.key.size();k++){
			if(!node_index_list.contains(index.key.get(k))){
				remainAtt.add(index.key.get(k));
			}
		}
		
		return remainAtt;
		
	}
	boolean contains2(ArrayList<Lat_Attribute> list, Lat_Attribute att){
		for(Lat_Attribute lat : list){
			if( lat.type.equals(att.type) && lat.value.equals(att.value+' ')){
				return true;
			}
		}
		return false;
		
	}
	public Node LookupIndex2(Node node, Index index){
		//System.out.println(index);
		if(node.level==0){
			//This means we are in the root
			//System.out.println("root "+node.key);
			for(int e=0;e<node.childs.size();e++){
				//System.out.println("root node key "+node.childs.get(e).key);
				if(contains2(index.key,node.childs.get(e).key)){
					//System.out.println("Something found in root "+node.childs.get(e).key+" level "+node.childs.get(e).level);
					node=node.childs.get(e);
					LookupIndex(node,index);
				}
			}
			//System.out.println("return root");
			return node;
		}				
		if(contains2(index.key,node.key)){
			//System.out.println("\t The search is here "+node.key+" level "+node.level);
			if(node.leaf!=true){
				//System.out.println("Something found till now "+node.key);
				for(int e=0;e<node.childs.size();e++){
					//System.out.println("e             "+node.childs.get(e).key);
					if(contains2(index.key,node.childs.get(e).key)){						
						LookupIndex(node.childs.get(e),index);
					}
				}
				//System.out.println("return node "+node.key);
				return node;
			}			
		}		
			return node;
	}
	/**
	 * 
	 * @param r
	 * It will add the rule r to the current Trie.
	 */
	public void UpdateTrie(Rule r){
		
		// Convert Rule to index
		Index r_idx = new Index(r.id);
		//System.out.println(r.toString());
		for (Map.Entry<Integer,Lat_Attribute> e : r.matchfields.entrySet()){
			r_idx.key.add(e.getValue());
		}
		
		//System.out.println(r_idx.toString());
		
		// Update trie
		Node node = new Node();
		//System.out.println("lookup start");
		node = LookupIndex2(this.root, r_idx);
		//System.out.println(node.level);
		if (node.leaf == true){
			node.ruleSubset.add(new Lat_Object(r.id, r.name));
			//System.out.println("leaf");
		} else {
			// Create an outlier
			ArrayList<Lat_Attribute> list = ReturnMatch(node, r_idx);
			Lat_Attribute key;
			if (list.size() == 0) {
				key = r_idx.key.get(0);
			} else {
				key = list.get(0);
			}
			ArrayList<Node> childs = new ArrayList<Node>();
			Node outlier = new Node(true, key, childs, node, (node.level+1));
			outlier.ruleSubset.add(new Lat_Object(r.id, r.name));
			node.childs.add(outlier);
		}
		
	}
	
	public void prepareTrie (Node node) {
		
		node.id = this.id++;
		
		if (node.childs.size() == 0)
			return;
		
		for (Node n: node.childs) {
			prepareTrie(n);
		}
		
	}
	
	public String formatTrie (Node node) {
		
		s+="\tnodeStructure:{\n";
		if (node.leaf == true){
			s+="\t\ttext:{name:\""+node.id+"\"}\n";
			/*System.out.print("\ttext:{name:\"");
			for ( Lat_Object rule : node.ruleSubset) {
				System.out.print(rule.name+" ");
			}
			System.out.print("\"},\n");*/
			//s+="\t\tchildren:[]\n";
			s+="\t}\n\n";
			return s;
		} else {
			s+="\t\ttext:{name:\""+node.id+"\"},\n";
			s+="\t\tchildren:[\n";
			
			for (int n=0; n<node.childs.size(); n++){
				if ( n != node.childs.size()-1 ){
					s+="\t\t\t{\n\t\t\t\ttext:{name:\""+node.childs.get(n).id+"\"}";
					if (node.childs.get(n).childs.size() != 0 ){
						s+=", children:[";
						for (int m=0; m<node.childs.get(n).childs.size(); m++){
							if ( m != node.childs.get(n).childs.size()-1 ){
								s+="{text:{name:\""+ node.childs.get(n).childs.get(m).id+"\"}},";
							} else {
								s+="{text:{name:\""+ node.childs.get(n).childs.get(m).id +"\"}}";
							}
						}
						s+="]\n";
						s+="\t\t\t},\n";
					}
					else {
						s+="\n\t\t\t},\n";
					}
				} else {
					s+="\t\t\t{\n\t\t\t\ttext:{name:\""+node.childs.get(n).id+"\"}\n\t\t\t}\n";
				}
			}

			s+="\t\t]\n";
			s+="\t}\n\n";
			
			for (Node p : node.childs){
				formatTrie (p);
			}
		}
		
		return s;
		
	}
	
	public String printTrieNode (Node node, String pad) {
		
		if (node.level == 0){
			s+="\t\"nodeStructure\":{\n";
			//s+=pad+"{\n";
			s+=pad+"\"text\":{\"name\":\"\"},\n";
		} else {
			s+=pad+"{\n";
			s+=pad+"\"text\":{\"name\":\""+ node.key.type +"="+ node.key.value +"\"},\n";
		}
		s+=pad+"\"children\":[\n";
		if (node.leaf == true) {
			s+=pad+"\t{\"text\":{\"name\":\"";
			for (int r=0; r< node.ruleSubset.size(); r++) {
				if (r != node.ruleSubset.size()-1 )
					s+=node.ruleSubset.get(r).name+",";
				else
					s+=node.ruleSubset.get(r).name;
			}
			s+="\"}, \"HTMLclass\":\"rule_subset\"}\n"+ pad +"  ]\n";
			return s;
		} else {
			pad += "\t";
			for (int p=0; p< node.childs.size(); p++) {
				if (p != node.childs.size()-1 ) {
					printTrieNode (node.childs.get(p), pad);
					s+=pad+" },\n";
				} else {
					printTrieNode (node.childs.get(p), pad);
					s+=pad+" }\n"+pad+"]\n";
				}
			}
		}

		return s;
		
	}
	/**
	 * 
	 * @param node
	 * @return A string in json format. This string will be used by external script
	 *  to trace the tree structure.
	 */
	public String formatTrieJson (Node node) {
		String pad ="\t\t";
		for (int i=0; i< node.level; i++)
			pad+="\t";
		s+=printTrieNode (node, pad);		
		s+="\t}\n";
		return s;
	}
}
