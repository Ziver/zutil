/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Ziver Koc
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

package zutil.net.nio.worker.grid;

import zutil.net.nio.message.Message;

public class GridMessage<T> implements Message {
    private static final long serialVersionUID = 1L;

    // Client type messages
    /** Computation job return right answer **/
    public static final int COMP_SUCCESSFUL = 1; //
    /** Initial static data **/
    public static final int COMP_INCORRECT = 2; //
    /** Computation job return wrong answer **/
    public static final int COMP_ERROR = 3; //
    /** There was an error computing **/
    public static final int REGISTER = 4; //
    /** Register at the server **/
    public static final int UNREGISTER = 5; //
    /** Request new computation data **/
    public static final int NEW_DATA = 6; //

    // Server type messages
    /** Sending initial static data **/
    public static final int INIT_DATA = 100;
    /** Sending new dynamic data **/
    public static final int COMP_DATA = 101;


    private int type;
    private int jobId;
    private T data;

    /**
     * Creates a new GridMessage
     *
     * @param type is the type of message
     */
    public GridMessage(int type){
        this(type, 0, null);
    }

    /**
     * Creates a new GridMessage
     *
     * @param type is the type of message
     * @param jobId is the id of the job
     */
    public GridMessage(int type, int jobId){
        this(type, jobId, null);
    }

    /**
     * Creates a new GridMessage
     *
     * @param type is the type of message
     * @param jobId is the id of the job
     * @param data is the data to send with this message
     */
    public GridMessage(int type, int jobId, T data){
        this.type = type;
        this.jobId = jobId;
        this.data = data;
    }

    /**
     * @return the type of message
     */
    public int messageType(){
        return type;
    }

    /**
     * @return the job id for this message
     */
    public int getJobQueueID(){
        return jobId;
    }

    /**
     * @return the data in this message, may not always carry any data.
     */
    public T getData(){
        return data;
    }
}
