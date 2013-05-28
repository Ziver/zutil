/*******************************************************************************
 * Copyright (c) 2013 Ziver
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/

package zutil.algo.sort.sortable;

public interface SortableDataList<T>{
	
	/**
	 * Returns a is a specific index i the list
	 * 
	 * @param i is the index
	 * @return
	 */
	public T get(int i);
	
	/**
	 * Sets an Object in the specified index
	 * 
	 * @param i is the index
	 * @param o is the Object
	 */
	public void set(int i, T o);
	
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
