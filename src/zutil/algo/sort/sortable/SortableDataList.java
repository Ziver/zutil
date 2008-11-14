package zutil.algo.sort.sortable;

public interface SortableDataList<T>{
	
	/**
	 * Returns a specific index i the list
	 * @param i The index
	 * @return
	 */
	public T getIndex(int i);
	
	/**
	 * Returns the size of the list
	 * 
	 * @return The size of the list
	 */
	public int size();
	
	/**
	 * Swaps the given indexes
	 * @param a First index
	 * @param b Second index
	 */
	public void swap(int a, int b);
	
	/**
	 * Compares to indexes and returns: 
	 * <0 if a<b ,
	 * >0 if a>b ,
	 * =0 if a=b
	 * 
	 * @param a Firs index to compare
	 * @param b Second index to compare
	 * @return Look at the info
	 */
	public int compare(int a, int b);
	
	/**
	 * Compares to indexes and returns: 
	 * <0 if a<b ,
	 * >0 if a>b ,
	 * =0 if a=b
	 * 
	 * @param a Firs index to compare
	 * @param b Second Object to compare
	 * @return Look at the info
	 */
	public int compare(int a, T b);

}
