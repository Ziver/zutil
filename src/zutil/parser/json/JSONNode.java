package zutil.parser.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * This is an node in an JSON tree,
 * it may contain a Map, list or value
 * 
 * @author Ziver
 */
public class JSONNode implements Iterable<JSONNode>{
	public enum JSONType{
		Map, List, String, Number, Boolean
	}
	private Map<String,JSONNode> map = null;
	private List<JSONNode> list = null;
	private String value = null;
	private JSONType type;


	/**
	 * Creates an instance with an Boolean value
	 */
	public JSONNode(boolean value){
		this.type = JSONType.Boolean;
		this.value = ""+value;
	}
	/**
	 * Creates an instance with an int value
	 */
	public JSONNode(int value){
		this.type = JSONType.Number;
		this.value = ""+value;
	}
	/**
	 * Creates an instance with an double value
	 */
	public JSONNode(double value){
		this.type = JSONType.Number;
		this.value = ""+value;
	}
	/**
	 * Creates an instance with an String value
	 */
	public JSONNode(String value){
		this.type = JSONType.String;
		this.value = value;
	}
	/**
	 * Creates an instance with a specific type
	 */
	public JSONNode(JSONType type){
		this.type = type;
		switch(type){
		case Map:
			map = new HashMap<String,JSONNode>(); break;
		case List:
			list = new LinkedList<JSONNode>(); break;
		}
	}

	/**
	 * @param index is the index of the List or Map
	 * @return an JSONNode that contains the next level of the List or Map
	 */
	public JSONNode get(int index){
		if(map != null)
			return map.get(""+index);
		else if(list != null)
			return list.get(index);
		return null;
	}	
	/**
	 * @param index is the key in the Map
	 * @return an JSONNode that contains the next level of the Map
	 */
	public JSONNode get(String index){
		if(map != null)
			return map.get(index);
		return null;
	}

	/**
	 * @return a iterator for the Map or List or null if the node contains a value
	 */
	public Iterator<JSONNode> iterator(){
		if(map != null)
			return map.values().iterator();
		else if(list != null)
			return list.iterator();
		return null;
	}
	/**
	 * @return a iterator for the keys in the Map or null if the node contains a value or List
	 */
	public Iterator<String> keyIterator(){
		if(map != null)
			return map.keySet().iterator();
		return null;
	}
	/**
	 * @return the size of the Map or List or -1 if it is a value
	 */
	public int size(){
		if(map != null)
			return map.size();
		else if(list != null)
			return list.size();
		return -1;
	}


	/**
	 * Adds a node to the List
	 */
	public void add(JSONNode node){
		list.add(node);
	}
	public void add(boolean value){
		list.add(new JSONNode( value ));
	}
	public void add(int value){
		list.add(new JSONNode( value ));
	}
	public void add(double value){
		list.add(new JSONNode( value ));
	}
	public void add(String value){
		list.add(new JSONNode( value ));
	}
	/**
	 * Adds a node to the Map
	 */
	public void add(String key, JSONNode node){
		map.put(key, node);
	}
	public void add(String key, boolean value){
		map.put(key, new JSONNode(value));
	}
	public void add(String key, int value){
		map.put(key, new JSONNode(value));
	}
	public void add(String key, double value){
		map.put(key, new JSONNode(value));
	}
	public void add(String key, String value){
		map.put(key, new JSONNode(value));
	}
	/**
	 * Sets the value of the node, but only if it is setup as an JSONType.Value
	 */
	public void set(int value){
		if( !this.isValue() ) return;
		type = JSONType.Number;
		this.value = ""+value;
	}
	/**
	 * Sets the value of the node, but only if it is setup as an JSONType.Value
	 */
	public void set(double value){
		if( !this.isValue() ) return;
		type = JSONType.Number;
		this.value = ""+value;
	}
	/**
	 * Sets the value of the node, but only if it is setup as an JSONType.Value
	 */
	public void set(boolean value){
		if( !this.isValue() ) return;
		type = JSONType.Boolean;
		this.value = ""+value;
	}
	/**
	 * Sets the value of the node, but only if it is setup as an JSONType.Value
	 */
	public void set(String value){
		if( !this.isValue() ) return;
		type = JSONType.String;
		this.value = value;
	}


	/**
	 * @return if this node contains an Map
	 */
	public boolean isMap(){
		return type == JSONType.Map;
	}
	/**
	 * @return if this node contains an List
	 */
	public boolean isList(){
		return type == JSONType.List;
	}
	/**
	 * @return if this node contains an value
	 */
	public boolean isValue(){
		return type != JSONType.Map && type != JSONType.List;
	}
	/**
	 * @return the type of the node
	 */
	public JSONType getType(){
		return type;
	}


	/**
	 * @return the String value in this node, null if its a Map or List
	 */
	public String getString(){
		return value;
	}
	/**
	 * @return the boolean value in this node
	 */
	public boolean getBoolean(){
		return Boolean.parseBoolean(value);
	}
	/**
	 * @return the integer value in this node
	 */
	public int getInt(){
		return Integer.parseInt(value);
	}
	/**
	 * @return the double value in this node
	 */
	public double getDouble(){
		return Double.parseDouble(value);
	}


	public String toString(){
		if( this.isMap() )
			return map.toString();
		else if( this.isList() )
			return list.toString();
		return value;
	}
}
