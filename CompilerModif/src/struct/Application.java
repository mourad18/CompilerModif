package struct;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
	/**
	 * 
	 * @author root
	 * This class define the application structure and all the methods that operates on it.
	 * An application is defined by :
	 * 	 The id of the application.
	 *	 A map of tables.
	 *	 The entries in each sub table.
	 *	 The chaining between tables, it's a matrix that presents the 
	 *      possible go_to actions.
	 *
	 */
public class Application {
	int id;
	Map<Integer,Table> AppTable;
	Map<Integer,SubsetTable> SubSetTable;
	int[][] TableChaining;
	public Application(int id, Map<Integer, Table> appTable,
			Map<Integer, SubsetTable> subSetTable, int[][] tableChaining) {
		super();
		this.id = id;
		AppTable = appTable;
		SubSetTable = subSetTable;
		TableChaining = tableChaining;
	}
	
	
	public Map<Integer, Table> getAppTable() {
		return AppTable;
	}


	public void setAppTable(Map<Integer, Table> appTable) {
		AppTable = appTable;
	}


	public Map<Integer, SubsetTable> getSubSetTable() {
		return SubSetTable;
	}


	public void setSubSetTable(Map<Integer, SubsetTable> subSetTable) {
		SubSetTable = subSetTable;
	}


	@Override
	public String toString() {
		String string = "Application [id=" + id ;
		string +="\n List of table is : \n";
		for(Map.Entry<Integer, Table> table : AppTable.entrySet()){
			string +=table.getValue().id+"\n";
			Iterator<Entry> entryIter = table.getValue().entry.iterator();
			while(entryIter.hasNext()){
				string +=entryIter.next().toString();
			}
		}
		string += "\n List of subset Table is :\n";
		for(Map.Entry<Integer, SubsetTable> subtab : SubSetTable.entrySet()){
			string += "\n"+subtab.getValue().id+"\n"+"\n";
			// Print all the rules in the file
			Iterator<Rule> ruleIter = subtab.getValue().rules.iterator() ;
			while(ruleIter.hasNext()){
				string +=ruleIter.next().name+"\n";
			}
		}
		string +="\n";
		for(int i=0;i<TableChaining.length;i++){
			for(int j=0;j<TableChaining[0].length;j++){
				string +=TableChaining[i][j]+", ";
			}
			string +="\n";
		}
		string +="Dimension of matrix is "+TableChaining.length+","+TableChaining[0].length;
		
		return string;
	}		
	
}
