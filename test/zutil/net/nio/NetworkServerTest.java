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

package zutil.net.nio;

import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;
import zutil.net.nio.worker.StandardWorker;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;


@SuppressWarnings("unused")
public class NetworkServerTest {
    public static void main(String[] args) throws InterruptedException {
        try {
            //LogUtil.setGlobalLevel(Level.ALL);
            LogUtil.setGlobalFormatter(new CompactLogFormatter());

            NioServer server = new NioServer(6056);
            server.setDefaultWorker(new StandardWorker(server));

            while (true){
                Thread.sleep(1000);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
