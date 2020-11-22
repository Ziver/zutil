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

package zutil.osal.linux.app;

import zutil.Timer;
import zutil.log.LogUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Documentation from https://www.kernel.org/doc/Documentation/block/stat.txt
 *
 * Created by Ziver on 2015-05-19.
 */
public class ProcStat {
    private static final Logger log = LogUtil.getLogger();
    private static final String PROC_PATH = "/proc/stat";
    private static final int TTL = 500; // update stats every 0.5 second

    private static CpuStats cpuTotal = new CpuStats();
    private static ArrayList<CpuStats> cpus = new ArrayList<>();
    private static long uptime;
    private static long processes;
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
            String[] str = line.split("\\s+");
            if(str[0].equals("cpu")) {
                cpuTotal.update(str);
            }
            else if(str[0].startsWith("cpu")){
                int cpuId = Integer.parseInt(str[0].substring(3));
                if(cpus.size() <= cpuId)
                    cpus.add(new CpuStats());
                cpus.get(cpuId).update(str);
            }
            else if(str[0].startsWith("btime")){
                uptime = Long.parseLong(str[1]);
            }
            else if(str[0].startsWith("processes")){
                processes = Long.parseLong(str[1]);
            }
        }
    }



    public static CpuStats getTotalCpuStats(){
        update();
        return cpuTotal;
    }
    public static Iterator<CpuStats> getCpuStats(){
        update();
        return cpus.iterator();
    }
    /**
     * @return the time at which the system booted, in seconds since the Unix epoch.
     */
    public static long getUptime(){
        update();
        return uptime;
    }
    /**
     * @return the number of processes and threads created, which includes (but is not limited to) those created by calls to the fork() and clone() system calls.
     */
    public static long getProcesses(){
        update();
        return processes;
    }



    public static class CpuStats {
        // normal processes executing in user mode
        private long user;
        // processes executing in kernel mode
        private long system;
        // twiddling thumbs
        private long idle;
        // waiting for I/O to complete
        private long iowait;
        private long steal;
        // virtual processes
        private long guest;
        private long total;

        // Percentage
        private float load_total;
        private float load_user;
        private float load_system;
        private float load_iowait;
        private float load_virtual;

        protected CpuStats(){}
        protected void update(String[] stats){
            long newUser, newNice, newSystem, newIdle, newIowait, newIrq, newSoftirq, newSteal=0, newGuest=0, newGuestNice=0;
            if(stats.length >= 1+8){
                newUser =    Long.parseLong(stats[1]);
                newNice =    Long.parseLong(stats[2]);
                newSystem =  Long.parseLong(stats[3]);
                newIdle =    Long.parseLong(stats[4]);
                newIowait =  Long.parseLong(stats[5]);
                newIrq =     Long.parseLong(stats[6]);
                newSoftirq = Long.parseLong(stats[7]);
                if(stats.length >= 1+8+3){
                    newSteal =     Long.parseLong(stats[8]);
                    newGuest =     Long.parseLong(stats[9]);
                    newGuestNice = Long.parseLong(stats[10]);
                }

                // Summarize
                newUser = newUser + newNice - newGuest - newGuestNice;
                newSystem = newSystem + newIrq + newSoftirq;
                newGuest = newGuest + newGuestNice;

                // Calculate the diffs
                long userDiff = newUser - user;
                long idleDiff = (newIdle + newIowait) - (idle + iowait);
                long systemDiff = newSystem - system;
                long stealDiff = newSteal - steal;
                long virtualDiff = newGuest - guest;
                long newTotal = userDiff + systemDiff + idleDiff + stealDiff + virtualDiff;
                // Calculate load
                load_total = (float)(newTotal-idleDiff)/newTotal;
                load_user = (float)userDiff/newTotal;
                load_system = (float)systemDiff/newTotal;
                load_iowait = (float)(newIowait - iowait)/newTotal;
                load_virtual = (float)virtualDiff/newTotal;

                // update old values
                user = newUser;
                system = newSystem;
                idle = newIdle;
                iowait = newIowait;
                steal = newSteal;
                guest = newGuest;
                total = newTotal;
            }
        }

        public float getTotalLoad() {
            return load_total;
        }
        public float getUserLoad() {
            return load_user;
        }
        public float getSystemLoad() {
            return load_system;
        }
        public float getIOWaitLoad() {
            return load_iowait;
        }
        public float getVirtualLoad() {
            return load_virtual;
        }

    }

    public static void main(String[] args){
        while(true){
            Iterator<CpuStats> it = ProcStat.getCpuStats();
            for(int i=0; it.hasNext(); ++i){
                CpuStats cpu = it.next();
                System.out.print("CPU"+i+": " + cpu.getTotalLoad()+ " ");
            }
            System.out.println("Total Load: " + ProcStat.getTotalCpuStats().getTotalLoad());
            try{Thread.sleep(1000);}catch (Exception e){}
        }
    }
}
