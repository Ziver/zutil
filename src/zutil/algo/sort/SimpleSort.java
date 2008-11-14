package zutil.algo.sort;

import zutil.algo.sort.sortable.SortableDataList;

/**
 * A collection of sorting algorithms for arrays of integers.
 * 
 * @author Ziver Koc
 * @version 2006-10-31
 */
public class SimpleSort{ 

    /**
     * Sort the elements in ascending order using selection sort.
     * This algorithm has time complexity Theta(n*n), where n is
     * the length of the array.
     * 
     * @param  list   A list to sort.
     * @return     The same array sorted in ascending order.
     */
    @SuppressWarnings("unchecked")
	public static SortableDataList selectionSort(SortableDataList list){
         int n = list.size();
         for (int i = 0; i < n - 1; i++) {
             // find index m of min element in v[i..n-1]  
             int m = i;
             for (int j = i + 1; j < n; j++) {
                 if (list.compare(j, m) < 0)
                     m = j;
             }
             // swap v[i] and v[m]
             list.swap(i, m);
         }
         
         return list;
    }

    /**
     * Sort the elements in ascending order using insertionsort.
     *
     * @param  A   		A list to sort.
     */
    @SuppressWarnings("unchecked")
	public static SortableDataList insertionSort(SortableDataList list){
    	return insertionSort(list, 0, list.size());
    }
    
	/**
     * Sort the elements in ascending order using insertionsort.
     * 
     * @param  A   		An array of integers.
     * @param  start	The index to start from
     * @param  stop		The index to stop
     */
    @SuppressWarnings("unchecked")
	public static SortableDataList insertionSort(SortableDataList list, int start, int stop){
        for(int i=start; i<stop ;i++){
            for(int j=i; j>start ;j--){
                if(list.compare(j, j-1) < 0){
                	list.swap(j, j-1);
                }
            }
        }
        return list;
    }
   
 }