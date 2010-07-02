package zutil.parser.json;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;

import zutil.parser.json.JSONNode.JSONType;

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
	public void write(JSONNode root){
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
			for(JSONNode node : root){
				if(!first)
					out.print(", ");
				write( node );
				first = false;
			}
			out.print(']');
			break;		
		default:
			if(root.getString() != null && root.getType() == JSONType.String){
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
	
}
