package zutil.network.nio.message.type;

import zutil.network.nio.message.Message;

/**
 * The reciver will echo out this message to the sender
 * 
 * @author Ziver
 */
public abstract class EchoMessage extends Message implements SystemMessage{
	private static final long serialVersionUID = 1L;
	
	private boolean echo;
	
	public EchoMessage(){
		echo = true;
	}
	
	/**
	 * This method returns if the message should be echoed
	 * @return If the message should be echoed
	 */
	public boolean echo() {
		return echo;
	}
	
	/**
	 * Called by the reciver to disable looping of the message
	 *
	 */
	public void recived() {
		echo = false;		
	}
}
