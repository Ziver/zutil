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

package zutil.ui;

import zutil.log.LogUtil;

import java.util.*;
import java.util.logging.Logger;

/**
 * A manager class for managing user gui messages.
 */
public class UserMessageManager implements Iterable<UserMessageManager.UserMessage> {
    private static final Logger logger = LogUtil.getLogger();

    private List<UserMessage> messages = Collections.synchronizedList(new LinkedList<>());


    /**
     * @param id      is the id of the wanted message.
     * @return a UserMessage object matching the ID or null if no message was found
     */
    public UserMessage get(int id) {
        for (UserMessage msg : messages) {
            if (msg.getId() == id) {
                return msg;
            }
        }
        return null;
    }

    public void add(UserMessage message) {
        messages.remove(message); // We don't want to flood the user with duplicate messages
        messages.add(message);
        logger.finer("Added new user message: " + message);
    }

    /**
     * Dismiss a specific message, should be connected to a user action.
     *
     * @param   id      is the id of the specific message.
     */
    public void dismiss(int id) {
        for (UserMessage message : messages) {
            if (message.getId() == id)
                message.dismiss();
        }
    }

    /**
     * This method must be executed when a gui has been generated,
     * it will decrees TTL of all messages by one.
     */
    public void decreaseTTL() {
        for (UserMessage message : messages) {
            message.decreaseTTL();
        }
    }

    /**
     * @return a list of all active messages
     */
    public List<UserMessage> getMessages() {
        List<UserMessage> messagesClone = new ArrayList<>(messages.size());
        for (Iterator<UserMessage> it = messages.iterator(); it.hasNext();) {
            UserMessage alert = it.next();
            if (alert.ttl <= 0) { // if alert is to old, remove it
                logger.fine("Message dismissed with end of life, alert id: " + alert.id);
                it.remove();
            } else {
                messagesClone.add(alert);
            }
        }

        return messagesClone;
    }

    @Override
    public Iterator<UserMessage> iterator() {
        return getMessages().iterator();
    }


    /**
     * Enum that defines the severity level of the message.
     */
    public enum MessageLevel {
        ERROR,
        WARNING,
        SUCCESS,
        INFO
    }

    /**
     * Ennum specifying how long a message should be shown to a User.
     */
    public enum MessageTTL {
        ONE_VIEW,
        DISMISSED
    }

    /**
     * Data class containing information about a single message.
     */
    public static class UserMessage {
        private static int nextId = 0;

        private int id;
        private MessageLevel level;
        private String title;
        private String description;
        private int ttl;


        public UserMessage(MessageLevel level, String title) {
            this(level, title, null, MessageTTL.ONE_VIEW);
        }
        public UserMessage(MessageLevel level, String title, MessageTTL ttl) {
            this(level, title, null, ttl);
        }
        public UserMessage(MessageLevel level, String title, String description, MessageTTL ttl) {
            synchronized (this) {
                this.id = nextId++;
            }

            this.level = level;
            this.title = title;
            this.description = description;
            setTTL(ttl);
        }


        public int getId() {
            return id;
        }

        public MessageLevel getLevel() {
            return level;
        }
        public boolean isError() {
            return level == MessageLevel.ERROR;
        }
        public boolean isWarning() {
            return level == MessageLevel.WARNING;
        }
        public boolean isSuccess() {
            return level == MessageLevel.SUCCESS;
        }
        public boolean isInfo() {
            return level == MessageLevel.INFO;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public void decreaseTTL() {
            --ttl;
        }

        public void setTTL(MessageTTL ttl) {
            switch (ttl) {
                case ONE_VIEW:
                    this.ttl = 1;
                    break;
                case DISMISSED:
                    this.ttl = Integer.MAX_VALUE;
                    break;
            }
        }

        public void dismiss() {
            ttl = -1;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Integer)
                return this.id == ((int) obj);
            else if (obj instanceof UserMessage)
                return level == ((UserMessage) obj).level &&
                        title.equals(((UserMessage) obj).title);
            return false;
        }
    }
}
