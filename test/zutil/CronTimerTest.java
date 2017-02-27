package zutil;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 *
 */
public class CronTimerTest {

    @Test
    public void getRange() throws Exception {
        // invalid numbers
        assertEquals(Collections.EMPTY_LIST, CronTimer.getRange("", 0,60));
        assertEquals(Collections.EMPTY_LIST, CronTimer.getRange(null, 0,60));

        // individual numbers
        assertEquals(Arrays.asList(55), CronTimer.getRange("55", 0,60));
        assertEquals(Arrays.asList(5,10,15), CronTimer.getRange("5,10,15", 0, 60));

        // ranges
        assertEquals(Arrays.asList(0,1), CronTimer.getRange("0-1", 0,60));
        assertEquals(Arrays.asList(5,6,7,8,9,10), CronTimer.getRange("5-10", 0,60));

        // intervals
        assertEquals(Arrays.asList(), CronTimer.getRange("15/10", 0,60));
        assertEquals(Arrays.asList(0,10,20,30,40,50,60), CronTimer.getRange("0-60/10", 0,60));

        // wildcards
        assertEquals(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10), CronTimer.getRange("*", 0,10));
        assertEquals(Arrays.asList(5,6,7), CronTimer.getRange("*", 5,7));
        assertEquals(Arrays.asList(0,10,20,30,40,50,60), CronTimer.getRange("*/10", 0,60));
    }

    @Test
    public void specificTime() {
        CronTimer cron = new CronTimer("59 23 31 12 5 2003");
        assertEquals(-1, (long) cron.next());
        assertEquals(1072911540000L, (long) cron.next(1072911540000L));
        assertEquals(1072911540000L, (long) cron.next(978307140000L));
    }
    @Test
    public void minuteWildcard(){
        CronTimer cron = new CronTimer("00 * * * * *");
        assertEquals(1041411600000L, (long)cron.next(1041411600000L));
        assertEquals(1041415200000L, (long)cron.next(1041411660000L));
    }

}