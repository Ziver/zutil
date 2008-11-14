package zutil.network.nio.worker;

import zutil.MultiPrintStream;

public class EchoWorker extends ThreadedEventWorker {

	@Override
	public void messageEvent(WorkerDataEvent dataEvent) {
		// Return to sender
		MultiPrintStream.out.println("Recived Msg: "+dataEvent.data);
		dataEvent.network.send(dataEvent.socket, dataEvent.data);
		
	}
	
	
}
