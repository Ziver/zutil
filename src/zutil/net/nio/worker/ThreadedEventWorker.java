package zutil.net.nio.worker;

public abstract class ThreadedEventWorker extends Worker{
	private Thread thread;

	public ThreadedEventWorker(){
		thread = new Thread(this);
		thread.start();
	}

	public void update() {
		WorkerDataEvent dataEvent;

		while(true) {
			try{
				// Wait for data to become available
				synchronized(getEventQueue()) {
					while(getEventQueue().isEmpty()) {
						try {
							getEventQueue().wait();
						} catch (InterruptedException e) {}
					}
					dataEvent = (WorkerDataEvent) getEventQueue().remove(0);
				}
				messageEvent(dataEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public abstract void messageEvent(WorkerDataEvent e);

}
