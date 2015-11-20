package zutil.test;

import org.junit.Test;
import zutil.struct.TimedHashSet;

import static org.junit.Assert.*;

/**
 * Created by Ziver on 2015-11-20.
 */
public class TimedHashSetTest {
    public static final String ENTRY = "key";

    @Test
    public void zeroTTL() throws InterruptedException {
        TimedHashSet set = new TimedHashSet(0);
        set.add(ENTRY);
        Thread.sleep(1);
        assertFalse(set.contains(ENTRY));
    }

    @Test
    public void tenMsTTL() throws InterruptedException {
        TimedHashSet set = new TimedHashSet(10);
        set.add(ENTRY);
        Thread.sleep(1);
        assert(set.contains(ENTRY));
        Thread.sleep(10);
        assertFalse(set.contains(ENTRY));
    }

    @Test
    public void oneSecTTL() throws InterruptedException {
        TimedHashSet set = new TimedHashSet(1000);
        set.add(ENTRY);
        Thread.sleep(1);
        assert(set.contains(ENTRY));
    }
}