package zutil.osal.linux.app;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BtrfsTest {

    @Test
    public void testGetScrubProgress() {
    }

    //@Test
    public void testGetLastScrubDate() throws IOException, ParseException {
        final String[] cmd = {null};

        Btrfs btrfs = new Btrfs("/dev/sda1") {
            public String execBtrfsCommand(String params, String path) throws IOException {
                cmd[0] = params + " " + path;

                return "scrub status for 3a6b2a74-05a9-464d-bbbd-15710c548f76\n" +
                        "        scrub started at Fri Feb  4 00:29:54 2022 and finished after 58380 seconds\n" +
                        "        total bytes scrubbed: 15.74TiB with 0 errors\n";
            }
        };

        Date actualDate = btrfs.getLastScrubDate();
        Date expectedDate = new SimpleDateFormat("dow mon dd hh:mm:ss zzz yyyy").parse("Fri Feb  4 00:29:54 2022");

        assertEquals("scrub status /dev/sda1", cmd[0]);
        assertTrue("Dates are not close enough to each other!",
                Math.abs(expectedDate.getTime() - actualDate.getTime()) < 1000);
    }
}