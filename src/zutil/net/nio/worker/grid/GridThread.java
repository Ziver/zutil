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

package zutil.net.nio.worker.grid;

/**
 * This interface is the thread that will do 
 * all the computation in the grid
 * 
 * @author Ziver
 */
public abstract class GridThread implements Runnable{
	/**
	 * The initial static and final data will be sent to this 
	 * method.
	 * 
	 * @param data is the static and or final data
	 */
	public abstract void setInitData(Object data);
	
	public void run(){
		while(true){
			GridJob tmp = null;
			try {
				tmp = GridClient.getNextJob();
				compute(tmp);
			} catch (Exception e) {
				e.printStackTrace();
				if(tmp != null){
					GridClient.jobError(tmp.jobID);
				}
			}
		}
	}
	
	/**
	 * Compute the given data and return 
	 * @param data
	 */
	public abstract void compute(GridJob data) throws Exception;
}
