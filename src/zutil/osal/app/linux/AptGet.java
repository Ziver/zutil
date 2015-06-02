/*
 * Copyright (c) 2015 ezivkoc
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

import zutil.log.LogUtil;
import zutil.osal.OSAbstractionLayer;

import java.util.logging.Logger;

/**
 * Created by Ziver on 2014-11-09.
 */
public class AptGet {
    public static final Logger log = LogUtil.getLogger();

    private static long updateTimestamp;

    public static void install(String pkg) {
        update();
        OSAbstractionLayer.runCommand("apt-get install " + pkg);
    }

    public static void update(){
        // Only run every 5 min
        if(updateTimestamp + 1000*60*5 >System.currentTimeMillis()){
            OSAbstractionLayer.runCommand("apt-get update");
        }
    }

    public static void purge(String pkg) {
        OSAbstractionLayer.runCommand("apt-get --purge remove " + pkg);
    }
}
