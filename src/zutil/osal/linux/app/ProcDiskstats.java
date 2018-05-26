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

package zutil.osal.linux.app;

import zutil.StringUtil;
import zutil.Timer;
import zutil.log.LogUtil;
import zutil.net.ThroughputCalculator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Documentation from https://www.kernel.org/doc/Documentation/block/stat.txt
 *
 * Created by Ziver on 2015-05-19.
 */
public class ProcDiskstats {
    private static final Logger log = LogUtil.getLogger();
    private static final String PROC_PATH = "/proc/diskstats";
    private static final int TTL = 500; // update stats every 0.5 second

    private static HashMap<String, HddStats> hdds = new HashMap<>();
    private static Timer updateTimer = new Timer(TTL);


    private synchronized static void update(){
        if(!updateTimer.hasTimedOut())
            return;

        try {
            BufferedReader in = new BufferedReader(new FileReader(PROC_PATH));
            parse(in);
            in.close();
        } catch (IOException e) {
            log.log(Level.SEVERE, null, e);
        }
    }
    protected static void parse(BufferedReader in) throws IOException {
        updateTimer.start();
        String line;
        while((line=in.readLine()) != null){
            String[] str = line.trim().split("\\s+", 4);
            if(str.length >= 4) {
                String devName = str[2];
                if(!hdds.containsKey(devName)){
                    HddStats hdd = new HddStats(devName);
                    hdds.put(hdd.getDevName(), hdd);
                }
                hdds.get(devName).update(str[3]);
            }
        }
    }



    public static HddStats getStats(String devName){
        update();
        return hdds.get(devName);
    }


    public static class HddStats {
        private String devName;
        //read I/Os       requests      number of read I/Os processed
        private long readIO = -1;
        //read merges     requests      number of read I/Os merged with in-queue I/O
        private long readMerges = -1;
        //read sectors    sectors       number of sectors read
        private long readSectors = -1;
        //read ticks      milliseconds  total wait time for read requests
        private long readTicks = -1;
        //write I/Os      requests      number of write I/Os processed
        private long writeIO = -1;
        //write merges    requests      number of write I/Os merged with in-queue I/O
        private long writeMerges = -1;
        //write sectors   sectors       number of sectors written
        private long writeSectors = -1;
        //write ticks     milliseconds  total wait time for write requests
        private long writeTicks = -1;
        //in_flight       requests      number of I/Os currently in flight
        private long inFlight = -1;
        //io_ticks        milliseconds  total time this block device has been active
        private long ioTicks = -1;
        //time_in_queue   milliseconds  total wait time for all requests
        private long timeInQueue = -1;

        private ThroughputCalculator readThroughput;
        private ThroughputCalculator writeThroughput;


        protected HddStats(String devName) {
            this.devName = devName;
            readThroughput = new ThroughputCalculator();
            writeThroughput = new ThroughputCalculator();
        }
        protected void update(String line){
            String[] stats = line.split("\\s+");
            if(stats.length >= 11){
                readIO =       Long.parseLong(stats[0]);
                readMerges =   Long.parseLong(stats[1]);
                readSectors =  Long.parseLong(stats[2]);
                readTicks =    Long.parseLong(stats[3]);
                writeIO =      Long.parseLong(stats[4]);
                writeMerges =  Long.parseLong(stats[5]);
                writeSectors = Long.parseLong(stats[6]);
                writeTicks =   Long.parseLong(stats[7]);
                inFlight =     Long.parseLong(stats[8]);
                ioTicks =      Long.parseLong(stats[9]);
                timeInQueue =  Long.parseLong(stats[10]);

                readThroughput.setTotalHandledData(readSectors * 512);
                writeThroughput.setTotalHandledData(writeSectors * 512);
            }
        }


        public String getDevName() {
            return devName;
        }
        /**
         * This values increment when an I/O request completes.
         */
        public long getReadIO() {
            return readIO;
        }
        /**
         * This value increment when an I/O request is merged with an
         * already-queued I/O request.
         */
        public long getReadMerges() {
            return readMerges;
        }
        /**
         * This value count the number of sectors read from to this
         * block device.  The "sectors" in question are the standard UNIX 512-byte
         * sectors, not any device- or filesystem-specific block size.  The
         * counter is incremented when the I/O completes.
         */
        public long getReadSectors() {
            return readSectors;
        }
        /**
         * This value count the number of milliseconds that I/O requests have
         * waited on this block device.  If there are multiple I/O requests waiting,
         * this value will increase at a rate greater than 1000/second; for
         * example, if 60 read requests wait for an average of 30 ms, the read_ticks
         * field will increase by 60*30 = 1800.
         */
        public long getReadTicks() {
            return readTicks;
        }
        /**
         * This values increment when an I/O request completes.
         */
        public long getWriteIO() {
            return writeIO;
        }
        /**
         * This value increment when an I/O request is merged with an
         * already-queued I/O request.
         */
        public long getWriteMerges() {
            return writeMerges;
        }
        /**
         * This value count the number of sectors written to this
         * block device.  The "sectors" in question are the standard UNIX 512-byte
         * sectors, not any device- or filesystem-specific block size.  The
         * counter is incremented when the I/O completes.
         */
        public long getWriteSectors() {
            return writeSectors;
        }
        /**
         * This value count the number of milliseconds that I/O requests have
         * waited on this block device.  If there are multiple I/O requests waiting,
         * this value will increase at a rate greater than 1000/second; for
         * example, if 60 write requests wait for an average of 30 ms, the write_ticks
         * field will increase by 60*30 = 1800.
         */
        public long getWriteTicks() {
            return writeTicks;
        }
        /**
         * This value counts the number of I/O requests that have been issued to
         * the device driver but have not yet completed.  It does not include I/O
         * requests that are in the queue but not yet issued to the device driver.
         */
        public long getInFlight() {
            return inFlight;
        }
        /**
         * This value counts the number of milliseconds during which the device has
         * had I/O requests queued.
         */
        public long getIoTicks() {
            return ioTicks;
        }
        /**
         * This value counts the number of milliseconds that I/O requests have waited
         * on this block device.  If there are multiple I/O requests waiting, this
         * value will increase as the product of the number of milliseconds times the
         * number of requests waiting (see "read ticks" above for an example).
         */
        public long getTimeInQueue() {
            return timeInQueue;
        }

        /**
         * @return the average byte/second read throughput from the disk
         */
        public double getReadThroughput(){
            return readThroughput.getByteThroughput();
        }
        /**
         * @return the average byte/second write throughput to the disk
         */
        public double getWriteThroughput(){
            return writeThroughput.getByteThroughput();
        }
    }


    public static void main(String[] args){
        while(true){
            HddStats hdd = ProcDiskstats.getStats("sda");
            System.out.println("sda= " +
                    "read: "  + StringUtil.formatByteSizeToString((long)hdd.getReadThroughput()) + "/s "+
                    "write: " + StringUtil.formatByteSizeToString((long)hdd.getWriteThroughput()) + "/s");
            try{Thread.sleep(1000);}catch (Exception e){}
        }
    }
}
