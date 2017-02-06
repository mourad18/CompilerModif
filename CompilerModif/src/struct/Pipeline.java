package struct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 
 * @author root
 * Define the pipeline structure. It has an id and a map of tables.
 */
public class Pipeline {

	public int id;
	public Map<Integer, Table> tables;	
	public Pipeline (int id){
		this.id = id;
		tables = new HashMap<Integer, Table>();		
	}
	
	
	
	@Override
	public String toString() {
		String string;
		
		string = "Pipeline [id=" + id+"\n";
		for (Entry<Integer, Table> e : tables.entrySet()){
			string += e.toString()+"\n";
		}
		return string;
	}
	
}
