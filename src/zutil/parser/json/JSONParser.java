package zutil.parser.json;

import zutil.MultiPrintStream;
import zutil.parser.json.JSONNode.JSONType;

public class JSONParser{
	private String json;
	private int index;


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
		JSONNode node = parser.read();
		MultiPrintStream.out.dump( node );
		JSONWriter writer = new JSONWriter(System.out);
		writer.write(node);
	}

	public JSONParser(String json){
		this.json = json;
	}

	public JSONNode read(){
		index = 0;
		return parse(new StringBuffer(json));
	}

	protected JSONNode parse(StringBuffer json){
		JSONNode root = null;
		JSONNode key = null;
		JSONNode node = null;
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
			root = new JSONNode(JSONType.Map);
			while((key = parse( json )) != null && (node = parse( json )) != null){
				root.add( key.toString(), node );
			}
			break;
		case '[':
			root = new JSONNode(JSONType.List);
			while((node = parse( json )) != null){
				root.add( node );
			}
			break;
		// Parse String
		case '\"':
			root = new JSONNode(JSONType.String);
			next_index = json.indexOf("\"");
			root.set( json.substring(0, next_index) );
			json.delete(0, next_index+1);
			break;
		default:
			root = new JSONNode(JSONType.Number);
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


	protected JSONNode parse2(){
		System.out.println("Recursion");
		JSONNode root = null;

		for(; index<json.length() ;++index){
			if(!Character.isWhitespace( json.charAt(index) ))
				break;
		}

		// Parse Map
		if( json.charAt(index)=='{' ){
			++index;
			System.out.println("Map:");
			root = new JSONNode(JSONType.Map);
			for(; index<json.length() ;++index){
				if( json.charAt(index)=='}' ) break;
				if( Character.isWhitespace( json.charAt(index) )) 
					continue;
				int next = json.indexOf(":", index+1);
				String key = json.substring(index, next);
				index = next+1;
				System.out.println("New Map element: "+key);
				root.add( key, parse2() );
			}
			++index;
		}
		// Parse List
		else if( json.charAt(index)=='[' ){
			++index;
			System.out.println("List");
			root = new JSONNode(JSONType.List);
			for(; index<json.length() ;++index){
				if( json.charAt(index)==']' ) break;
				if( Character.isWhitespace( json.charAt(index) )) 
					continue;
				System.out.println("New List: ");
				root.add( parse2() );
			}
			++index;
		}
		// Parse String
		else if( json.charAt(index)=='\"' ){
			++index;
			int next = json.indexOf("\"", index);
			root = new JSONNode(JSONType.String);
			root.set( json.substring(index, next) );
			System.out.println("String: "+root);
			index = next;
		}
		// Parse Number or Boolean
		else{
			System.out.println("Number");
			int next = index;
			for(; next<json.length() ;++next)
				if( json.charAt(next)==',' || 
						json.charAt(next)==']' ||
						json.charAt(next)=='}'){
					break;
				}
			root = new JSONNode(JSONType.Number);
			root.set( json.substring(index, next).trim() );
			System.out.println("Number: "+root);
			index = next;
		}
		System.out.println("Return");
		return root;
	}

	public void close() {}
}
