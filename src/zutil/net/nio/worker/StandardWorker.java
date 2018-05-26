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

package zutil.net.nio.worker;

import zutil.converter.Converter;
import zutil.log.LogUtil;
import zutil.net.nio.NioNetwork;
import zutil.net.nio.message.EchoMessage;
import zutil.net.nio.response.RequestResponseMessage;
import zutil.net.nio.response.ResponseHandler;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class StandardWorker extends ThreadedEventWorker {
    private static Logger logger = LogUtil.getLogger();

    private NioNetwork nio;
    // Maps a responseId to a RspHandler
    private Map<Long, ResponseHandler> rspEvents = new HashMap<>();
    // Different services listening on specific messages
    private Map<Class<?>, ThreadedEventWorker> services = new HashMap<>();



    /**
     * Creates a new StandardWorker
     */
    public StandardWorker(NioNetwork nio){
        this.nio = nio;
    }



    @Override
    public void messageEvent(WorkerEventData event) {
        try {
            logger.finer("Message: "+event.data.getClass().getName());

            if(event.data instanceof EchoMessage && !((EchoMessage)event.data).echo()){
                // Echo back the received message
                ((EchoMessage)event.data).received();
                logger.finer("Echoing Message: "+event.data);
                nio.send(event.remoteAddress, event.data);
            }
            else if(event.data instanceof RequestResponseMessage &&
                    rspEvents.get(((RequestResponseMessage)event.data).getResponseId()) != null){
                long responseId = ((RequestResponseMessage)event.data).getResponseId();
                // Look up the handler for this channel
                ResponseHandler handler = rspEvents.get(responseId);
                // And pass the response to it
                handler.handleResponse(event.data);
                rspEvents.remove(responseId);
                logger.finer("Response Request Message: "+event.data);
            }
            else{
                // Check mapped workers
                if(services.containsKey(event.data.getClass())){
                    services.get(event.data.getClass()).messageEvent(event);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Maps a Worker to a specific message
     *
     * @param   messageClass    the received message class
     * @param   worker          the worker that should handle the specified message type
     */
    public void registerWorker(Class<?> messageClass, ThreadedEventWorker worker){
        services.put(messageClass, worker);
    }

    /**
     * Un-maps a message class to a worker
     *
     * @param   messageClass    the received message class
     */
    public void unregisterWorker(Class<?> messageClass){
        services.remove(messageClass);
    }

    /**
     * Send a message with a defined response handler
     *
     * @param   address     the target host for the message
     * @param   message     the message object
     * @param   handler     the handler that should be called when a response is received
     */
    public void send(SocketAddress address, RequestResponseMessage message, ResponseHandler handler) throws IOException {
        // Register the response handler
        rspEvents.put(message.getResponseId(), handler);

        nio.send(address, Converter.toBytes(message));
    }
}
