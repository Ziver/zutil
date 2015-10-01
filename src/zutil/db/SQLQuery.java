/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package zutil.db;

import java.util.LinkedList;

/**
 * A class that generates a query by objects, minimizes errors
 * 
 * @author Ziver
 */
public class SQLQuery {
	protected static abstract class SQLQueryItem{
		SQLQueryItem root;
		
		protected SQLQueryItem(){}
		
		protected void setRoot(SQLQueryItem root){
			this.root = root;
		}
		
		protected abstract void build(StringBuilder query);
		
		public String toString(){
			StringBuilder query = new StringBuilder();
			root.build(query);
			return query.toString();
		}
	}

	//*******************************************
	// Main Types
	/**
	 * <XMP>
	 *	SELECT
	 *	    [ALL | DISTINCT | DISTINCTROW ]
	 *	    [FROM table_references
	 *	    [WHERE where_condition]
	 *	    [GROUP BY {col_name | expr | position}
	 *	      [ASC | DESC], ... [WITH ROLLUP]]
	 *	    [HAVING where_condition]
	 *	    [ORDER BY {col_name | expr | position}
	 *	      [ASC | DESC], ...]
	 *	    [LIMIT {[offset,] row_count | row_count OFFSET offset}]
	 * </XMP>
	 */
	public static class SQLSelect extends SQLQueryItem{
		String[] params;
		SQLFrom from;

		/**
		 * @param		params		is the columns that you want out of the SELECT query, leave empty for all columns
		 */
		protected SQLSelect(String ...params ){
			setRoot(this);
			this.params = params;
		}
		
		protected void build(StringBuilder query) {
			query.append("SELECT ");
			if( params == null || params.length <= 0 )
				query.append("*");
			else{
				for(int i=0; i<params.length ;i++){
					query.append(params[i]);
					if( i != params.length-1 )
						query.append(",");
				}
			}
			if( from != null )
				from.build( query );
		}
		
		public SQLFrom FROM(String ...tables){
			return from = new SQLFrom(this, tables);
		}
	}
	
	/*
	 * <XMP>
	 *	UPDATE [LOW_PRIORITY] [IGNORE] table_reference
	 *	    SET col_name1={expr1|DEFAULT} [, col_name2={expr2|DEFAULT}] ...
	 *	    [WHERE where_condition]
	 *	    [ORDER BY ...]
	 *	    [LIMIT row_count]
	 * </XMP>
	 */
	public static class SQLUpdate extends SQLQueryItem{
		protected SQLUpdate(){
			setRoot(this);
		}
		
		protected void build(StringBuilder query) {
			
		}
	}
	
	/*
	 * <XMP>
	 *	INSERT [INTO] tbl_name
	 *	    SET col_name={expr | DEFAULT}, ...
	 *	
	 *	INSERT [INTO] tbl_name [(col_name,...)]
	 *	    {VALUES | VALUE} ({expr | DEFAULT},...),(...),...
	 * </XMP>
	 */
	public static class SQLInsert extends SQLQueryItem{
		protected SQLInsert(){
			setRoot(this);
		}
		
		protected void build(StringBuilder query) {
			
		}
	}
	
	/*
	 * <XMP>
	 *	DELETE FROM tbl_name
	 *	    [WHERE where_condition]
	 *	    [ORDER BY ...]
	 *	    [LIMIT row_count]
	 * </XMP>
	 */
	public static class SQLDelete extends SQLQueryItem{
		protected SQLDelete(){
			setRoot(this);
		}
		
		protected void build(StringBuilder query) {
			
		}
	}
	
	//*******************************************
	// Sub Types
	public static class SQLFrom extends SQLQueryItem{
		LinkedList<String> tables = new LinkedList<String>();
		SQLQueryItem next;

		protected SQLFrom(SQLQueryItem root, String ...tables){
			setRoot(root);
			for( String table : tables )
				this.tables.add(table);
		}
		
		public SQLFrom NATURAL_JOIN(String table){
			return joinLastTable("NATURAL JOIN", table);
		}
		public SQLFrom NATURAL_JOIN(String ...tables){
			return joinTable("NATURAL JOIN", tables);
		}
		
		public SQLFrom JOIN(String table){
			return joinLastTable("JOIN", table);
		}		
		public SQLFrom JOIN(String ...tables){
			return joinTable("JOIN", tables);
		}
		
		public SQLFrom UNION(String table){
			return joinLastTable("UNION", table);
		}
		public SQLFrom UNION(String ...tables){
			return joinTable("UNION", tables);
		}
		
		private SQLFrom joinLastTable(String type, String table){
			String last = tables.getLast();
			tables.removeLast();
			tables.add(
					new StringBuilder(last).append(" ").append(type).append(" ").append(table).toString());
			return this;
		}
		
		private SQLFrom joinTable(String type, String[] tables){
			if( tables.length < 2 )
				return this;
			StringBuilder str = new StringBuilder();
			for(int i=0; i<tables.length ;i++){
				str.append(tables[i]);
				if( i != tables.length-1 )
					str.append(' ').append(type).append(' ');
			}
			this.tables.add(str.toString());
			return this;
		}
		
		public SQLWhere WHERE(){
			return (SQLWhere) (next = new SQLWhere(root));
		}
		public SQLGroupBy GROUP_BY( String ...cols ){
			return (SQLGroupBy) (next = new SQLGroupBy(root, cols));
		}
		public SQLOrderBy ORDER_BY( String ...cols ){
			return (SQLOrderBy) (next = new SQLOrderBy(root, cols));
		}
		public SQLLimit LIMIT( long count ){
			return (SQLLimit) (next = new SQLLimit(root, count));
		}
		
