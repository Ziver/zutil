package zutil.db;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * A class that generates a query by objects, minimizes errors
 * 
 * @author Ziver
 */
public class SQLQuery {
	protected abstract class SQLQueryItem{
		public abstract void getString(StringBuilder query);
		
		public String toString(){
			StringBuilder query = new StringBuilder();
			this.getString(query);
			return query.toString();
		}
	}

	//*******************************************
	// Main Types
	protected class Select extends SQLQueryItem{
		String[] params;

		/**
		 * @param		params		is the columns that you want out of the SELECT query, leave empty for all columns
		 */
		public Select(String ...params ){
			this.params = params;
		}
		
		public void getString(StringBuilder query) {
			query.append("SELECT ");
			if( params == null )
				query.append("* ");
			else{
				for(int i=0; i<params.length ;i++){
					query.append(params[i]);
					if( i != params.length-1 )
						query.append(", ");
				}
				query.append(' ');
			}			
		}
		
		public From FROM(String table){
			return new From(table);
		}
	}
	
	protected class Update extends SQLQueryItem{
		public void getString(StringBuilder query) {
			
		}
	}
	
	protected class Delete extends SQLQueryItem{
		public void getString(StringBuilder query) {
			
		}
	}
	
	//*******************************************
	// Sub Types
	protected class From extends SQLQueryItem{
		ArrayList<String> tables;

		public From(String ...tables){
			this.tables = new ArrayList<String>();
			for( String table : tables )
				this.tables.add(table);
		}
		
		public From NATURALJOIN(String ...tables){
			return joinTable("NATURAL JOIN", tables);
		}
		
		public From JOIN(String ...tables){
			return joinTable("JOIN", tables);
		}
		
		public From UNION(String ...tables){
			return joinTable("UNION", tables);
		}
		
		private From joinTable(String type, String[] tables){
			StringBuilder str = new StringBuilder();
			for(int i=0; i<tables.length ;i++){
				str.append(tables[i]);
				if( i != tables.length-1 )
					str.append(' ').append(type).append(' ');
			}
			str.append(' ');
			this.tables.add(str.toString());
			return this;
		}
		
		public void getString(StringBuilder query) {
			query.append("FROM ");
			if( tables.isEmpty() )
				throw new RuntimeException("The FROM query item must hav atleast 1 table!");
			for(int i=0; i<tables.size() ;i++){
				query.append(tables.get(i));
				if( i != tables.size()-1 )
					query.append(", ");
			}
			query.append(' ');
		}
	}
	
	
	//*******************************************
	// Condition Types
	protected class Where extends SQLQueryItem{
		
		public void getString(StringBuilder query) {
			
		}
	}
	
	//*******************************************
	// Sorting Types
	
	
	//*******************************************
	public Select SELECT(){
		return new Select();
	}
	
	public Update UPDATE(){
		return new Update();
	}
	
	public Delete DELETE(){
		return new Delete();
	}
	
}
