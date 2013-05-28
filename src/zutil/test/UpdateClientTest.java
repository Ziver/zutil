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

import java.awt.EventQueue;
import java.util.logging.Level;

import zutil.ProgressListener;
import zutil.log.CompactLogFormatter;
import zutil.log.LogUtil;
import zutil.net.update.FileInfo;
import zutil.net.update.UpdateClient;
import zutil.net.update.Zupdater;

public class UpdateClientTest implements ProgressListener<UpdateClient, FileInfo>{
	public static void main(String[] args){
		LogUtil.setLevel("zutil", Level.FINEST);
		LogUtil.setFormatter("zutil", new CompactLogFormatter());
		
		UpdateClientTest client = new UpdateClientTest();
		client.start();
	}
	
	public void start(){
		try {
			final UpdateClient client = new UpdateClient("localhost", 2000, "C:\\Users\\Ziver\\Desktop\\client");
			client.setProgressListener(new Zupdater());
			
			//client.setProgressListener(this);
						
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						Zupdater gui = new Zupdater();
						client.setProgressListener(gui);
						gui.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			client.update();
			client.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void progressUpdate(UpdateClient source, FileInfo info, double percent) {
		System.out.println(info+": "+percent+"%");		
	}
}
