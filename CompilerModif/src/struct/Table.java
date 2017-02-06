package struct;

/**
 * Define the table structure. 
 * A table is defined by the field and the type of search supported.
 */
import java.util.ArrayList;

import javax.swing.text.html.HTMLDocument.Iterator;

public class Table {
	
	public int id;	
	public MatchType matchType;
	public AttributeType matchField;

	
	public ArrayList<Entry> entry;
	
	public Table (int id, AttributeType matchField){
		this.id = id;
		matchType = null;
		this.matchField = matchField;		
		entry = new ArrayList<Entry>();

	}
	public Table(int id,AttributeType matchField, MatchType matchType){
		this.id = id;
		this.matchType = matchType;
		this.matchField = matchField;		
		entry = new ArrayList<Entry>();

	}
	
	public void setTableMatchType(AttributeType matchField){
		matchType = MatchType.EXACT;
		if ( matchField.equals(AttributeType.IpDst) | matchField.equals(AttributeType.IpSrc)){
			matchType = MatchType.LPM;
		}
		
	}
	@Override
	public String toString(){
		//if(this == null) return "Table is null";
		String string="";
		string ="Table id is "+id+" Match Type is "+matchType+" Matchfield is "+matchField+"\n";
		java.util.Iterator<Entry> iter = entry.iterator();
		while(iter.hasNext()){
			string += iter.next().toString()+"\n";
		}
		return string;
	}
	
}
