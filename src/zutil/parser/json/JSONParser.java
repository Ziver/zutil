/*******************************************************************************
 * Copyright (c) 2013 Ziver
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

import zutil.io.StringInputStream;
import zutil.parser.DataNode;
import zutil.parser.DataNode.DataType;
import zutil.struct.MutableInt;

import java.io.*;

/**
 * This is a JSON parser class
 * 
 * @author Ziver
 */
public class JSONParser{
    private Reader in;

    public JSONParser(Reader in){
       this.in = in;
    }

    /**
     * Starts parsing from the InputStream.
     * This method will block until one root tree has been parsed.
     *
     * @return a DataNode object representing the input JSON
     */
    public DataNode read() throws IOException {
        return parse(in, new MutableInt());
    }

	/**
	 * Starts parsing from a string
	 * 
	 * @param 	json	is the JSON String to parse
	 * @return a DataNode object representing the JSON in the input String
	 */
	public static DataNode read(String json){
        try{
		    return parse(new StringReader(json), new MutableInt());
        }catch (IOException e){
            e.printStackTrace();
        }catch (NullPointerException e){}
        return null;
	}

    /**
     * This is the real recursive parsing method
     */
    protected static DataNode parse(Reader in, MutableInt end) throws IOException {
        DataNode root = null;
        DataNode key = null;
        DataNode node = null;
        end.i = 0;

        char c = '_';
        while((c=(char)in.read()) < 0 || Character.isWhitespace(c) ||
                c == ',' || c == ':');

        switch( c ){
            // This is the end of an Map or List
            case ']':
            case '}':
            case (char)-1:
                end.i = 1;
                return null;
            // Parse Map
            case '{':
                root = new DataNode(DataType.Map);
                while(end.i != 1 &&
                        (key = parse(in, end)) != null &&
                        (node = parse(in, end)) != null){
                    root.set( key.toString(), node );
                }
                end.i = 0;
                break;
            // Parse List
            case '[':
                root = new DataNode(DataType.List);
                while(end.i != 1 && (node = parse(in, end)) != null){
                    root.add( node );
                }
                end.i = 0;
                break;
            // Parse String
            case '\"':
                root = new DataNode(DataType.String);
                StringBuilder str = new StringBuilder();
                while((c=(char)in.read()) != (char)-1 && c != '\"')
                    str.append(c);
                root.set(str.toString());
                break;
            // Parse Number
            default:
                root = new DataNode(DataType.Number);
                StringBuilder num = new StringBuilder().append(c);
                while((c=(char)in.read()) != (char)-1 && !Character.isWhitespace(c) &&
                        c != ',' && c != '='){
                    if(c == ']' || c == '}'){
                        end.i = 1;
                        break;
                    }
                    num.append(c);
                }
                root.set(num.toString());
                break;
        }

        return root;
    }
}
