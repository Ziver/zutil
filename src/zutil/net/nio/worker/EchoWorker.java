package zutil.net.nio.worker;

import java.io.IOException;

import zutil.io.MultiPrintStream;

public class EchoWorker extends ThreadedEventWorker {

	@Override
	public void messageEvent(WorkerDataEvent dataEvent) {
		try {
			// Return to sender
			MultiPrintStream.out.println("Recived Msg: "+dataEvent.data);
			dataEvent.network.send(dataEvent.socket, dataEvent.data);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
