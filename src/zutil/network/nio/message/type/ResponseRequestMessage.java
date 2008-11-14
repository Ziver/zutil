package zutil.network.nio.message.type;

/**
 * This interface means that the sender 
 * wants a reply from the destination
 * 
 * @author Ziver
 *
 */
public interface ResponseRequestMessage {

	/**
	 * The id of the response to identify the response event
	 * @return Response id
	 */
	public double getResponseId();
	
}
