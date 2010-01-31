package zutil.struct;

import java.util.Iterator;
import java.util.LinkedList;


public class HistoryList<T> implements Iterable<T>{
	public int history_length; 	
	private LinkedList<T> history;
	private int historyIndex = 0;
	
	/**
	 * Creates an HistoryList object
	 */
	public HistoryList(){
		this( Integer.MAX_VALUE );
	}
	
	/**
	 * Creates an HistoryList object with an max size
	 * 
	 * @param histlength the maximum size of the list
	 */
	public HistoryList(int histlength){
		history_length = histlength;
		history = new LinkedList<T>();
	}
	
	/**
	 * Returns the item in the given index
	 * 
	 * @param i is the index
	 * @return item in that index
	 */
	public T get(int i){
		return history.get( i );
	}
	
	/**
	 * Adds an item to the list and removes the last if 
	 * the list is bigger than the max length.
	 * 
	 * @param item is the item to add
	 */
	public void add(T item){		
		while(historyIndex < history.size()-1){
			history.removeLast();
		}		
		history.addLast(item);
		if(history_length < history.size()){
			history.removeFirst();
		}
		
		historyIndex = history.size()-1;
	}
	
	/**
	 * @return the previous item in the list
	 */
	public T getPrevious(){
		if(historyIndex > 0){
			historyIndex -= 1;
		}
		else{
			historyIndex = 0;
		}
		return history.get(historyIndex);		
	}
	
	/**
	 * @return the next item in the list
	 */
	public T getNext(){
		if(next()){
			historyIndex += 1;
		}
		else{
			historyIndex = history.size()-1;
		}
		return history.get(historyIndex);
	}
	
	public T getCurrent(){
		return history.get(historyIndex);
	}
	
	/**
	 * @return if there are items newer than the current
	 */
	public boolean next(){
		if(historyIndex < history.size()-1){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * @return an iterator of the list
	 */
	public Iterator<T> iterator(){
		return history.iterator();
	}
}
