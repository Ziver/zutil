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

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 *
 */
public class CronTimerTest {

    @Test
    public void getRange() {
        // invalid numbers
        assertEquals(Collections.EMPTY_LIST, CronTimer.getRange("", 0,60));
        assertEquals(Collections.EMPTY_LIST, CronTimer.getRange(null, 0,60));

        // individual numbers
        assertEquals(Collections.singletonList(55), CronTimer.getRange("55", 0,60));
        assertEquals(Arrays.asList(5,10,15), CronTimer.getRange("5,10,15", 0, 60));

        // ranges
        assertEquals(Arrays.asList(0,1), CronTimer.getRange("0-1", 0,60));
        assertEquals(Arrays.asList(5,6,7,8,9,10), CronTimer.getRange("5-10", 0,60));

        // intervals
        assertEquals(Collections.emptyList(), CronTimer.getRange("15/10", 0,60));
        assertEquals(Arrays.asList(0,10,20,30,40,50,60), CronTimer.getRange("0-60/10", 0,60));

        // wildcards
        assertEquals(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10), CronTimer.getRange("*", 0,10));
        assertEquals(Arrays.asList(5,6,7), CronTimer.getRange("*", 5,7));
        assertEquals(Arrays.asList(0,10,20,30,40,50,60), CronTimer.getRange("*/10", 0,60));

        // Illegal numbers
        try{
            CronTimer.getRange("50", 1,12);
            fail("Did not receive Exception");
        } catch (IllegalArgumentException e) {e.printStackTrace();} // We expect exception
        try{
            CronTimer.getRange("0", 1,12);
            fail("Did not receive Exception");
        } catch (IllegalArgumentException e) {e.printStackTrace();} // We expect exception
        try{
            CronTimer.getRange("1-50", 1,12);
            fail("Did not receive Exception");
        } catch (IllegalArgumentException e) {e.printStackTrace();} // We expect exception
        try{
            CronTimer.getRange("0-5", 3,12);
            fail("Did not receive Exception");
        } catch (IllegalArgumentException e) {e.printStackTrace();} // We expect exception
    }

    @Test
    public void specificTime() {
        CronTimer cron = getCronTimer("59 23 31 12 3 2003");
        assertEquals(-1, (long) cron.next());
        assertEquals(-1, (long) cron.next(1072915140000L));
        assertEquals(1072915140000L, (long) cron.next(978307140000L));
    }

    @Test
    public void minuteWildcard() {
        CronTimer cron = getCronTimer("0 * * * * *");
        assertEquals(1041382800000L, (long)cron.next(1041379200000L));
        assertEquals(1041382800000L, (long)cron.next(1041379260000L));
    }

    @Test
    public void hourWildcard() {
        CronTimer cron = getCronTimer("* 0 * * * *");
        assertEquals(1041379260000L, (long)cron.next(1041379200000L));
        assertEquals(1041379320000L, (long)cron.next(1041379260000L)); // minute change
        assertEquals(1041465600000L, (long)cron.next(1041382790000L)); // minute border
        assertEquals(1041465600000L, (long)cron.next(1041382800000L)); // hour change
    }

    @Test
    public void dayWildcard() {
        CronTimer cron = getCronTimer("* * 1 * * *");
        assertEquals(1041379260000L, (long)cron.next(1041379200000L));
        assertEquals(1041379320000L, (long)cron.next(1041379260000L)); // minute change
        assertEquals(1044057600000L, (long)cron.next(1041465600000L)); // day change
    }

    @Test
    public void monthWildcard() {
        CronTimer cron = getCronTimer("* * * 1 * *");
        assertEquals(1041379260000L, (long)cron.next(1041379200000L));
        assertEquals(1041382860000L, (long)cron.next(1041382800000L)); // hour change
        assertEquals(1041469260000L, (long)cron.next(1041469200000L)); // day change
        assertEquals(1072915200000L, (long)cron.next(1044057600000L)); // month change
    }

    @Test
    public void weekDayWildcard() {
        CronTimer cron = getCronTimer("* * * * 3 *");
        assertEquals(1041379260000L, (long)cron.next(1041379200000L));
        assertEquals(1041984000000L, (long)cron.next(1041465600000L)); // day change
    }

    @Test
    public void yearWildcard() {
        CronTimer cron = getCronTimer("* * * * * 2003");
        assertEquals(1041379260000L, (long)cron.next(1041379200000L));
        assertEquals(1041379320000L, (long)cron.next(1041379260000L)); // min change
        assertEquals(1041382980000L, (long)cron.next(1041382920000L)); // hour change
        assertEquals(1041469440000L, (long)cron.next(1041469380000L)); // day change
        assertEquals(1044147900000L, (long)cron.next(1044147840000L)); // month change
        assertEquals(-1, (long)cron.next(1075683660000L)); // year change
    }



    private static CronTimer getCronTimer(String cron) {
        CronTimer timer = new CronTimer(cron);
        timer.setTimeZone(TimeZone.getTimeZone("UTC"));
        return timer;
    }
}
