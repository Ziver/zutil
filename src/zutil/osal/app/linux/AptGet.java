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

package zutil.osal.app.linux;

import zutil.Timer;
import zutil.log.LogUtil;
import zutil.osal.OSAbstractionLayer;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Created by Ziver on 2014-11-09.
 */
public class AptGet {
    public static final Logger log = LogUtil.getLogger();

    private static Timer updateTimer = new Timer(1000*60*5); // 5min timer
    private static Timer packageTimer = new Timer(1000*60*5); // 5min timer
    private static HashMap<String, Package> packages = new HashMap<>();

    public static void install(String pkg) {
        update();
        OSAbstractionLayer.exec("apt-get -y install " + pkg);
        packageTimer.reset();
    }

    public static void upgrade(){
        update();
        OSAbstractionLayer.exec("apt-get -y " +
                // Dont display configuration conflicts
                "-o Dpkg::Options::=\"--force-confdef\" -o Dpkg::Options::=\"--force-confold\" " +
                "upgrade");
        packageTimer.reset();
    }

    public static void update(){
        // Only run every 5 min
        if(updateTimer.hasTimedOut()){
            OSAbstractionLayer.exec("apt-get update");
            updateTimer.start();
        }
    }

    public static void purge(String pkg) {
        OSAbstractionLayer.exec("apt-get --purge remove " + pkg);
        packageTimer.reset();
    }


    public static Package getPackage(String pkg){
        updatePackages();
        return packages.get(pkg);
    }
    public static synchronized void updatePackages(){
        // Only run every 5 min
        if(packageTimer.hasTimedOut()){
            String[] output = OSAbstractionLayer.exec("dpkg --list");
            for(int i=5; i<output.length; ++i) {
                packages.put(output[i], new Package(output[5]));
            }
            packageTimer.start();
        }
    }



    /**
     * This class represents a system package and its current status
     */
    public static class Package{
        public enum PackageState{
            // Expected States
            /* u */ Unknown,
            /* i */ Installed,
            /* r */ Removed,
            /* p */ Purged, // remove including config files
            /* h */ Holding,

            // Current States
            /* n */ NotInstalled,
            /* i */ //Installed,
            /* c */ ConfigFiles, // only the config files are installed
            /* u */ Unpacked,
            /* f */ HalfConfigured, // configuration failed for some reason
            /* h */ HalfInstalled, // installation failed for some reason
            /* w */ TriggersAwaited, // package is waiting for a trigger from another package
            /* t */ TriggersPending, //package has been triggered

            // Error States
            /* r */ ReinstallRequired // package broken, reinstallation required
        }


        private PackageState expectedState = PackageState.Unknown;
        private PackageState currentState  = PackageState.Unknown;;
        private PackageState errorState    = PackageState.Unknown;;

        private String name;
        private String version;
        private String description;


        protected Package(String dpkgStr){
            switch (dpkgStr.charAt(0)){
                case 'u': expectedState = PackageState.Unknown; break;
                case 'i': expectedState = PackageState.Installed; break;
                case 'r': expectedState = PackageState.Removed; break;
                case 'p': expectedState = PackageState.Purged; break;
                case 'h': expectedState = PackageState.Holding; break;
            }
            switch (dpkgStr.charAt(0)){
                case 'n': currentState = PackageState.NotInstalled; break;
                case 'i': currentState = PackageState.Installed; break;
                case 'c': currentState = PackageState.ConfigFiles; break;
                case 'u': currentState = PackageState.Unpacked; break;
                case 'f': currentState = PackageState.HalfConfigured; break;
                case 'h': currentState = PackageState.HalfInstalled; break;
                case 'w': currentState = PackageState.TriggersAwaited; break;
                case 't': currentState = PackageState.TriggersPending; break;
            }
            if(dpkgStr.charAt(2) == 'r')
                errorState = PackageState.ReinstallRequired;

            String[] tmp = dpkgStr.split("[ \\t]*", 4);
            name = tmp[1];
            version = tmp[2];
            description = tmp[3];
        }


        public PackageState getExpectedState() {
            return expectedState;
        }
        public PackageState getCurrentState() {
            return currentState;
        }
        public PackageState getErrorState() {
            return errorState;
        }
        public String getName() {
            return name;
        }
        public String getVersion() {
            return version;
        }
        public String getDescription() {
            return description;
        }
    }
}
