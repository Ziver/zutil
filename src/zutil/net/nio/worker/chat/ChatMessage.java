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

package zutil.net.nio.worker.chat;

import zutil.net.nio.message.Message;

public class ChatMessage implements Message {
    private static final long serialVersionUID = 1L;

    public enum ChatMessageType {REGISTER, UNREGISTER, MESSAGE};

    public ChatMessageType type;
    public String msg;
    public String room;

    /**
     * Registers the user to the main chat
     */
    public ChatMessage() {
        this("", "", ChatMessageType.REGISTER);
    }

    /**
     * Registers the user to the given room
     *
     * @param room The room to register to
     */
    public ChatMessage(String room) {
        this("", room, ChatMessageType.REGISTER);
    }

    /**
     * Sends a message to the given room
     *
     * @param msg The message
     * @param room The room
     */
    public ChatMessage(String msg, String room) {
        this(msg, room, ChatMessageType.MESSAGE);
    }

    public ChatMessage(String msg, String room, ChatMessageType type) {
        this.msg = msg;
        this.room = room;
        this.type = type;
    }
}
