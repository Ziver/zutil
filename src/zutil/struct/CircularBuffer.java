/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
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
import java.util.NoSuchElementException;

/**
 * This class is a first in first out circular buffer with a fixed size.
 * If the size is exceed then the oldest item will be removed.
 *
 * Created by Ziver on 2015-09-22.
 */
public class CircularBuffer<T> implements Iterable<T>{

    private Object[] buffer;
    private int buffSize;
    private int buffPos;
    private long addCount;

    /**
     * Initiates the buffer with a maximum size of maxSize.
     */
    public CircularBuffer(int maxSize){
        buffer = new Object[maxSize];
        buffSize = 0;
        buffPos  = 0;
    }

    public void add(T obj){
        if(buffPos+1 >= buffer.length)
            buffPos = 0;
        else
            ++buffPos;
        if(buffSize < buffer.length)
            ++buffSize;
        buffer[buffPos] = obj;
        ++addCount;
    }

    public T get(int index) {
        if(index >= buffSize)
            throw new IndexOutOfBoundsException("Index "+ index +" is larger than actual buffer size "+ buffSize);
        int buffIndex = buffPos - index;
        if(buffIndex < 0)
            buffIndex = buffer.length - Math.abs(buffIndex);
        return (T)buffer[buffIndex];
    }

    public int size() {
        return buffSize;
    }

    /**
     * @return the total amount of insertions into the buffer, this value only increments and will never decrease.
     */
    public long getInsertionCount() {
        return addCount;
    }

    @Override
    public Iterator<T> iterator() {
        return new CircularBufferIterator();
    }


    protected class CircularBufferIterator implements Iterator<T> {
        private int iteratorPos = 0;

        @Override
        public boolean hasNext() {
            return iteratorPos < buffSize;
        }

        @Override
        public T next() {
            if(iteratorPos >= buffSize)
                throw new NoSuchElementException();
            return get(iteratorPos++);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException ();
        }
    }
}
