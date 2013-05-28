/*******************************************************************************
 * Copyright (c) 2013 Ziver
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
 ******************************************************************************/

package zutil.test;

import java.util.logging.Level;

import javassist.CannotCompileException;
import javassist.NotFoundException;

import javax.wsdl.WSDLException;

import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;
import zutil.net.http.soap.SOAPClientFactory;
import zutil.net.ws.WSInterface;

public class SOAPClientTest {

	public static void main(String[] args) throws InstantiationException, IllegalAccessException, CannotCompileException, NotFoundException, WSDLException{
		LogUtil.setGlobalLevel(Level.ALL);
		LogUtil.setFormatter("", new CompactLogFormatter());
		
		TestClient intf = SOAPClientFactory.getClient(TestClient.class);
		intf.m();
		intf.c();
	}
	
	
	public interface TestClient extends WSInterface{
		public void m();
		
		public void c();

	}
}
