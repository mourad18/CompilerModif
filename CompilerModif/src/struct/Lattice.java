package struct;

import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author root
 * Define the Lattice structure. The important parameters are:
 * objects : A map of all the matchfields.
 * attributes : A map of all the rules. 
 * concepts: A map of all the concepts.
 * nbInnerConcept : The number of concepts with inner characteristics.
 * topConceptId : The id of the top concept. This concept includes all the rules.
 * bottomConceptId : The id of the bottom concept. This concept 	includes all the matchfields and 0 rule.
 */
public class Lattice {

	public int id;
	public String name;
	public int minSupport;
	
	public Map<Integer, Lat_Object> objects;
	public Map<Integer, Lat_Attribute> attributes;
	
	public Map<Integer, Lat_Concept> concepts;
	
	public int nbInnerConcept;
	
	public int topConceptId;
	public int bottomConceptId;
	
	public int topConceptLevel;
	public int bottomConceptLevel;
	
	public Lattice(int id, String name, int minSupport){
		
		this.id = id;
		this.name = name;
		this.minSupport = minSupport;
		
		objects = new HashMap<Integer, Lat_Object>();
		attributes = new HashMap<Integer, Lat_Attribute>();
		
		concepts = new HashMap<Integer, Lat_Concept>();

		nbInnerConcept = 0;
		
		topConceptId = -1;
		bottomConceptId = -1;
		
		topConceptLevel = -1;
		bottomConceptLevel = -1;
		
	}
	@Override
	public String toString(){
		
		String string ="Lattice "+ name + " (" + id + ")" + " [minSupp= "+ minSupport +"]";
		string +="\tNb Inner Concept = "+ nbInnerConcept;
		string +="\tTop Concept ID = "+ topConceptId;
		string +="\tBottom Concept ID = "+ bottomConceptId;
		string +="\tTop Concept Level = "+ topConceptLevel;
		string +="\tBottom Concept Level = "+ bottomConceptLevel;
		
		string +="\tObjects :";
		if (objects.size() != 0){
			for (Map.Entry<Integer,Lat_Object> e : objects.entrySet()){
			    //System.out.print("\t(" + e.getKey() + ") \t");
			    string +=e.getValue().toString();
			}
		}
		
		string += "\tAttributes :";
		if (attributes.size() != 0){
			for (Map.Entry<Integer,Lat_Attribute> e : attributes.entrySet()){
			    //System.out.print("\t(" + e.getKey() + ") \t");
			    string +=e.getValue().toString();
			}
		}
		
		string +="\tConcepts :";
		if (concepts.size() != 0){
			for (Map.Entry<Integer,Lat_Concept> e : concepts.entrySet()){
			   // System.out.print("\t(" + e.getKey() + ") \t");
			    //string +=e.getValue().toString();
			}
		}
		return string;
	}
	
}
