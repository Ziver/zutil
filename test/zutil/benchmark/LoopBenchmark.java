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

package zutil.benchmark;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Rule;
import org.junit.Test;

public class LoopBenchmark {
    public static final int TEST_EXECUTIONS = 500;

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();


    private int[] array1 = new int[100_000];
    private int[] array2 = new int[50_000];


    @Test
    public void writeArrayOneLoop() {
        for (int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < Math.max(array1.length, array1.length); i++) {
                if (i < array1.length)
                    array1[i] = i;
                if (i < array2.length)
                    array2[i] = i;
            }
        }
    }

    @Test
    public void writeArraySeparateLoops(){
        for (int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < array1.length; i++) {
                array1[i] = i;
            }
            for (int j = 0; j < array2.length; j++) {
                array2[j] = j;
            }
        }
    }


    @Test
    public void readArrayLoop() {
        int sum = 0;
        for (int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < array1.length; i++) {
                sum += array1[i];
            }
        }
    }

    @Test
    public void readArrayForeach() {
        int sum = 0;
        for (int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i : array1) {
                sum += array1[i];
            }
        }
    }
}