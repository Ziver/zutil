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

package zutil.net.nio.worker.sync;

import zutil.log.LogUtil;
import zutil.net.nio.worker.ThreadedEventWorker;
import zutil.net.nio.worker.WorkerEventData;

import java.util.HashMap;
import java.util.logging.Logger;

public class SyncService extends ThreadedEventWorker{
    private static Logger logger = LogUtil.getLogger();

    // list of objects to sync
    private HashMap<String, ObjectSync> sync = new HashMap<>();



    /**
     * Adds a object to be synced
     */
    public void addSyncObject(ObjectSync os) {
        sync.put(os.id, os);
        logger.fine("New Sync object: " + os);
    }

    @Override
    public void messageEvent(WorkerEventData event) {
        if (event.data instanceof SyncMessage) {
            SyncMessage syncMessage = (SyncMessage)event.data;
            if (syncMessage.type == SyncMessage.MessageType.SYNC) {
                ObjectSync obj = sync.get(syncMessage.id);
                if (obj != null) {
                    logger.finer("Syncing Message...");
                    obj.syncObject(syncMessage);
                }
            }
            else if (syncMessage.type == SyncMessage.MessageType.REMOVE) {
                sync.remove(syncMessage.id).remove();
            }
        }
    }

    /**
     * Syncs all the objects whit the server
     */
    public void sync() {
        for (String id : sync.keySet()) {
            sync.get(id).sendSync();
        }
    }
}
