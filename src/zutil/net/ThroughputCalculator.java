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

package zutil.net;

/**
 * Created by Ziver Koc
 */
public class ThroughputCalculator {
    public static final float UPDATES_PER_SEC = 1;
    public static final double NANOSEC_PER_SECOND = 1000000000.0;

    private boolean updated;
    private double throughput;
    private long previousTimeStamp;
    private long data_amount;
    private long total_data_amount;
    private float frequency = UPDATES_PER_SEC;

    public void setTotalHandledData(long bytes){
        setHandledData(bytes - total_data_amount);
        total_data_amount = bytes;
    }
    public void setHandledData(long bytes){
        long currentTimeStamp = System.nanoTime();
        data_amount += bytes;
        if(currentTimeStamp - (NANOSEC_PER_SECOND/frequency) > previousTimeStamp) {
            throughput = data_amount / ((currentTimeStamp - previousTimeStamp) / NANOSEC_PER_SECOND);
            previousTimeStamp = currentTimeStamp;
            data_amount = 0;
            updated = true;
        }
    }

    public double getByteThroughput(){
        setHandledData(0); // Update throughput
        updated = false;
        return throughput;
    }
    public double getBitThroughput(){
        return getByteThroughput()*8;
    }

    public boolean isUpdated(){
        return updated;
    }

    public void setFrequency(float frequency) {
        if(frequency < 0)
            this.frequency = UPDATES_PER_SEC;
        else
            this.frequency = frequency;
    }
}
