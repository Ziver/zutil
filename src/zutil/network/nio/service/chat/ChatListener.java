package zutil.network.nio.service.chat;

/**
 * Tis is a listener class for new chat messages
 * @author Ziver
 *
 */
public interface ChatListener {
	public void messageAction(String msg, String room);
}
