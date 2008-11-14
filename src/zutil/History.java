package zutil;

import java.util.LinkedList;

public class History<T> {
	public int history_length = 10; 	
	private LinkedList<T> history;
	private int historyIndex = 0;
	
	public History(int histlength){
		history_length = histlength;
		history = new LinkedList<T>();
	}
	
	public void addToHistory(T url){		
		while(historyIndex < history.size()-1){
			history.removeLast();
		}		
		history.addLast(url);
		if(history_length < history.size()){
			history.removeFirst();
		}
		
		historyIndex = history.size()-1;
	}
	
	public T getBackHistory(){
		if(historyIndex > 0){
			historyIndex -= 1;
		}
		else{
			historyIndex = 0;
		}
		return history.get(historyIndex);		
	}
	
	public T getForwHistory(){
		if(forwHistoryExist()){
			historyIndex += 1;
		}
		else{
			historyIndex = history.size()-1;
		}
		return history.get(historyIndex);
	}
	
	public T getCurrentHistory(){
		return history.get(historyIndex);
	}
	
	public boolean forwHistoryExist(){
		if(historyIndex < history.size()-1){
			return true;
		}
		else{
			return false;
		}
	}
}
