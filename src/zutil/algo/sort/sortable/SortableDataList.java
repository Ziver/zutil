package zutil.algo.sort.sortable;

public interface SortableDataList<T>{
	
	/**
	 * Returns a is a specific index i the list
	 * @param i is the index
	 * @return
	 */
	public T get(int i);
	
	/**
	 * Returns the size of the list
	 * 
	 * @return the size of the list
	 */
	public int size();
	
	/**
	 * Swaps the given indexes
	 * 
	 * @param a is the first index
	 * @param b is the second index
	 */
	public void swap(int a, int b);
	
	/**
	 * Compares to indexes and returns: 
	 * <0 if a<b ,
	 * >0 if a>b ,
	 * =0 if a=b
	 * 
	 * @param a is the first index to compare
	 * @param b is the second index to compare
	 * @return Look at the info
	 */
	public int compare(int a, int b);
	
	/**
	 * Compares to indexes and returns: 
	 * <0 if a<b ,
	 * >0 if a>b ,
	 * =0 if a=b
	 * 
	 * @param a is the first index to compare
	 * @param b is the second Object to compare
	 * @return Look at the info
	 */
	public int compare(int a, T b);

}
