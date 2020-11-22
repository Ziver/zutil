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

package zutil.log;

import zutil.ClassUtil;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is used for performance measurements on an application.
 * The counter values will be published in JMX and can be viewed in JConsole.
 */
public class CounterManager {
    /** a two dimensional map [Class][Counter ID] for storing singleton objects **/
    private static HashMap<String, HashMap<String, Counter>> counters = new HashMap<>();

    /**
     * @param   name    a unique name/id of the counter
     * @return a singleton instance of the counter name with the calling class as context. Will always return a valid object.
     */
    public static Counter getCounter(String name) {
        return getCounter(ClassUtil.getCallingClass(CounterManager.class), name);
    }
    /**
     * @param   clazz   the counter context
     * @param   name    a unique name/id of the counter
     * @return a singleton instance of the counter name. Will always return a valid object.
     */
    public static Counter getCounter(Class clazz, String name) {
        return getCounter(clazz.getName(), name);
    }
    private static synchronized Counter getCounter(String clazz, String name) {
        Counter counter;
        if ( ! counters.containsKey(clazz) || ! counters.get(clazz).containsKey(name)) {
            // Get the platform MBeanServer
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            // Unique identification of MBeans
            counter = new Counter(name);

            try {
                // Uniquely identify the MBeans and register them with the platform MBeanServer
                ObjectName objectName = new ObjectName(clazz + ":name=" + counter.getName());
                mbs.registerMBean(counter, objectName);
                // Register the singleton
                if ( ! counters.containsKey(clazz))
                    counters.put(clazz, new HashMap<>());
                counters.get(clazz).put(name, counter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
            counter = counters.get(clazz).get(name);
        return counter;
    }


    /**
     * MBean Counter definition for publishing data in JMX
     */
    public interface CounterMBean {
        String getName();

        int getValue();
        int getMax();
        int getMin();
        double getAverage();
    }


    public static class Counter implements CounterMBean{
        private final String name;
        private int max;
        private int min;
        private double average;
        private int sampleCount;
        private AtomicInteger counter = new AtomicInteger();


        protected Counter(String name){
            this.name = name;
        }


        public void add(int i){
            int prev = counter.getAndAdd(i);
            updateMetaData(prev + i);
        }
        public void set(int i){
            counter.getAndSet(i);
            updateMetaData(i);
        }
        public void increment(){
            int i = counter.incrementAndGet();
            updateMetaData(i);
        }
        public void decrement(){
            int i = counter.decrementAndGet();
            updateMetaData(i);
        }
        private void updateMetaData(int i){
            if (max < i)
                max = i;
            if (min > i)
                min = i;

            average = (average*sampleCount + i) / ++sampleCount;
        }


        @Override
        public String getName() {
            return name;
        }
        /**
         * @return current value of the counter
         */
        @Override
        public int getValue(){
            return counter.intValue();
        }
        /**
         * @return the maximum registered value over the lifetime of the counter
         */
        @Override
        public int getMax() {
            return max;
        }
        /**
         * @return the minimum registered value over the lifetime of the counter
         */
        @Override
        public int getMin() {
            return min;
        }
        /**
         * @return the average value of the counter
         */
        @Override
        public double getAverage() {
            return average;
        }
    }
}
