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
import zutil.StringUtil;

import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Created by Ziver on 2016-07-07.
 */
public class StringSplitBenchmark {
    public static final int TEST_EXECUTIONS = 200000;

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();

    private static String delimiter = ",";
    private static String str = "aaa,aaaaaa,aaaaaa,aa,a,a,a,bb,bbb,aaaaaaaaa,a,a,aaa";


    @Test
    public void stringSplit(){
        for (int i=0; i<TEST_EXECUTIONS; i++)
            assertSplit(str.split(delimiter));
    }

    public static Pattern pattern = Pattern.compile(delimiter);
    @Test
    public void patternSplit(){
        for (int i=0; i<TEST_EXECUTIONS; i++)
            assertSplit(pattern.split(str));
    }

    @Test
    public void substring(){
        for (int i=0; i<TEST_EXECUTIONS; i++) {
            List<String> splitList = StringUtil.split(str, delimiter.charAt(0));
            assertSplit(splitList.toArray(new String[0]));
        }
    }

    public void assertSplit(String[] str){
        assertEquals(13,str.length);
    }
}
