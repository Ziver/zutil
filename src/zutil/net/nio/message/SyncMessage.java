package zutil.net.nio.message;

import zutil.net.nio.message.type.SystemMessage;

public class SyncMessage extends Message implements SystemMessage{
	private static final long serialVersionUID = 1L;
	public static enum MessageType { REQUEST_ID, NEW, REMOVE, SYNC };
	
	// type of message
	public MessageType type;
	// id of the Object
	public String id;
}
