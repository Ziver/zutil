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

package zutil.io.file;

import zutil.io.MultiPrintStream;
import zutil.log.LogUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * This class checks a file periodically for changes to the
 * last modified date and calls the registered listeners.
 *
 * @author Ziver
 *
 */
public class FileWatcher extends TimerTask{
    private static Logger logger = LogUtil.getLogger();

    private FileChangeListener listener;
    private long lastChanged;
    private File file;

    /**
     * Creates a watcher for the given file whit the check
     * interval of 1 second
     *
     * @param 		file 	is the file to check
     */
    public FileWatcher(File file) throws FileNotFoundException{
        this(file, 1000);
    }

    /**
     * Creates a watcher for the given file whit the given
     * check interval
     *
     * @param 		file 			is the file
     * @param 		interval 		is the interval
     */
    public FileWatcher(File file, int interval) throws FileNotFoundException{
        if (file==null || !file.exists())
            throw new FileNotFoundException("File not found: " +file);
        this.file = file;
        lastChanged = file.lastModified();

        Timer t = new Timer(true);
        t.schedule(this, 0, interval);
    }

    public void setListener(FileChangeListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        if (lastChanged != file.lastModified()) {
            lastChanged = file.lastModified();
            if (listener != null) {
                listener.fileChangedEvent(file);
            }
            else {
                logger.fine("File was modified (" + file + ") but no listeners was registered.");
            }
        }
    }

    /**
     * Interface for the FileWatcher class
     *
     * @author Ziver
     */
    public interface FileChangeListener{

        /**
         * This method is called when there is a change in a file
         *
         * @param file The file that has changed
         */
        void fileChangedEvent(File file);
    }
}
