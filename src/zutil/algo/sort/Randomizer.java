package zutil.algo.sort;

import zutil.algo.sort.sortable.SortableDataList;

/**
 * This class randomizes the index of all the elements in 
 * the Sortable object
 * 
 * @author Ziver
 */
public class Randomizer {

	
	/**
	 * Randomizes the index of all the elements
	 * @param list The list
	 */
	@SuppressWarnings("unchecked")
	public static void sort(SortableDataList list){
		int size = list.size();
		for(int i=0; i<size ;i++){
			list.swap(i, (int)(Math.random()*size));
		}
	}
}
