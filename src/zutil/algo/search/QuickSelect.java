/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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
 */

package zutil.algo.search;

import zutil.algo.sort.sortable.SortableDataList;

/**
 * This algorithm is a modified QuickSort
 * to find the k smallest or biggest value
 * http://en.wikipedia.org/wiki/Selection_algorithm
 *
 * @author Ziver
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class QuickSelect {

    public static Object find(SortableDataList list, int k) {
        return find(list, k, 0, list.size()-1);
    }

    /*
     function select(list, k, left, right)
         select a pivot value list[pivotIndex]
         pivotNewIndex := partition(list, left, right, pivotIndex)
         if k = pivotNewIndex
             return list[k]
         else if k < pivotNewIndex
             return select(list, k, left, pivotNewIndex-1)
         else
             return select(list, k, pivotNewIndex+1, right)
     */
    public static Object find(SortableDataList list, int k, int left, int right) {
        // select a pivot
        int pivot = right/2;
        int newPivot = partition(list, left, right, pivot);
        if (k == newPivot)
            return list.get(k);
        else if (k < newPivot)
            return find(list, k, left, newPivot-1);
        else
            return find(list, k, newPivot+1, right);
    }

    /*
     function partition(list, left, right, pivotIndex)
         pivotValue := list[pivotIndex]
         swap list[pivotIndex] and list[right]  // Move pivot to end
         storeIndex := left
         for i from left to right-1
             if list[i] < pivotValue
                 swap list[storeIndex] and list[i]
                 storeIndex := storeIndex + 1
         swap list[right] and list[storeIndex]  // Move pivot to its final place
         return storeIndex
     */
    private static int partition(SortableDataList list, int left, int right, int pivot) {
        Object pivotValue = list.get(pivot);
        list.swap(pivot, right);
        int storeIndex = left;
        for (int i=left; i<right; i++) {
            if (list.compare(i, pivotValue) < 0) {
                list.swap(storeIndex, i);
                storeIndex = storeIndex + 1;
            }
        }
        list.swap(right, storeIndex);
        return storeIndex;
    }
}
