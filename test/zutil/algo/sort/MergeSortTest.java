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

import zutil.algo.sort.sortable.SortableIntArray;

// TODO: Convert to JUnit
@SuppressWarnings("unused")
public class MergeSortTest {
    public static final int SIZE = 10000;
    public static final int MAX_INT = 10000;

    public static void main(String[] args) {
        int[] array = new int[SIZE];

        for(int i=0; i<array.length ;i++) {
            array[i] = (int)(Math.random()*MAX_INT);
        }

        for(int i=0; i<array.length ;i++) {
            System.out.print(array[i]+", ");
        }

        long time = System.currentTimeMillis();
        //SimpleSort.bubbleSort( new SortableIntArray(array) );
        //SimpleSort.selectionSort( new SortableIntArray(array) );
        //SimpleSort.insertionSort( new SortableIntArray(array) );
        //QuickSort.sort( new SortableIntArray(array) );
        //MergeSort.sort( array );
        MergeSort.sort( new SortableIntArray(array) );
        time = System.currentTimeMillis() - time;

        System.out.println("\n--------------------------------------------");
        System.out.print(array[0] + ", ");
        int error = -1;
        for(int i=1; i<array.length; i++) {
            System.out.print(array[i] + ", ");
            if (array[i-1] > array[i]) {
                error = i;
            }
        }

        if (error >= 0) {
            System.out.println("\nArray not sorted!! (" + array[error-1] + " > " + array[error] + ")");
        }
        System.out.println("\nTime: " + time + " ms");
    }
}
