package struct;

import java.util.ArrayList;
/**
 * 
 * @author root
 * Define the index structure. It's defined by :
 * An id
 * A list of keys
 * The list of rules associated with the index.
 */
public class Index {
	public int id;
	// Key
	public ArrayList<Lat_Attribute> key;
	// Rule Subset
	public ArrayList<Lat_Object> ruleSubset;
	public Index(int id) {
		this.id = id;
		key = new ArrayList<Lat_Attribute>();
		ruleSubset = new ArrayList<Lat_Object>();
	}

	@Override
	public String toString() {
		String string = "Index [id=" + id+"\n"  ;
		for (int i =0;i<key.size();i++){
			string += key.get(i).toString()+"\n";
		}
		for (int i=0;i<ruleSubset.size();i++){
			string +=ruleSubset.get(i).toString()+"\n";
		}
		return string;
	}
	public String toString(int e) {
		String string = "";
		for(int i=0;i<ruleSubset.size();i++){
			string +=ruleSubset.get(i).toString()+"\n";
		}
		return string;
	}
}
