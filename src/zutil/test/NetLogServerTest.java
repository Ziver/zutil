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

package zutil.test;

import java.util.logging.Level;
import java.util.logging.Logger;

import zutil.log.LogUtil;
import zutil.log.net.NetLogServer;

public class NetLogServerTest {
	private static final Logger logger = LogUtil.getLogger();

	public static void main(String[] args){
		LogUtil.setGlobalLevel(Level.FINEST);
		LogUtil.addGlobalHandler(new NetLogServer(5050));
		
		while(true){
			logger.log(Level.SEVERE,  "Test Severe");
			logger.log(Level.WARNING, "Test Warning");
			logger.log(Level.INFO,    "Test Info");
			logger.log(Level.FINE,    "Test Fine");
			logger.log(Level.FINER,   "Test Finer");
			logger.log(Level.FINEST,  "Test Finest");
			
			logger.log(Level.SEVERE,  "Test Exception", new Exception("Test"));
			
			try{Thread.sleep(3000);}catch(Exception e){}
		}
	}
}
