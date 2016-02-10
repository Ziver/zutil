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

package zutil.test;

import com.carrotsearch.junitbenchmarks.BenchmarkRule;
import org.junit.Rule;
import org.junit.Test;
import zutil.test.JSONSerializerTest.TestClass;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class JSONSerializerBenchmark {
    private static final int TEST_EXECUTIONS = 2000;

    @Rule
    public BenchmarkRule benchmarkRun = new BenchmarkRule();

    @Test
    public void testJavaLegacySerialize() throws InterruptedException, IOException, ClassNotFoundException{
        for(int i=0; i<TEST_EXECUTIONS; i++){
            TestClass sourceObj = new TestClass().init();

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(sourceObj);
            out.flush();
            out.close();
            byte[] data = bout.toByteArray();

            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            ObjectInputStream in = new ObjectInputStream(bin);
            TestClass targetObj = (TestClass) in.readObject();
            in.close();

            assertEquals( sourceObj, targetObj );
        }
    }

    @Test
    public void testJavaJSONSerialize() throws InterruptedException, IOException, ClassNotFoundException{
        for(int i=0; i<TEST_EXECUTIONS; i++){
            TestClass sourceObj = new TestClass().init();

            TestClass targetObj = JSONSerializerTest.sendReceiveObject(sourceObj);

            assertEquals( sourceObj, targetObj );
        }
    }

    @Test
    public void testOutputJavaLegacySerialize() throws InterruptedException, IOException, ClassNotFoundException{
        for(int i=0; i<TEST_EXECUTIONS; i++){
            TestClass sourceObj = new TestClass().init();

            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bout);
            out.writeObject(sourceObj);
            out.flush();
            out.close();
            byte[] data = bout.toByteArray();
        }
    }

    @Test
    public void testOutputJavaJSONSerialize() throws InterruptedException, IOException, ClassNotFoundException{
        for(int i=0; i<TEST_EXECUTIONS; i++){
            TestClass sourceObj = new TestClass().init();

            String targetObj = JSONSerializerTest.writeObjectToJson(sourceObj);
        }
    }

    @Test
    public void testInputJavaLegacySerialize() throws InterruptedException, IOException, ClassNotFoundException{
        TestClass sourceObj = new TestClass().init();

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bout);
        out.writeObject(sourceObj);
        out.flush();
        out.close();
        byte[] data = bout.toByteArray();

        for(int i=0; i<TEST_EXECUTIONS; i++){
            ByteArrayInputStream bin = new ByteArrayInputStream(data);
            ObjectInputStream in = new ObjectInputStream(bin);
            TestClass targetObj = (TestClass) in.readObject();
            in.close();
        }
    }

    @Test
    public void testInputJavaJSONSerialize() throws InterruptedException, IOException, ClassNotFoundException{
        TestClass sourceObj = new TestClass().init();
        String sourceStr = JSONSerializerTest.writeObjectToJson(sourceObj);

        for(int i=0; i<TEST_EXECUTIONS; i++){
            TestClass targetObj = JSONSerializerTest.readObjectFromJson(sourceStr);
        }
    }
}
