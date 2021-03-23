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

public class AnonymousFunctionBenchmark {
    public static final int TEST_EXECUTIONS = 500;

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();


    private int[] array  = new int[100_000];


    @Test
    public void functionLoop() {
        for (int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < array.length; i++) {
                array[i] = new CalcFunc(){
                    public int calc(int i){
                        return i+1;
                    }
                }.calc(i);
            }
        }
    }

    @Test
    public void preFunctionLoop() {
        CalcFunc func = new CalcFunc(){
            public int calc(int i){
                return i+1;
            }
        };

        for (int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < array.length; i++) {
                array[i] = func.calc(i);
            }
        }
    }

    @Test
    public void rawLoops(){
        for (int k=0; k<TEST_EXECUTIONS; k++) {
            for (int i = 0; i < array.length; i++) {
                array[i] = i;
            }
        }
    }

    private interface CalcFunc{
        int calc(int i);
    }
}