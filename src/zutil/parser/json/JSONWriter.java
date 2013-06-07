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

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;

import zutil.io.StringOutputStream;
import zutil.parser.DataNode;
import zutil.parser.DataNode.DataType;

/**
 * Writes An JSONNode to an String or stream
 * 
 * @author Ziver
 */
public class JSONWriter{
	private PrintWriter out;
	
	/**
	 * Creates a new instance of the writer
	 * 
	 * @param out the OutputStream that the Nodes will be sent to
	 */
	public JSONWriter(OutputStream out){
		this( new PrintWriter(out) );
	}
	
	/**
	 * Creates a new instance of the writer
	 * 
	 * @param out the OutputStream that the Nodes will be sent to
	 */
	public JSONWriter(PrintStream out){
		this( new PrintWriter(out) );
	}
	
	/**
	 * Creates a new instance of the writer
	 * 
	 * @param out the OutputStream that the Nodes will be sent to
	 */
	public JSONWriter(PrintWriter out){
		this.out = out;
	}
	
	/**
	 * Writes the specified node to the stream
	 * 
	 * @param root is the root node
	 */
	public void write(DataNode root){
		boolean first = true;
		switch(root.getType()){
		// Write Map
		case Map:
			out.print('{');
			Iterator<String> it = root.keyIterator();
			while(it.hasNext()){
				if(!first)
					out.print(", ");
				String key = it.next();
				try{
					out.print( Integer.parseInt(key) );
				}catch(Exception e){
					out.print('\"');
					out.print(key);
					out.print('\"');
				}				
				out.print(": ");
				write( root.get(key) );
				first = false;
			}
			out.print('}');
			break;
		// Write an list
		case List:
			out.print('[');
			for(DataNode node : root){
				if(!first)
					out.print(", ");
				write( node );
				first = false;
			}
			out.print(']');
			break;		
		default:
			if(root.getString() != null && root.getType() == DataType.String){
				out.print('\"');
				out.print(root.toString());
				out.print('\"');
			} else
				out.print(root.toString());
			break;
		}
		out.flush();
	}
	
	/**
	 * Closes the internal stream
	 */
	public void close(){
		out.close();
	}

    /**
     * @return JSON String that is generated from the input DataNode graph
     */
    public String toString(DataNode root){
        StringOutputStream out = new StringOutputStream();
        JSONWriter writer = new JSONWriter(out);
        writer.write(root);
        writer.close();
        return out.toString();
    }
}
