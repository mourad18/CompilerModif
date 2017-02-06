package struct;

import java.util.HashMap;
import java.util.Map;
/**
 * 
 * @author root
 * Define the rule structure. A rule is a set of matchfields 
 * and an id to distinguish the rule. 
 */
public class Rule {

	public int id;
	public String name;
	// Match
	public Map<Integer, Lat_Attribute> matchfields;
	// Action
	public String action;
	
	public Rule(int id, String name){
		
		this.id = id;
		this.name = name;
		matchfields = new HashMap<Integer, Lat_Attribute>();
		action = "DEFAULT_ACTION";
		
	}
	@Override
	public String toString(){
		
		String string ="Rule "+ id + " ["+ name + "]";
		
		if (matchfields.size() != 0){
			for (Map.Entry<Integer,Lat_Attribute> e : matchfields.entrySet()){
			    //System.out.print("\t(" + e.getKey() + ") \t");
			    string +=e.getValue().toString();
			}
		}
		
		string+="\tAction : " + action;
	return string;
	}
	
}
