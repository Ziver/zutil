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
        assertEquals(Collections.EMPTY_LIST, CronTimer.getRange(""));
        assertEquals(Collections.EMPTY_LIST, CronTimer.getRange(null));

        // ranges
        //assertEquals(Arrays.asList(0,1), CronTimer.getRange("0-1"));

        // intervals

        // range and interval
    }

}