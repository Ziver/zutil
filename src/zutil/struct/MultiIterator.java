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

package zutil.struct;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * This class is a wrapper that combine multiple Iterators of same type.
 * This class will start by iterating through the first Iterator until the
 * end of it has been reach and then the next iterator will be used until
 * all iterators no longer has any items.
 */
public class MultiIterator<T> implements Iterator<T> {
    private Queue<Iterator<T>> queue = new LinkedList<>();


    public void addIterator(Iterator<T> it) {
        queue.add(it);
    }


    @Override
    public boolean hasNext() {
        if (queue.isEmpty())
            return false;
        else {
            if (!queue.peek().hasNext()) {
                queue.poll();
                return hasNext();
            }

            return true;
        }
    }

    @Override
    public T next() {
        if (!hasNext())
            throw new NoSuchElementException("End of iterator has been reached.");
        else {
            return queue.peek().next();
        }
    }

    @Override
    public void remove() {
        if (queue.isEmpty())
            return;

        queue.peek().remove();
    }
}
