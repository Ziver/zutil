/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021 Ziver Koc
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

package zutil.osal.app.ffmpeg;

import zutil.log.LogUtil;
import zutil.net.threaded.ThreadedTCPNetworkServer;
import zutil.net.threaded.ThreadedTCPNetworkServerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class FFmpegProgressManager extends ThreadedTCPNetworkServer {
    private static final Logger logger = LogUtil.getLogger();
    private static final int PROGRESS_DEFAULT_PORT = 5697;

    /**
     * A interface where FFmpeg progress will be reported.
     */
    public interface FFmpegProgressListener {
        /**
         * Method will be called everytime there is new information about the progress of the FFmpeg execution.
         *
         * @param progress Object containing progress information.
         */
        void ffmpegProgress(FFmpegProgress progress);
    }

    /**
     * A Data class containing FFmpeg progress information.
     */
    public static class FFmpegProgress {
        public long frame = -1;
        public float fps = -1;
        public int bitrate = -1;
        public long total_size = -1;
        public long out_time_us = -1;
        public long out_time_ms = -1;
        public long out_time = -1;
        public long dup_frames = -1;
        public int drop_frames = -1;
        public float speed = -1;
        public FFmpegProgressStatus progress;
    }

    public enum FFmpegProgressStatus {
        CONTINUE,
        END;

        public static FFmpegProgressStatus valueOfIgnoreCase(String status) {
            for (FFmpegProgressStatus s : FFmpegProgressStatus.values()) {
                if (s.name().equalsIgnoreCase(status))
                    return s;
            }

            throw new IllegalArgumentException("Unknown progress status: " + status);
        }
    }

    // ----------------------------------------------------
    // Manager variables
    // ----------------------------------------------------

    private FFmpegProgressListener listener;
    private String address;

    public FFmpegProgressManager(FFmpegProgressListener listener) {
        this(listener, PROGRESS_DEFAULT_PORT);
    }
    public FFmpegProgressManager(FFmpegProgressListener listener, int port) {
        super(port);
        this.listener = listener;
        this.address = "tcp://" + InetAddress.getLoopbackAddress() + ":" + port;
    }


    @Override
    protected ThreadedTCPNetworkServerThread getThreadInstance(Socket s) throws IOException {
        return new FFmpegProgressParserThread(s, listener);
    }

    /**
     * @return The address where the progress information should be pushed to.
     */
    public String getAddress() {
        return address;
    }

    public FFmpegProgressListener getListener() {
        return listener;
    }


    protected static class FFmpegProgressParserThread implements ThreadedTCPNetworkServerThread {
        private Socket socket;
        private BufferedReader in;
        private FFmpegProgressListener listener;

        protected FFmpegProgressParserThread(Socket s, FFmpegProgressListener listener) throws IOException {
            this.socket = s;
            this.listener = listener;

            this.in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }

        @Override
        public void run() {
            try {
                FFmpegProgress progress = new FFmpegProgress();
                String line;
                while ((line = in.readLine()) != null) {
                    parseProgress(line, progress);

                    if (progress.progress != null) {
                        // End of FFmpeg progress reporting, so report it and reset data object
                        listener.ffmpegProgress(progress);
                        progress = new FFmpegProgress();
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "FFmpeg progress parser thread crashed.", e);
            }

            try {
                in.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "FFmpeg progress parser thread crashed closing socket.", e);
            }
        }

        protected static void parseProgress(String line, FFmpegProgress progress) {
            String[] args = line.split("=", 2);
            if (args.length != 2)
                return;

            String key = args[0].trim();
            String value = args[1].trim();

            if (value.equals("N/A"))
                return;

            switch (key) {
                case "frame":
                    progress.frame = Long.parseLong(value);
                    break;

                case "fps":
                    progress.fps = Float.parseFloat(value);
                    break;

                case "bitrate":
                    progress.bitrate = Integer.parseInt(value);
                    break;

                case "total_size":
                    progress.total_size = Long.parseLong(value);
                    break;

                case "out_time_us": // microseconds
                    progress.out_time_us = Long.parseLong(value);
                    break;

                case "out_time_ms": // milliseconds
                    progress.out_time_ms = Long.parseLong(value);
                    break;

                // TODO: out_time=00:01:47.000000

                case "dup_frames":
                    progress.dup_frames = Long.parseLong(value);
                    break;

                case "drop_frames":
                    progress.drop_frames = Integer.parseInt(value);
                    break;

                case "speed":
                    value = value.replace("x", "");
                    progress.speed = Float.parseFloat(value);
                    break;

                case "progress":
                    progress.progress = FFmpegProgressStatus.valueOfIgnoreCase(value);
                    break;

                default:
                    // TODO: Handle stream information, e.g. stream_0_0_xxx=xxx:
                    break;
            }
        }
    }
}
