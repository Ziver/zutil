/*
 * Copyright (c) 2015 ezivkoc
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

package zutil.net.nio.worker;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import zutil.log.LogUtil;
import zutil.net.nio.NioNetwork;
import zutil.net.nio.message.ChatMessage;
import zutil.net.nio.message.Message;
import zutil.net.nio.message.SyncMessage;
import zutil.net.nio.message.type.EchoMessage;
import zutil.net.nio.message.type.ResponseRequestMessage;
import zutil.net.nio.response.ResponseEvent;
import zutil.net.nio.service.NetworkService;
import zutil.net.nio.service.chat.ChatService;
import zutil.net.nio.service.sync.SyncService;


public class SystemWorker extends ThreadedEventWorker {
	private static Logger logger = LogUtil.getLogger();
	private NioNetwork nio;
	// Maps a SocketChannel to a RspHandler
	private Map<Double, ResponseEvent> rspEvents = new HashMap<Double, ResponseEvent>();	
	// Difren services listening on specific messages
	private Map<Class<?>, NetworkService> services = new HashMap<Class<?>, NetworkService>();
	/**
	 * Creates a new SystemWorker
	 * @param nio The Network
	 */
	public SystemWorker(NioNetwork nio){
		this.nio = nio;
	}

	@Override
	public void messageEvent(WorkerDataEvent event) {
		try {
			logger.finer("System Message: "+event.data.getClass().getName());
			if(event.data instanceof Message){
				if(event.data instanceof EchoMessage && ((EchoMessage)event.data).echo()){
					// Echos back the recived message
					((EchoMessage)event.data).recived();
					logger.finer("Echoing Message: "+event.data);
					nio.send(event.socket, event.data);
				}
				else if(event.data instanceof ResponseRequestMessage && 
						rspEvents.get(((ResponseRequestMessage)event.data).getResponseId()) != null){
					// Handle the response
					handleResponse(((ResponseRequestMessage)event.data).getResponseId(), event.data);
					logger.finer("Response Request Message: "+event.data);
				}
				else{
					//Services
					if(services.containsKey(event.data.getClass()) ||
							!services.containsKey(event.data.getClass()) && defaultServices(event.data)){
						services.get(event.data.getClass()).handleMessage((Message)event.data, event.socket);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Registers a Service to a specific message
	 * 
	 * @param c The Message class
	 * @param ns The service
	 */
	public void registerService(Class<?> c, NetworkService ns){
		services.put(c, ns);
	}

	/**
	 * Unregisters a service
	 * 
	 * @param c The class
	 */
	public void unregisterService(Class<?> c){
		services.remove(c);
	}

	/**
	 * Connects a ResponseHandler to a specific message 
	 * @param handler The Handler
	 * @param data The Message
	 */
	public void addResponseHandler(ResponseEvent handler, ResponseRequestMessage data){
		rspEvents.put(data.getResponseId(), handler);
	}

	/**
	 * Client And Server ResponseEvent
	 */
	private void handleResponse(double responseId, Object rspData){	
		// Look up the handler for this channel
		ResponseEvent handler = rspEvents.get(responseId);		
		// And pass the response to it
		handler.handleResponse(rspData);

		rspEvents.remove(responseId);
	}

	/**
	 * Registers the default services in the engin e
	 * if the message needs one of them
	 * 
	 * @param o The message
	 */
	private boolean defaultServices(Object o){
		if(o instanceof SyncMessage){
			if(SyncService.getInstance() == null)
				registerService(o.getClass(), new SyncService(nio));
			else
				registerService(o.getClass(), SyncService.getInstance());
			return true;
		}
		else if(o instanceof ChatMessage){
			if(ChatService.getInstance() == null)
				registerService(o.getClass(), new ChatService(nio));
			else
				registerService(o.getClass(), ChatService.getInstance());
			return true;
		}
		return false;
	}

}
