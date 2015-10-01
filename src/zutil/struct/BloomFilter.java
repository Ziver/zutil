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

import zutil.Hasher;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * A implementation of a bloom filter
 * @author Ziver
 *
 */
public class BloomFilter<T extends Serializable> implements Set<T>, Serializable{
	private static final long serialVersionUID = 1L;
	
	private BitSet bits;
	private int content_size;
	private int optimal_size;
	private int k;

	
	/**
	 * Creates a bloom filter
	 * 
	 * @param size The amount of bits in the filter
	 * @param expected_data_count The estimated amount of data to 
	 * 			be inserted(a bigger number is better than a smaller)
	 */
	public BloomFilter(int size, int expected_data_count){
		bits = new BitSet(size);
		k = (int)((size/expected_data_count) * Math.log(2));
		content_size = 0;
		optimal_size = expected_data_count;
	}

	/**
	 * @param e A Serializable object
	 * @return If the optimal size has been reached
	 */
	public boolean add(T e) {
		try {
			content_size++;
			int hash = 0;
			for(int i=0; i<k ;i++){
				hash = Hasher.MurmurHash(e, hash);
				hash = Math.abs(hash) % bits.size();
				bits.set(hash, true);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return isFull();
	}

	/**
	 * Adds a collection to the bloom filter
	 * 
	 * @return If the optimal size has been reached
	 */
	public boolean addAll(Collection<? extends T> c) {
		for(T t : c){
			add(t);
		}
		return isFull();
	}

	/**
	 * @return clears the filter
	 */
	public void clear() {
		content_size = 0;
		bits.clear();
	}

	/**
	 * @param o is the Serializable object to search for
	 * @return If the object contains in the filter or 
	 * 			false if the Object is not Serializable
	 */
	public boolean contains(Object o) {
		try {
			if(!(o instanceof Serializable))return false;
			int hash = 0;
			for(int i=0; i<k ;i++){
				hash = Hasher.MurmurHash((Serializable)o, hash);
				hash = Math.abs(hash) % bits.size();
				if(!bits.get(hash))
					return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}

	/**
	 * Checks if the whole collection contains in the filter
	 * 
	 * @param c The collection
	 */
	public boolean containsAll(Collection<?> c) {
		for(Object o : c){
			if(!contains(o)) return false;
		}
		return true;
	}

	/**
	 * @return If the bloom filter is empty
	 */
	public boolean isEmpty() {
		return content_size == 0;
	}
	
	/**
	 * @return If the optimal size has been reached
	 */
	public boolean isFull() {
		return content_size > optimal_size;
	}
	
	/**
	 * @return The number of data added
	 */
	public int size() {
		return content_size;
	}
	
	/**
	 * @return The false positive probability of the current state of the filter
	 */
	public double falsePosetiveProbability(){
		return Math.pow(0.6185, bits.size()/content_size);
	}
	
	/**
	 * Set the hash count. Should be set before adding elements 
	 * or the already added elements will be lost
	 * 
	 * @param k The hash count
	 */
	public void setHashCount(int k){
		this.k = k;
	}
	
	//*********************************************************************
	//*********************************************************************
	public Object[] toArray() {
		throw new UnsupportedOperationException();  
	}

	@SuppressWarnings("hiding")
	public <T> T[] toArray(T[] a) {
		throw new UnsupportedOperationException();  
	}

	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();  
	}

	public boolean remove(Object o) {
		throw new UnsupportedOperationException();  
	}

	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException();  
	}
	
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException();  
	}
}
