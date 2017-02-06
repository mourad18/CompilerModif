package struct;

/**
 * Define the subset of rules structures. It contains an id and a list of rules.
 */
import java.util.ArrayList;

public class SubsetTable {
	
	public int id;
	
	public ArrayList<Rule> rules;
	
	public SubsetTable(int id) {
		this.id = id;
		rules = new ArrayList<Rule>();
	}
	@Override
	public String toString(){
		
		String string ="Subset Table "+ id ;
		
		if (rules.size() != 0){
			for (int i=0; i<rules.size(); i++){
			    //System.out.print("\t");
			    string +=rules.get(i).toString();
			}
		}
	return string;	
	}
	
}
