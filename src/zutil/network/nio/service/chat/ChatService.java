package zutil.network.nio.service.chat;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;

import zutil.MultiPrintStream;
import zutil.network.nio.NioNetwork;
import zutil.network.nio.message.ChatMessage;
import zutil.network.nio.message.Message;
import zutil.network.nio.service.NetworkService;

public class ChatService extends NetworkService{
	private HashMap<String,LinkedList<SocketChannel>> rooms;
	private ChatListener listener;

	public ChatService(NioNetwork nio){
		super(nio);
		rooms = new HashMap<String,LinkedList<SocketChannel>>();
	}

	@Override
	public void handleMessage(Message message, SocketChannel socket) {
		try {
			// New message
			if(message instanceof ChatMessage){
				ChatMessage chatmessage = (ChatMessage)message;
				//is this a new message
				if(chatmessage.type == ChatMessage.ChatMessageType.MESSAGE){
					// Is this the server
					if(nio.getType() == NioNetwork.NetworkType.SERVER){
						if(rooms.containsKey(chatmessage.room)){
							LinkedList<SocketChannel> tmpList = rooms.get(chatmessage.room);

							// Broadcast the message
							for(SocketChannel s : tmpList){
								if(s.isConnected()){
									nio.send(s, chatmessage);
								}
								else{
									unRegisterUser(chatmessage.room, s);
								}
							}
						}
					}
					if(NioNetwork.DEBUG>=2)MultiPrintStream.out.println("New Chat Message: "+chatmessage.msg);
					listener.messageAction(chatmessage.msg, chatmessage.room);
				}
				// register to a room
				else if(chatmessage.type == ChatMessage.ChatMessageType.REGISTER){
					registerUser(chatmessage.room, socket);
				}
				// unregister to a room
				else if(chatmessage.type == ChatMessage.ChatMessageType.UNREGISTER){
					unRegisterUser(chatmessage.room, socket);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Registers a user to the main room
	 * 
	 * @param socket The socket to the user
	 */
	public void registerUser(SocketChannel socket){
		registerUser("", socket);
	}

	/**
	 * Registers the given user to a specific room
	 * 
	 * @param room The room
	 * @param socket The socket to the user
	 */
	public void registerUser(String room, SocketChannel socket){
		addRoom(room);
		if(NioNetwork.DEBUG>=1)MultiPrintStream.out.println("New Chat User: "+socket);
		rooms.get(room).add(socket);
	}

	/**
	 * Unregisters a user from a room and removes the room if its empty
	 * 
	 * @param room The room
	 * @param socket The socket to the user
	 */
	public void unRegisterUser(String room, SocketChannel socket){
		if(rooms.containsKey(room)){
			if(NioNetwork.DEBUG>=1)MultiPrintStream.out.println("Remove Chat User: "+socket);
			rooms.get(room).remove(socket);
			removeRoom(room);
		}
	}

	/**
	 * Adds a room into the list
	 * 
	 * @param room The name of the room
	 */
	public void addRoom(String room){
		if(!rooms.containsKey(room)){
			if(NioNetwork.DEBUG>=1)MultiPrintStream.out.println("New Chat Room: "+room);
			rooms.put(room, new LinkedList<SocketChannel>());
		}
	}
	
	/**
	 * Removes the given room if its empty
	 * 
	 * @param room The room
	 */
	public void removeRoom(String room){
		if(rooms.get(room).isEmpty()){
			if(NioNetwork.DEBUG>=1)MultiPrintStream.out.println("Remove Chat Room: "+room);
			rooms.remove(room);
		}
	}

	public static ChatService getInstance(){
		return (ChatService)instance;
	}
}
