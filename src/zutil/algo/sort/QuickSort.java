package zutil.algo.sort;

import zutil.algo.sort.sortable.SortableComparableArray;
import zutil.algo.sort.sortable.SortableDataList;

/**
 * This class implements QuickSort to sort a array
 * 
 * @author Ziver
 */
public class QuickSort{
	public static final int RANDOM_PIVOT = 0;
	public static final int MEDIAN_PIVOT = 1;
	public static final int HALF_PIVOT = 2;
	
    /**
     * Sort the elements in ascending order using quicksort.
     *
     * @param  A   		A list to sort.
     */
    @SuppressWarnings("unchecked")
	public static void sort(SortableDataList list){
    	sort(list, 0, list.size()-1, 2, true);
    }
    
    /**
     * Sort the elements in ascending order using quicksort.
     *
     * @param  A   		A list to sort.
     * @param type		type of pivot
     * @param insert	to use insertion sort when needed
     */
    @SuppressWarnings("unchecked")
	public static void sort(SortableDataList list, int type, boolean insert){
    	sort(list, 0, list.size()-1, type, insert);
    }
	
    /**
     * Sort the elements in ascending order using qicksort.
     * after the 10 th re write and a bad mood i found this
     * site that gave me much help:
     * http://www.inf.fh-flensburg.de/lang/algorithmen/
     *		sortieren/quick/quicken.htm
     *
     * @param  A   		A list to sort.
     * @param  start	The index to start from
     * @param  stop		The index to stop
     * @param  type		The type of pivot to use
     */	
	@SuppressWarnings("unchecked")
	public static void sort(SortableDataList list, int start, int stop, int type, boolean insertionSort){
		if(stop-start <= 15 && insertionSort){
			SimpleSort.insertionSort( list, start, stop);
		}
		int pivotIndex = pivot(list,start,stop,type);
		Object pivot = list.getIndex(pivotIndex);
		int left=start, right=stop;
		
		do{
			while(list.compare(left, pivot) < 0){
				left++;
			}
			while(list.compare(right, pivot) > 0){
				right--;
			}
				
			if(left <= right){
				list.swap(left, right);
				left++;
				right--;
			}
		}while(left <= right);
		
		if(start < right){
			sort(list, start, right, type, insertionSort);
		}
		if(left < stop){
			sort(list, left, stop, type, insertionSort);
		}

	}

    
    @SuppressWarnings("unchecked")
	private static int pivot(SortableDataList list, int start, int stop,int type){
    	switch(type){
    		case 0:
    			return start+(int)(Math.random()*(stop-start));
    		case 1:
    			Comparable[] i = new Comparable[]{
    					(Comparable)list.getIndex(0), 
    					(Comparable)list.getIndex(list.size()/2), 
    					(Comparable)list.getIndex(list.size()-1)};
    			SimpleSort.insertionSort(new SortableComparableArray(i),0,i.length);
    			if(i[i.length/2].compareTo(list.getIndex(start)) == 0)
    				return start;
    			else if(i[i.length/2].compareTo(list.getIndex(stop)) == 0)
    				return stop;
    			else 
    				return start+(stop-start)/2;
    		case 2:
    			return (start+stop)/2;
    	}
    	return 0;
    }
}