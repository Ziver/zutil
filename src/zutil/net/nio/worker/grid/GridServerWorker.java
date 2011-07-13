/*******************************************************************************
 * Copyright (c) 2011 Ziver Koc
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

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import zutil.net.nio.message.GridMessage;
import zutil.net.nio.worker.ThreadedEventWorker;
import zutil.net.nio.worker.WorkerDataEvent;

/**
 * Implements a simple network computing server
 * 
 * @author Ziver
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GridServerWorker extends ThreadedEventWorker{
	// Job timeout after 30 min
	public static int JOB_TIMEOUT = 1000*60*30;

	private HashMap<Integer, GridJob> jobs; // contains all the ongoing jobs
	private Queue<GridJob> reSendjobQueue; // Contains all the jobs that will be recalculated
	private GridJobGenerator jobGenerator; // The job generator
	private GridResultHandler resHandler;
	private int nextJobID;

	public GridServerWorker(GridResultHandler resHandler, GridJobGenerator jobGenerator){
		this.resHandler = resHandler;
		this.jobGenerator = jobGenerator;
		nextJobID = 0;

		jobs = new HashMap<Integer, GridJob>();
		reSendjobQueue = new LinkedList<GridJob>();
		GridMaintainer maintainer = new GridMaintainer();
		maintainer.start();
	}

	@Override
	public void messageEvent(WorkerDataEvent e) {
		try {
			// ignores other messages than GridMessage
			if(e.data instanceof GridMessage){
				GridMessage msg = (GridMessage)e.data;
				GridJob job = null;

				switch(msg.messageType()){
				case GridMessage.REGISTER:
					e.network.send(e.socket, new GridMessage(GridMessage.INIT_DATA, 0, jobGenerator.initValues()));
					break;
				// Sending new data to compute to the client
				case GridMessage.NEW_DATA:
					if(!reSendjobQueue.isEmpty()){ // checks first if there is a job for recalculation
						job = reSendjobQueue.poll();
						job.renewTimeStamp();
					}
					else{ // generates new job
						job = new GridJob(nextJobID,
								jobGenerator.generateJob());
						jobs.put(job.jobID, job);
						nextJobID++;
					}
					GridMessage newMsg = new GridMessage(GridMessage.COMP_DATA, job.jobID, job.job);
					e.network.send(e.socket, newMsg);				
					break;

					// Received computation results
				case GridMessage.COMP_SUCCESSFUL:
					resHandler.resultEvent(msg.getJobQueueID(), true, msg.getData());
					break;
				case GridMessage.COMP_INCORRECT:
					resHandler.resultEvent(msg.getJobQueueID(), false, msg.getData());
					break;
				case GridMessage.COMP_ERROR: // marks the job for recalculation
					job = jobs.get(msg.getJobQueueID());
					reSendjobQueue.add(job);
					break;
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Changes the job timeout value
	 * @param min is the timeout in minutes
	 */
	public static void setJobTimeOut(int min){
		JOB_TIMEOUT = 1000*60*min;
	}

	class GridMaintainer extends Thread{
		/**
		 * Runs some behind the scenes stuff
		 * like job timeout.
		 */
		public void run(){
			while(true){
				long time = System.currentTimeMillis();
				for(int jobID : jobs.keySet()){
					if(time-jobs.get(jobID).timestamp > JOB_TIMEOUT){
						reSendjobQueue.add(jobs.get(jobID));
					}
				}
				try{Thread.sleep(1000*60*1);}catch(Exception e){};
			}
		}
	}
}

