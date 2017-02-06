package struct;

/**
 * 
 * @author root
 * Define the entry selection. An entry is defined by:
 *	An id
 *	A matchfield
 *	The next table
 *	The input metadata: received from previous entry
 *  The output metadata: to be sent to the following entry.
 */

public class Entry {

	public int id;
	// Match
	public Lat_Attribute matchfield;
	
	public int nextTableId;
	
	//Metadata
	public int in_metadata;
	public int out_metadata;
	
	
	// Action
	public String action;
	
	public Entry(int id, Lat_Attribute matchfield, String action, int nextTableId, int in_metadata, int out_metadata){
		this.id = id;
		this.matchfield = matchfield;
		this.action = action;
		
		this.nextTableId = nextTableId;
		this.in_metadata = in_metadata;
		this.out_metadata = out_metadata;
		
	}
	
	@Override
	public String toString() {
		return "Entry [id=" + id + ", matchfield=" + matchfield
				+ ", nextTableId=" + nextTableId + ", action=" + action + "]"+" In_metadata "+in_metadata+" out_metadata "+out_metadata+"\n";
	}
	
}
