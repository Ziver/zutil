/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
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
package zutil.test;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Random;

import zutil.struct.BloomFilter;

import junit.framework.TestCase;

/**
 * This code may be used, modified, and redistributed provided that the 
 * author tag below remains intact.
 * 
 * @author Ian Clarke <ian@uprizer.com>
 */

public class BloomFilterTest extends TestCase {
	public void testBloomFilter() {
		DecimalFormat df = new DecimalFormat("0.00000");
		Random r = new Random(124445l);
		int bfSize = 400000;
		System.out.println("Testing " + bfSize + " bit SimpleBloomFilter");
		for (int i = 5; i < 10; i++) {
			int addCount = 10000 * (i + 1);
			BloomFilter<Integer> bf = new BloomFilter<Integer>(bfSize, addCount);
			HashSet<Integer> added = new HashSet<Integer>();
			for (int x = 0; x < addCount; x++) {
				int num = r.nextInt();
				added.add(num);
			}
			bf.addAll(added);
			assertTrue("Assert that there are no false negatives", bf
					.containsAll(added));

			int falsePositives = 0;
			for (int x = 0; x < addCount; x++) {
				int num = r.nextInt();

				// Ensure that this random number hasn't been added already
				if (added.contains(num)) {
					continue;
				}
				
				// If necessary, record a false positive
				if (bf.contains(num)) {
					falsePositives++;
				}
			}
			double expectedFP = bf.falsePosetiveProbability();
			double actualFP = (double) falsePositives / (double) addCount;
			System.out.println("Got " + falsePositives
					+ " false positives out of " + addCount + " added items, rate = "
					+ df.format(actualFP) + ", expected = "
					+ df.format(expectedFP));
			double ratio = expectedFP/actualFP;
			assertTrue(
					"Assert that the actual false positive rate doesn't deviate by more than 10% from what was predicted",
					ratio > 0.9 && ratio < 1.1);
		}
	}


}