		protected void build(StringBuilder query) {
			query.append(" FROM ");
			if( tables.isEmpty() )
				throw new RuntimeException("The FROM query item must have at least 1 table!");
			for(int i=0; i<tables.size() ;i++){
				query.append(tables.get(i));
				if( i != tables.size()-1 )
					query.append(", ");
			}
			if( next != null ) next.build( query );
		}
	}
	
	
	//*******************************************
	// Condition Types
	public static class SQLWhere extends SQLQueryItem{
		LinkedList<String> conds = new LinkedList<String>();
		SQLQueryItem next;
		
		protected SQLWhere(SQLQueryItem root){
			setRoot(root);
		}
		
		/**
		 * Equals (arg1 = arg2)
		 */
		public SQLWhere EQ(String arg1, String arg2){
			return cond("=", arg1, arg2);
		}		
		/**
		 * Not Equal (arg1 != arg2)
		 */
		public SQLWhere NE(String arg1, String arg2){
			return cond("!=", arg1, arg2);
		}
		/**
		 * Less than (arg1 < arg2)
		 */
		public SQLWhere LT(String arg1, String arg2){
			return cond("<", arg1, arg2);
		}
		/**
		 * Greater than (arg1 > arg2)
		 */
		public SQLWhere GT(String arg1, String arg2){
			return cond(">", arg1, arg2);
		}
		/**
		 * Less than or equal (arg1 <= arg2)
		 */
		public SQLWhere LE(String arg1, String arg2){
			return cond("<=", arg1, arg2);
		}
		/**
		 * Greater than or equal (arg1 >= arg2)
		 */
		public SQLWhere GE(String arg1, String arg2){
			return cond(">=", arg1, arg2);
		}
		
		private SQLWhere cond(String cond, String arg1, String arg2){
			conds.add(
					//new StringBuilder(arg1).append(cond).append('\"').append(arg2).append('\"').toString());
					new StringBuilder(arg1).append(cond).append(arg2).toString());
			return this;
		}
		
		
		public SQLGroupBy GROUP_BY( String ...cols ){
			return (SQLGroupBy) (next = new SQLGroupBy(root, cols));
		}
		public SQLOrderBy ORDER_BY( String ...cols ){
			return (SQLOrderBy) (next = new SQLOrderBy(root, cols));
		}
		public SQLLimit LIMIT( long count ){
			return (SQLLimit) (next = new SQLLimit(root, count));
		}
		
		protected void build(StringBuilder query) {
			query.append(" WHERE ");
			if( conds.isEmpty() )
				throw new RuntimeException("The WHERE query item must hav atleast 1 condition!");
			for(int i=0; i<conds.size() ;i++){
				query.append(conds.get(i));
				if( i != conds.size()-1 )
					query.append(" AND ");
			}
			if( next != null ) next.build( query );
		}
	}
	
	//*******************************************
	// Sorting Types
	public abstract static class SQLGroupOrderBy<T> extends SQLQueryItem{
		protected String[] cols;
		protected String end;
		SQLQueryItem next;
		
		protected SQLGroupOrderBy(SQLQueryItem root, String ...cols){
			setRoot(root);
			this.cols = cols;
		}
		
		@SuppressWarnings("unchecked")
		public T ASC(){
			end = "ASC";
			return (T)this;
		}
		@SuppressWarnings("unchecked")
		public T DESC(){
			end = "DESC";
			return (T)this;
		}
		
		protected void build(String op, StringBuilder query) {
			query.append(' ').append(op).append(' ');
			if( cols == null || cols.length <= 0 )
				throw new RuntimeException("The "+op+" query item must hav atleast 1 column!");
			for(int i=0; i<cols.length ;i++){
				query.append( cols[i] );
				if( i != cols.length-1 )
					query.append(",");
			}
			if( end != null ) query.append(' ').append( end );
			if( next != null ) next.build( query );
		}	
	}
	
	public static class SQLGroupBy extends SQLGroupOrderBy<SQLGroupBy>{
		
		protected SQLGroupBy(SQLQueryItem root, String ...cols){
			super( root, cols );
		}
	
		
		public SQLOrderBy ORDER_BY( String ...cols ){
			return (SQLOrderBy) (next = new SQLOrderBy(root, cols));
		}
		public SQLLimit LIMIT( long count ){
			return (SQLLimit) (next = new SQLLimit(root, count));
		}
		
		protected void build(StringBuilder query) {
			build("GROUP BY", query);
		}
	}
	public static class SQLOrderBy extends SQLGroupOrderBy<SQLOrderBy>{
		
		protected SQLOrderBy(SQLQueryItem root, String ...cols){
			super( root, cols );
		}
	
		
		public SQLLimit LIMIT( long count ){
			return (SQLLimit) (next = new SQLLimit(root, count));
		}
		
		protected void build(StringBuilder query) {
			build("ORDER BY", query);
		}
	}
	
	public static class SQLLimit extends SQLQueryItem{
		long start;
		Long count;
		
		protected SQLLimit(SQLQueryItem root, long start){
			setRoot( root );
			this.start = start;
		}
		
		public SQLLimit TO( long count ){
			this.count = count;
			return this;
		}
	
		protected void build(StringBuilder query) {
			query.append(" LIMIT ").append( start );
			if( count != null ) query.append(' ').append( count );
		}
	}
	
	//*******************************************
	public static SQLSelect SELECT( String ...params ){
		return new SQLSelect( params );
	}
	
	public static SQLUpdate UPDATE(){
		return new SQLUpdate();
	}
	
	public static SQLInsert INSERT(){
		return new SQLInsert();
	}
	
	public static SQLDelete DELETE(){
		return new SQLDelete();
	}
	
}
