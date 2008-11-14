package zutil.network.nio.message;

import zutil.network.nio.message.type.SystemMessage;

/**
 * Tells the destination that the 
 * source is still online
 * 
 * @author Ziver
 *
 */
public class KeepAliveMessage extends Message implements SystemMessage{

	private static final long serialVersionUID = 1L;

}
