package zutil.parser.json;

import zutil.io.MultiPrintStream;
import zutil.parser.DataNode;
import zutil.parser.DataNode.DataType;

/**
 * This is a JSON parser class
 * 
 * @author Ziver
 */
public class JSONParser{
	private String json;

	public static void main(String[] args){
		JSONParser parser = new JSONParser("" +
				"{"+
				"     \"firstName\": \"John\","+
				"     \"lastName\": \"Smith\","+
				"     \"age\": 25,"+
				"     \"address\": {"+
				"         \"streetAddress\": \"21 2nd Street\","+
				"         \"city\": \"New York\","+
				"         \"state\": \"NY\","+
				"         \"postalCode\": \"10021\""+
				"     },"+
				"     \"phoneNumber\": ["+
				"         { \"type\": \"home\", \"number\": \"212 555-1234\" },"+
				"         { \"type\": \"fax\", \"number\": \"646 555-4567\" }"+
				"     ]"+
		"}");
		DataNode node = parser.read();
		MultiPrintStream.out.dump( node );
		JSONWriter writer = new JSONWriter( System.out );
		writer.write(node);
	}

	/**
	 * Creates a new instance of the parser
	 * 
	 * @param 	json	is the JSON String to parse
	 */
	public JSONParser(String json){
		this.json = json;
	}

	/**
	 * Starts parsing
	 * 
	 * @return a JSONNode object that is the root of the JSON
	 */
	public DataNode read(){
		return parse(new StringBuffer(json));
	}
	
	/**
	 * Starts parsing
	 * 
	 * @return a JSONNode object that is the root of the JSON
	 */
	/*public static JSONNode read( String json ){
		return parse(new StringBuffer(json));
	}*/

	/**
	 * This is the real recursive parsing method
	 * 
	 * @param json
	 * @return
	 */
	protected static DataNode parse(StringBuffer json){
		DataNode root = null;
		DataNode key = null;
		DataNode node = null;
		int next_index;

		while(Character.isWhitespace( json.charAt(0) ) ||
				json.charAt(0) == ',' || json.charAt(0) == ':')
			json.deleteCharAt(0);
		char c = json.charAt(0);
		json.deleteCharAt(0);
		
		switch( c ){
		case ']':
		case '}':
			return null;
		case '{':
			root = new DataNode(DataType.Map);
			while((key = parse( json )) != null && (node = parse( json )) != null){
				root.set( key.toString(), node );
			}
			break;
		case '[':
			root = new DataNode(DataType.List);
			while((node = parse( json )) != null){
				root.add( node );
			}
			break;
		// Parse String
		case '\"':
			root = new DataNode(DataType.String);
			next_index = json.indexOf("\"");
			root.set( json.substring(0, next_index) );
			json.delete(0, next_index+1);
			break;
		default:
			root = new DataNode(DataType.Number);
			for(next_index=0; next_index<json.length() ;++next_index)
				if( json.charAt(next_index)==',' || 
						json.charAt(next_index)==']' ||
						json.charAt(next_index)=='}'){
					break;
				}
			root.set( c+json.substring(0, next_index) );
			json.delete(0, next_index);
			break;
		}

		return root;
	}
}
