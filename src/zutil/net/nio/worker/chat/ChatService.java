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

package zutil.net.nio.worker.chat;

import zutil.log.LogUtil;
import zutil.net.nio.NioNetwork;
import zutil.net.nio.message.ChatMessage;
import zutil.net.nio.message.Message;
import zutil.net.nio.worker.ThreadedEventWorker;
import zutil.net.nio.worker.WorkerEventData;

import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * A simple chat service with users and rooms
 * 
 * @author Ziver
 */
public class ChatService extends ThreadedEventWorker{
	private static Logger logger = LogUtil.getLogger();

	private HashMap<String,LinkedList<SocketAddress>> rooms = new HashMap<>();
	private ChatListener listener;





	@Override
    public void messageEvent(WorkerEventData event) {
		try {
			// New message
			if(event.data instanceof ChatMessage){
				ChatMessage chatmessage = (ChatMessage)event.data;
				//is this a new message
				if(chatmessage.type == ChatMessage.ChatMessageType.MESSAGE){
					// Is this the server
                    if(rooms.containsKey(chatmessage.room)){
                        LinkedList<SocketAddress> tmpList = rooms.get(chatmessage.room);

                        // Broadcast the message
                        for(SocketAddress remote : tmpList){
                            event.network.send(remote, chatmessage); // TODO: should not be done for clients
                        }
                    }
					logger.finer("New Chat Message: "+chatmessage.msg);
					listener.messageAction(chatmessage.msg, chatmessage.room);
				}
				// register to a room
				else if(chatmessage.type == ChatMessage.ChatMessageType.REGISTER){
					registerUser(chatmessage.room, event.remoteAddress);
				}
				// unregister to a room
				else if(chatmessage.type == ChatMessage.ChatMessageType.UNREGISTER){
					unRegisterUser(chatmessage.room, event.remoteAddress);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Registers a user to the main room
	 * 
	 * @param	remoteAddress	the address of the remote user
	 */
	public void registerUser(SocketAddress remoteAddress){
		registerUser("", remoteAddress);
	}

	/**
	 * Registers the given user to a specific room
	 * 
	 * @param 	room			the room name
	 * @param	remoteAddress	the address of the remote user
	 */
	public void registerUser(String room, SocketAddress remoteAddress){
		addRoom(room);
		logger.fine("New Chat User: "+remoteAddress);
		rooms.get(room).add(remoteAddress);
	}

	/**
	 * Unregisters a user from a room and removes the room if its empty
	 *
	 * @param 	room			the room name
	 * @param	remoteAddress	the address of the remote user
	 */
	public void unRegisterUser(String room, SocketAddress remoteAddress){
		if(rooms.containsKey(room)){
			logger.fine("Remove Chat User: "+remoteAddress);
			rooms.get(room).remove(remoteAddress);
			removeRoom(room);
		}
	}

	/**
	 * Adds a room into the list
	 * 
	 * @param room The name of the room
	 */
	private void addRoom(String room){
		if(!rooms.containsKey(room)){
			logger.fine("New Chat Room: "+room);
			rooms.put(room, new LinkedList<>());
		}
	}
	
	/**
	 * Removes the given room if its empty
	 * 
	 * @param room The room
	 */
	private void removeRoom(String room){
		if(rooms.get(room).isEmpty()){
			logger.fine("Remove Chat Room: "+room);
			rooms.remove(room);
		}
	}

}
