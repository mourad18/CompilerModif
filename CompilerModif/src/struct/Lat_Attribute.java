package struct;

/**
 * Define the matchfield structure.
 * An id
 * The field type: It should be one the types defined in Attribute.java
 * Value : The value is presented as a string type.
 */

import java.io.Serializable;

public class Lat_Attribute implements Serializable {

	public int id;
	
	public AttributeType type;
	public String value;
	
	//use only for int 
	int weight;
	public Lat_Attribute(int id) {
		this.id = id;
		type = null;
		value = "";
		weight = 0;
	}

	

	@Override
	public String toString() {
		//return "Lat_Attribute [id=" + id + ", type=" + type + ", value="
		//		+ value + ", weight="+ weight+"]";
		return "["+type+"="+value+"]";
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}



	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lat_Attribute other = (Lat_Attribute) obj;
		//if (id != other.id)
		//	return false;
		if (type != other.type)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}



	public void setAttributeFieldType(String str) {

		String[] part = str.split("=");
		String field = part[0];
		type = AttributeType.valueOf(field);
	}

	public void setAttributeValue(String str) {
		String[] part = str.split("=");
		value = part[1];
	}

}
