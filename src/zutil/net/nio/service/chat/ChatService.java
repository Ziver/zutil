/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Ziver Koc
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package zutil.net.nio.service.chat;

import zutil.log.LogUtil;
import zutil.net.nio.NioNetwork;
import zutil.net.nio.message.ChatMessage;
import zutil.net.nio.message.Message;
import zutil.net.nio.service.NetworkService;

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * A simple chat service with users and rooms
 * 
 * @author Ziver
 */
public class ChatService extends NetworkService{
	private static Logger logger = LogUtil.getLogger();
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
					logger.finer("New Chat Message: "+chatmessage.msg);
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
		logger.fine("New Chat User: "+socket);
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
			logger.fine("Remove Chat User: "+socket);
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
			logger.fine("New Chat Room: "+room);
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
			logger.fine("Remove Chat Room: "+room);
			rooms.remove(room);
		}
	}

	public static ChatService getInstance(){
		return (ChatService)instance;
	}
}
