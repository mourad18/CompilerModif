package struct;

import java.io.Serializable;

/**
 * 
 * @author root
 * Define the rule. it includes an id and the number of the rule. 												
 */
public class Lat_Object implements Serializable {
	
	public int id;
	public String name;
	
	public Lat_Object(int id, String name){
		this.id = id;
		this.name = name;
	}

	@Override
	public String toString() {
		return name ;
	}
}
