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

import zutil.algo.sort.sortable.SortableIntArray;

import java.util.Arrays;


/**
 * TODO: Convert to JUnit
 */
public class QuickSelectTest {
    public static void main(String[] args) {
        int[] array = {1,3,4,6,3,2,98,5,7,8,543,2,4,5,8,9,5,2,3,5,7,5,3,2,6,8,5,324,8,6};
        //int[] array = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,17,18,19,20};

        long time = System.currentTimeMillis();
        int median = (Integer)QuickSelect.find(new SortableIntArray(array), array.length/2);
        System.out.println("QuickSelection("+(System.currentTimeMillis()-time)+"ms): "+median);

        time = System.currentTimeMillis();
        Arrays.sort(array);
        System.out.println("RightAnswer("+(System.currentTimeMillis()-time)+"ms): "+array[array.length/2]);

        System.out.println("Sorted Array("+array.length+"): ");
        for(int i=0; i<array.length ;i++) {
            System.out.println(array[i] +",");
        }
    }
}
