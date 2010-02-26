package zutil.algo.sort;

import zutil.algo.sort.sortable.SortableDataList;


public class MergeSort{

	/**
	 * Sorts the given list with Merge sort
	 * 
	 * @param list is the list to sort
	 */
	public static void sort(int[] list){	
		if(list == null) 
			return;
		
		sort(list, 0, list.length);
	}

	/**
	 * This method is the array splitting method 
	 * that recursively splits the array in two.
	 * 
	 * @param list is the list to sort
	 * @param start is the starting index of the sub list
	 * @param stop is the end index of the sub list
	 */
	protected static void sort(int[] list, int start, int stop){
		if(stop-start <= 1) return;

		int pivot = start+(stop-start)/2;
		sort(list, start, pivot);
		sort(list, pivot, stop);

		merge(list, start, stop, pivot);
	}

	/**
	 * This method is the merger, after the array 
	 * has been split this method will merge the 
	 * two parts of the array and sort it.
	 * 
	 * @param list is the list to merge
	 * @param start is the start of the first sublist
	 * @param stop is the end of the second sublist
	 * @param pivot is the end index for the first list and the beginning of the second.
	 */
	protected static void merge(int[] list, int start, int stop, int pivot){
		int length = pivot-start;
		int[] tmp = new int[stop-start];

		for(int i=0; i<tmp.length ;++i){
			tmp[i] = list[start+i];
		}

		int index1 = 0;
		int index2 = length;
		for(int i=start; i<stop ;++i){
			if( index2 < stop-start && (index1 >= length || tmp[index1] > tmp[index2]) ){
				list[i] = tmp[index2];
				++index2;
			}
			else {
				list[i] = tmp[index1];
				++index1;
			}
		}
	}
	
	
	/**
	 * Sorts the given list with Merge sort, 
	 * this is slower than the one with int[] array
	 * 
	 * @param list is the list to sort
	 */
	public static void sort(SortableDataList<?> list){	
		if(list == null) 
			return;
		
		sort(list, 0, list.size());
	}

	/**
	 * This method is the array splitting method 
	 * that recursively splits the array in two.
	 * 
	 * @param list is the list to sort
	 * @param start is the starting index of the sub list
	 * @param stop is the end index of the sub list
	 */
	protected static void sort(SortableDataList<?> list, int start, int stop){
		if(stop-start <= 1) return;

		int pivot = start+(stop-start)/2;
		sort(list, start, pivot);
		sort(list, pivot, stop);

		merge(list, start, stop, pivot);
	}

	/**
	 * This method is the merger, after the array 
	 * has been split this method will merge the 
	 * two parts of the array and sort it.
	 * @param <T>
	 * 
	 * @param list is the list to merge
	 * @param start is the start of the first sublist
	 * @param stop is the end of the second sublist
	 * @param pivot is the end index for the first list and the beginning of the second.
	 */
	@SuppressWarnings("unchecked")
	protected static <T> void merge(SortableDataList<T> list, int start, int stop, int pivot){
		int length = pivot-start;
		Object[] tmp = new Object[stop-start];

		for(int i=0; i<tmp.length ;++i){
			tmp[i] = list.get( start+i );
		}

		int index1 = 0;
		int index2 = length;
		for(int i=start; i<stop ;++i){
			if( index2 < stop-start && (index1 >= length || ((Comparable)tmp[index1]).compareTo(tmp[index2]) > 0 )){
				list.set(i, (T)tmp[index2]);
				++index2;
			}
			else {
				list.set(i, (T)tmp[index1]);
				++index1;
			}
		}
	}
}
