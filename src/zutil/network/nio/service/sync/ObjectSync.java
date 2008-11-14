package zutil.network.nio.service.sync;

import zutil.network.nio.message.SyncMessage;

public abstract class ObjectSync {
	public String id;

	public ObjectSync(String id){
		this.id = id;
	}
	
	/**
	 * Sends sync message if the object has bean changed
	 */
	public abstract void sendSync();
	
	/**
	 * Applies the SyncMessage to the object
	 * @param message
	 * @param object
	 */
	public abstract void syncObject(SyncMessage message);
	
	/**
	 * Called when the object is removed from the sync list
	 */
	public abstract void remove();
}
