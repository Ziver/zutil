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

package zutil;

import java.io.Serializable;

/**
 * This class is a timer, it will track time and
 * timeout after a specific amount of time.
 * <br>
 * Note that the {@link #start()} method needs to be called for the timer to start.
 */
public class Timer implements Serializable {
    /** The timeout period **/
    private long period;
    /** The timestamp when the timer was started, -1 if timer has not been started or has been reset **/
    private long timestamp;


    /**
     * Create a new timer with no timeout period configured.
     */
    public Timer() {}

    /**
     * Create a new timer that will timeout in a specified amount of time from now.
     *
     * @param millisecond is the period in milliseconds that the timeout should happen in.
     */
    public Timer(long millisecond) {
        this.period = millisecond;
        reset();
    }


    /**
     * Sets a new timeout period. Note that this will modify the timeout of a already started timer.
     *
     * @param millisecond is the new period of timeout in milliseconds.
     * @return a reference of itself
     */
    public Timer setPeriod(long millisecond) {
        this.period = millisecond;
        return this;
    }

    /**
     * Will start or restart the timer if it is already running
     *
     * @return a reference of itself
     */
    public Timer start() {
        timestamp = System.currentTimeMillis();
        return this;
    }

    /**
     * Will reset the timer so that {@link #hasTimedOut()} returns true
     *
     * @return a reference of itself
     */
    public Timer reset() {
        timestamp = -1;
        return this;
    }

    /**
     * @return true if the timer has timed out or has been reset, false if timer is running
     */
    public boolean hasTimedOut() {
        return timestamp + period < System.currentTimeMillis();
    }

    /**
     * @return the timestamp in the future based on epoc in milliseconds where the timer will timeout or -1 if the timer has already timeout.
     */
    public long getTimeoutTimeMillis() {
        if (hasTimedOut())
            return -1;
        return timestamp + period;
    }

    public String toString() {
        if (hasTimedOut())
            return "Timed out";
        else
            return "Timeout in " +StringUtil.formatTimeToString((timestamp+period)-System.currentTimeMillis());
    }
}
