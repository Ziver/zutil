package zutil.algo.sort.sortable;

@SuppressWarnings("unchecked")
public class SortableComparableArray implements SortableDataList<Comparable>{
	private Comparable[] list;
	
	public SortableComparableArray(Comparable[] list){
		this.list = list;
	}	
	
	public Comparable getIndex(int i) {
		return list[i];
	}

	public int size() {
		return list.length;
	}

	public void swap(int a, int b) {
		Comparable temp = list[a];
		list[a] = list[b];
		list[b] = temp;
	}

	public int compare(int a, int b) {
		if(list[a].compareTo(list[b]) < 0){
			return -1;
		}
		else if(list[a].compareTo(list[b]) > 0){
			return 1;
		}
		return 0;
	}

	public int compare(int a, Comparable b) {
		if(list[a].compareTo(b) < 0){
			return -1;
		}
		else if(list[a].compareTo(b) > 0){
			return 1;
		}
		return 0;
	}



}
