package zutil.network.nio.message;

public class ChatMessage extends Message{
	private static final long serialVersionUID = 1L;

	public static enum ChatMessageType {REGISTER, UNREGISTER, MESSAGE};
	
	public ChatMessageType type;
	public String msg;
	public String room;
	
	/**
	 * Registers the user to the main chat
	 * 
	 * @param name Name of user
	 */
	public ChatMessage(){
		this("", "", ChatMessageType.REGISTER);
	}
	
	/**
	 * Registers the user to the given room
	 * 
	 * @param room The room to register to
	 */
	public ChatMessage(String room){
		this("", room, ChatMessageType.REGISTER);
	}
	
	/**
	 * Sends a message to the given room
	 * 
	 * @param msg The message
	 * @param room The room
	 */
	public ChatMessage(String msg, String room){
		this(msg, room, ChatMessageType.MESSAGE);
	}
	
	public ChatMessage(String msg, String room, ChatMessageType type){
		this.msg = msg;
		this.room = room;
		this.type = type;
	}
}
