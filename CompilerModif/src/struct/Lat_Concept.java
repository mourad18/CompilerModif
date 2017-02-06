package struct;

import java.util.ArrayList;
/**
 * 
 * @author root
 * Define the concept structure. It includes all the information required to lattice analysis:
 *	id
 *	support : the support of the concept is the number of rules in this concept.
 *	level : this characteristic is related to the level of the concept in the Hasse diagram.
 *	type : This indicates whether the concept is on top, bottom or inner.
 *	extent : A list of rules in the concepts
 *	intent : A list of matchfields in the concepts.
 *	parents : A list of parent of this concepts using the partial order relation. The parents can be viewed in Hasse diagram.
 *	children : A list of children of this concepts using the partial order relation. The children can be viewed in Hasse diagram.
 *	generators : A concept may have a set of generator. In the evaluation process, the characteristics of generators are used to evaluate a concept.
 */
public class Lat_Concept {

	public int id;
	public int support;
	public int level;
	public ConceptType type;
	
	public ArrayList<Lat_Object> extent;
	public ArrayList<Lat_Attribute> intent;
	
	public ArrayList<Integer> parents;
	public ArrayList<Integer> children;
	
	public ArrayList<Lat_Attribute> generators;
	
	public Lat_Concept(int id, int support, int level) {
		
		this.id = id;
		this.support = support;
		this.level = level;
		this.type = null;
		
		extent = new ArrayList<Lat_Object>();
		intent = new ArrayList<Lat_Attribute>();
		
		parents = new ArrayList<Integer>();
		children = new ArrayList<Integer>();
		
		generators = new ArrayList<Lat_Attribute>();
		
	}
	
	public void setConceptType(String str){
		
		if (str.equals("top")){
			type = ConceptType.TOP;
		} else if (str.equals("bottom")){
			type = ConceptType.BOTTOM;
		} else if (str.equals("inner")){
			type = ConceptType.INNER;
		}
		
	}

	@Override
	public String toString() {
		
		String string ="Lat_Concept [id=" + id + ", support=" + support + ", level="
				+ level + ", type=" + type ;
		string +="Extent are :";
		for(int i=0;i<extent.size();i++){
			string +=extent.get(i).toString();
		}
		string +="Intent are :";
		for(int i=0;i<intent.size();i++){
			string +=intent.get(i).toString();
		}
		string +="Generator are :";
		for(int i=0;i<generators.size();i++){
			string +=generators.get(i).toString();
		}
		string +="Parents are :";
		for(int i=0;i<parents.size();i++){
			string +=parents.get(i);
		}
		string +="Children are :";
		for(int i=0;i<children.size();i++){
			string +=children.get(i);
		}
		return string;
	}
	
	
	
}
