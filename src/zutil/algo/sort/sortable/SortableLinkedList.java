package zutil.algo.sort.sortable;

import java.util.LinkedList;

public class SortableLinkedList<T> implements SortableDataList<T>{
	private LinkedList<T> list;
	
	public SortableLinkedList(LinkedList<T> list){
		this.list = list;
	}

	public T getIndex(int i) {
		return list.get(i);
	}

	public int size() {
		return list.size();
	}

	public void swap(int a, int b) {
		T temp = list.get(a);
		list.set(a, list.get(b));		
		list.set(b, temp);
	}
	
	@SuppressWarnings("unchecked")
	public int compare(int a, int b) {
		Comparable aa = (Comparable)list.get(a);
		Comparable bb = (Comparable)list.get(b);
		return aa.compareTo(bb);
	}

	@SuppressWarnings("unchecked")
	public int compare(int a, T b) {
		Comparable aa = (Comparable)list.get(a);
		Comparable bb = (Comparable)b;
		return aa.compareTo(bb);
	}
	
	

}
