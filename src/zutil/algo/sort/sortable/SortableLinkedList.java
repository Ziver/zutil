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

package zutil.algo.sort.sortable;

import java.util.LinkedList;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class SortableLinkedList<T> implements SortableDataList<T>{
    private LinkedList<T> list;

    public SortableLinkedList(LinkedList<T> list) {
        this.list = list;
    }

    public T get(int i) {
        return list.get(i);
    }

    public void set(int i, T o) {
        list.set(i, o);
    }

    public int size() {
        return list.size();
    }

    public void swap(int a, int b) {
        T temp = list.get(a);
        list.set(a, list.get(b));
        list.set(b, temp);
    }

    public int compare(int a, int b) {
        Comparable aa = (Comparable)list.get(a);
        Comparable bb = (Comparable)list.get(b);
        return aa.compareTo(bb);
    }

    public int compare(int a, T b) {
        Comparable aa = (Comparable)list.get(a);
        Comparable bb = (Comparable)b;
        return aa.compareTo(bb);
    }



}
