package zutil.network.nio.worker;

import java.util.HashMap;
import java.util.Map;

import zutil.MultiPrintStream;
import zutil.network.nio.NioNetwork;
import zutil.network.nio.message.ChatMessage;
import zutil.network.nio.message.Message;
import zutil.network.nio.message.SyncMessage;
import zutil.network.nio.message.type.EchoMessage;
import zutil.network.nio.message.type.ResponseRequestMessage;
import zutil.network.nio.response.ResponseEvent;
import zutil.network.nio.service.NetworkService;
import zutil.network.nio.service.chat.ChatService;
import zutil.network.nio.service.sync.SyncService;


public class SystemWorker extends ThreadedEventWorker {
	private NioNetwork nio;
	// Maps a SocketChannel to a RspHandler
	private Map<Double, ResponseEvent> rspEvents = new HashMap<Double, ResponseEvent>();	
	// Difren services listening on specific messages
	@SuppressWarnings("unchecked")
	private Map<Class, NetworkService> services = new HashMap<Class, NetworkService>();
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
			if(NioNetwork.DEBUG>=2) MultiPrintStream.out.println("System Message: "+event.data.getClass().getName());
			if(event.data instanceof Message){
				if(event.data instanceof EchoMessage && ((EchoMessage)event.data).echo()){
					// Echos back the recived message
					((EchoMessage)event.data).recived();
					if(NioNetwork.DEBUG>=3) MultiPrintStream.out.println("Echoing Message: "+event.data);
					nio.send(event.socket, event.data);
				}
				else if(event.data instanceof ResponseRequestMessage && 
						rspEvents.get(((ResponseRequestMessage)event.data).getResponseId()) != null){
					// Handle the response
					handleResponse(((ResponseRequestMessage)event.data).getResponseId(), event.data);
					if(NioNetwork.DEBUG>=3) MultiPrintStream.out.println("Response Request Message: "+event.data);
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
	@SuppressWarnings("unchecked")
	public void registerService(Class c, NetworkService ns){
		services.put(c, ns);
	}

	/**
	 * Unregisters a service
	 * 
	 * @param c The class
	 */
	@SuppressWarnings("unchecked")
	public void unregisterService(Class c){
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
