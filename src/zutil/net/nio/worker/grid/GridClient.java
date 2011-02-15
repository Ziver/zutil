package zutil.net.nio.worker.grid;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import zutil.io.MultiPrintStream;
import zutil.net.nio.NioClient;
import zutil.net.nio.message.GridMessage;
import zutil.net.nio.worker.ThreadedEventWorker;
import zutil.net.nio.worker.WorkerDataEvent;

/**
 * This class is the client part of the grid.
 * It connects to a grid server and requests new job.
 * And then sends back the result to the server.
 * 
 * @author Ziver
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class GridClient extends ThreadedEventWorker {
	private static LinkedList<GridJob> jobQueue;
	private static GridThread thread;
	private static NioClient network;

	/**
	 * Creates a new GridClient object and registers itself at the server
	 * and sets itself as a worker in NioClient
	 * 
	 * @param thread the Thread interface to run for the jobs
	 * @param network the NioClient to use to communicate to the server
	 */
	public GridClient(GridThread thread, NioClient network){
		jobQueue = new LinkedList<GridJob>();
		GridClient.thread = thread;
		GridClient.network = network;

	}

	/**
	 * Starts up the client and a couple of GridThreads.
	 * And registers itself as a worker in NioClient
	 * @throws IOException 
	 */
	public void initiate() throws IOException{
		network.setDefaultWorker(this);
		network.send(new GridMessage(GridMessage.REGISTER));

		for(int i=0; i<Runtime.getRuntime().availableProcessors() ;i++){
			Thread t = new Thread(thread);
			t.start();
		}
	}

	@Override
	public void messageEvent(WorkerDataEvent e) {
		// ignores other messages than GridMessage
		if(e.data instanceof GridMessage){
			GridMessage msg = (GridMessage)e.data;
			switch(msg.messageType()){
			// Receive data from Server
			case GridMessage.INIT_DATA:
				thread.setInitData(msg.getData());
				break;
			case GridMessage.COMP_DATA:
				jobQueue.add(new GridJob(msg.getJobQueueID(), (Queue)msg.getData()));
				break;
			}
		}
	}

	/**
	 * Register whit the server that the job is done
	 * 
	 * @param jobID is the job id
	 * @param correct if the answer was right
	 * @param result the result of the computation
	 * @throws IOException
	 */
	public static void jobDone(int jobID, boolean correct, Object result) throws IOException{
		if(correct)
			network.send(new GridMessage(GridMessage.COMP_SUCCESSFUL, jobID, result));
		else
			network.send(new GridMessage(GridMessage.COMP_INCORRECT, jobID, result));
	}

	/**
	 * Registers with the server that there was an 
	 * error when computing this job
	 * 
	 * @param jobID is the job id
	 */
	public static void jobError(int jobID){
		try{
			network.send(new GridMessage(GridMessage.COMP_SUCCESSFUL, jobID));
		}catch(Exception e){e.printStackTrace();}
	}

	/**
	 * @return a new job to compute
	 * @throws IOException
	 */
	public static synchronized GridJob getNextJob() throws IOException{
		if(jobQueue.isEmpty()){
			network.send(new GridMessage(GridMessage.NEW_DATA));
			while(jobQueue.isEmpty()){
				try{Thread.sleep(100);}catch(Exception e){}
			}
		}
		MultiPrintStream.out.println("Starting job");
		return jobQueue.poll();
	}

}
