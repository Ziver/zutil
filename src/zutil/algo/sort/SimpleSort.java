package zutil.algo.sort;

import zutil.algo.sort.sortable.SortableDataList;

/**
 * A collection of simple and slow sorting algorithms.
 * 
 * @author Ziver Koc
 * @version 2006-10-31
 */
public class SimpleSort{ 

    /**
     * Sort the elements in ascending order using bubble sort 
     * witch is the slowest of the algorithms.
     * Complexity: O(n^2).
     *
     * @param A is the list to sort.
     */
	public static void bubbleSort(SortableDataList<?> list){
    	bubbleSort(list, 0, list.size());
    }    
	/**
     * Sort the elements in ascending order using bubble sort
     * witch is the slowest of the algorithms.
     * Complexity: O(n^2).
     * 
     * @param A is an array of integers.
     * @param start is the index to start from
     * @param stop is the index to stop
     */
	public static void bubbleSort(SortableDataList<?> list, int start, int stop){
        for(int i=start; i<stop ;++i){
            for(int j=stop-2; i<=j ;--j){
                if(list.compare(j, j+1) > 0){
                	list.swap(j, j+1);
                }
            }
        }
    }
	
    /**
     * Sort the elements in ascending order using selection sort
     * witch in practice is 40% faster than bubble sort.
     * Complexity: O(n^2).
     * 
     * @param list is the list to sort.
     */
	public static void selectionSort(SortableDataList<?> list){
		selectionSort(list, 0, list.size());
    }	
    /**
     * Sort the elements in ascending order using selection sort
     * witch in practice is 40% faster than bubble sort.
     * Complexity: O(n^2).
     * 
     * @param list is the list to sort.
     * @param start is the index to start from
     * @param stop is the index to stop
     */
	public static void selectionSort(SortableDataList<?> list, int start, int stop){
         for (int i = start; i < stop - 1; i++) {
             // find index m of the minimum element in v[i..n-1]  
             int m = i;
             for (int j = i + 1; j < stop; j++) {
                 if (list.compare(j, m) < 0)
                     m = j;
             }
             // swap v[i] and v[m]
             list.swap(i, m);
         }
    }
	
    /**
     * Sort the elements in ascending order using insertion sort
     * witch in practice is 5 times faster than bubble sort.
     * Complexity: O(n^2).
     *
     * @param  A is a list to sort.
     */
	public static void insertionSort(SortableDataList<?> list){
    	insertionSort(list, 0, list.size());
    }    
	/**
     * Sort the elements in ascending order using insertion sort
     * witch in practice is 5 times faster than bubble sort.
     * Complexity: O(n^2).
     * 
     * @param A is an array of integers.
     * @param start is the index to start from
     * @param stop is the index to stop
     */
	public static void insertionSort(SortableDataList<?> list, int start, int stop){
		for(int i=start; i<stop ;++i){
			for(int j=i; start<j && list.compare(j-1, j)>0 ;--j){
				list.swap(j-1, j);
			}
		}
    }
   
 }