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
import zutil.osal.app.ffmpeg.FFmpegConstants.*;

import static org.junit.Assert.*;


public class FFmpegTest {

    @Test
    public void noArgs() {
        FFmpeg ffmpeg = new FFmpeg();
        ffmpeg.addInput(new FFmpegInput("iTest.mp4"));
        ffmpeg.addOutput(new FFmpegOutput("oTest.mp4"));

        assertEquals("ffmpeg -i \"iTest.mp4\" \"oTest.mp4\"", ffmpeg.buildCommand());
    }

    @Test
    public void setLogLevel() {
        FFmpeg ffmpeg = new FFmpeg();
        ffmpeg.setLogLevel(FFmpegLogLevel.ERROR);
        ffmpeg.addInput(new FFmpegInput("iTest.mp4"));
        ffmpeg.addOutput(new FFmpegOutput("oTest.mp4"));

        assertEquals("ffmpeg -loglevel error -i \"iTest.mp4\" \"oTest.mp4\"", ffmpeg.buildCommand());
    }

    @Test
    public void enableOutputOverwrite() {
        FFmpeg ffmpeg = new FFmpeg();
        ffmpeg.enableOutputOverwrite(true);
        ffmpeg.addInput(new FFmpegInput("iTest.mp4"));
        ffmpeg.addOutput(new FFmpegOutput("oTest.mp4"));

        assertEquals("ffmpeg -y -i \"iTest.mp4\" \"oTest.mp4\"", ffmpeg.buildCommand());
    }
}