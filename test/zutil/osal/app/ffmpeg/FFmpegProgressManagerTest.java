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

import org.junit.Test;
import zutil.osal.app.ffmpeg.FFmpegProgressManager.FFmpegProgressParserThread;

import static org.junit.Assert.*;
import static zutil.osal.app.ffmpeg.FFmpegProgressManager.FFmpegProgressStatus;

public class FFmpegProgressManagerTest {

    @Test
    public void simpleParsing() {
        FFmpegProgressManager.FFmpegProgress progress = new FFmpegProgressManager.FFmpegProgress();

        FFmpegProgressParserThread.parseProgress("frame=2675", progress);
        FFmpegProgressParserThread.parseProgress("fps=0.00", progress);
        FFmpegProgressParserThread.parseProgress("bitrate=3000", progress);
        FFmpegProgressParserThread.parseProgress("total_size=10000", progress);
        FFmpegProgressParserThread.parseProgress("out_time_us=107000000", progress);
        FFmpegProgressParserThread.parseProgress("out_time_ms=107000000", progress);
        FFmpegProgressParserThread.parseProgress("out_time=00:01:47.000000", progress);
        FFmpegProgressParserThread.parseProgress("dup_frames=0", progress);
        FFmpegProgressParserThread.parseProgress("drop_frames=0", progress);
        FFmpegProgressParserThread.parseProgress("speed=214x", progress);
        FFmpegProgressParserThread.parseProgress("progress=continue", progress);

        assertEquals(2675, progress.frame);
        assertEquals(0.0f, progress.fps, 0.001);
        assertEquals(3000, progress.bitrate);
        assertEquals(10000, progress.total_size);
        assertEquals(107000000, progress.out_time_us);
        assertEquals(107000000, progress.out_time_ms);
        assertEquals(0, progress.dup_frames);
        assertEquals(0, progress.drop_frames);
        assertEquals(214f, progress.speed, 0.001);
        assertEquals(FFmpegProgressStatus.CONTINUE, progress.progress);
    }

    public void parsingNA() {
        FFmpegProgressManager.FFmpegProgress progress = new FFmpegProgressManager.FFmpegProgress();

        FFmpegProgressParserThread.parseProgress("bitrate=N/A", progress);
        FFmpegProgressParserThread.parseProgress("total_size=N/A", progress);
        FFmpegProgressParserThread.parseProgress("progress=end", progress);

        assertEquals(3000, progress.bitrate);
        assertEquals(10000, progress.total_size);
        assertEquals(FFmpegProgressStatus.END, progress.progress);
    }
}