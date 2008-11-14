package zutil.algo.sort.sortable;

public class SortableIntArray implements SortableDataList<Integer>{
	private int[] list;
	
	public SortableIntArray(int[] list){
		this.list = list;
	}	
	
	public Integer getIndex(int i) {
		return list[i];
	}

	public int size() {
		return list.length;
	}

	public void swap(int a, int b) {
		int temp = list[a];
		list[a] = list[b];
		list[b] = temp;
	}

	public int compare(int a, int b) {
		if(list[a] < list[b]){
			return -1;
		}
		else if(list[a] > list[b]){
			return 1;
		}
		return 0;
	}

	public int compare(int a, Integer b) {
		if(list[a] < b){
			return -1;
		}
		else if(list[a] > b){
			return 1;
		}
		return 0;
	}



}
