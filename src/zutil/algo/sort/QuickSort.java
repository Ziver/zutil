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
    public static final int MIDDLE_PIVOT = 2;

    /**
     * Sort the elements in ascending order using Quicksort.
     *
     * @param	list	is the list to sort.
     */
    public static void sort(SortableDataList<?> list) {
        sort(list, 0, list.size()-1, MIDDLE_PIVOT, true);
    }

    /**
     * Sort the elements in ascending order using Quicksort.
     *
     * @param	list	is the list to sort.
     * @param	type	is the type of pivot
     * @param	insert	is if insertion sort will be used
     */
    public static void sort(SortableDataList<?> list, int type, boolean insert) {
        sort(list, 0, list.size()-1, type, insert);
    }

    /**
     * Sort the elements in ascending order using Quicksort.
     * Reference: http://www.inf.fh-flensburg.de/lang/algorithmen/sortieren/quick/quicken.htm
     * Complexity: O(n*log n) normally, but O(n^2) if the pivot is bad
     *
     * @param	list	is the list to sort.
     * @param	start	is the index to start from
     * @param	stop 	is the index to stop
     * @param	type	is the type of pivot to use
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void sort(SortableDataList list, int start, int stop, int type, boolean insertionSort) {
        if (stop-start <= 15 && insertionSort) {
            SimpleSort.insertionSort(list, start, stop);
        }
        int pivotIndex = pivot(list,start,stop,type);
        Object pivot = list.get(pivotIndex);
        int left=start, right=stop;

        do{
            while (list.compare(left, pivot) < 0) {
                left++;
            }
            while (list.compare(right, pivot) > 0) {
                right--;
            }

            if (left <= right) {
                list.swap(left, right);
                left++;
                right--;
            }
        }while (left <= right);

        if (start < right) {
            sort(list, start, right, type, insertionSort);
        }
        if (left < stop) {
            sort(list, left, stop, type, insertionSort);
        }

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private static int pivot(SortableDataList<?> list, int start, int stop,int type) {
        switch(type) {
            case RANDOM_PIVOT:
                return start+(int)(Math.random()*(stop-start));
            case MEDIAN_PIVOT:
                Comparable[] i = new Comparable[]{
                        (Comparable)list.get(0),
                        (Comparable)list.get(list.size()/2),
                        (Comparable)list.get(list.size()-1)};
                SimpleSort.insertionSort(new SortableComparableArray(i));
                if (i[i.length/2].compareTo(list.get(start)) == 0)
                    return start;
                else if (i[i.length/2].compareTo(list.get(stop)) == 0)
                    return stop;
                else
                    return start+(stop-start)/2;
            case MIDDLE_PIVOT:
                return (start+stop)/2;
        }
        return 0;
    }
}
