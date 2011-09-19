/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
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
 ******************************************************************************/
package zutil.parser.json;

import zutil.parser.DataNode;
import zutil.parser.DataNode.DataType;

/**
 * This is a JSON parser class
 * 
 * @author Ziver
 */
public class JSONParser{
	/**
	 * A dumb class for persisting an index
	 */
	protected class Index{
		protected int i = 0;
	}

	/**
	 * Starts parsing
	 * 
	 * @param 	json	is the JSON String to parse
	 * @return a JSONNode object that is the root of the JSON
	 */
	public DataNode read(String json){
		return parse(new Index(), new StringBuffer(json));
	}

	
	/**
	 * This is the real recursive parsing method
	 * 
	 * @param json
	 * @return
	 */
	protected static DataNode parse(Index index, StringBuffer json){
		DataNode root = null;
		DataNode key = null;
		DataNode node = null;
		int next_index;

		while(Character.isWhitespace( json.charAt(index.i) ) ||
				json.charAt(index.i) == ',' || json.charAt(index.i) == ':')
			index.i++;
		char c = json.charAt(index.i++);
		
		switch( c ){
		case ']':
		case '}':
			return null;
		case '{':
			root = new DataNode(DataType.Map);
			while((key = parse( index, json )) != null && (node = parse( index, json )) != null){
				root.set( key.toString(), node );
			}
			break;
		case '[':
			root = new DataNode(DataType.List);
			while((node = parse( index, json )) != null){
				root.add( node );
			}
			break;
		// Parse String
		case '\"':
			root = new DataNode(DataType.String);
			next_index = json.indexOf( "\"", index.i);
			root.set( json.substring(index.i, next_index) );
			index.i = next_index+1;
			break;
		default:
			root = new DataNode(DataType.Number);
			for(next_index=index.i; next_index<json.length() ;++next_index)
				if( json.charAt(next_index)==',' || 
						json.charAt(next_index)==']' ||
						json.charAt(next_index)=='}'){
					break;
				}
			root.set( c+json.substring(index.i, next_index) );
			index.i = next_index;
			break;
		}

		return root;
	}
}
