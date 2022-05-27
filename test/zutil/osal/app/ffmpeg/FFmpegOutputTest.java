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

import static org.junit.Assert.*;

public class FFmpegOutputTest {

    @Test
    public void onlyOutput() {
        FFmpegOutput ffmpegOutput = new FFmpegOutput("oTest.mp4");

        assertEquals("\"oTest.mp4\"", ffmpegOutput.buildCommand());
    }


    @Test
    public void options() {
        FFmpegOutput ffmpegOutput = new FFmpegOutput("oTest.mp4");
        ffmpegOutput.setDuration(10.1f);
        ffmpegOutput.setStartPosition(9.1f);
        ffmpegOutput.setEndPosition(20.1f);
        ffmpegOutput.setTargetFileSize(1000);
        ffmpegOutput.setEncodingPass(2);

        ffmpegOutput.setVideoCodec(FFmpegConstants.FFmpegVideoCodec.libx264);
        ffmpegOutput.setVideoFrameRate(29.8f);
        ffmpegOutput.setVideoResolution(320, 240);
        ffmpegOutput.setVideoBitRate(300_000);
        ffmpegOutput.setVideoQuality(22);

        ffmpegOutput.setAudioCodec(FFmpegConstants.FFmpegAudioCodec.libmp3lame);
        ffmpegOutput.setAudioSampleRate(48_000);
        ffmpegOutput.setAudioChannels(6);
        ffmpegOutput.setAudioBitRate(360_000);
        ffmpegOutput.setAudioQuality(23);
        ffmpegOutput.setAudioEnabled(false);

        ffmpegOutput.setSubtitleCodec(FFmpegConstants.FFmpegSubtitleCodec.subrip);
        ffmpegOutput.setSubtitleEnabled(false);

        assertEquals("-ss 9.1 -to 20.1 -t 10.1 -fs 1000 -pass 2" +
                    " -codec:v libx264 -frames 29.8 -filter:v scale=320:240 -b:v 300000 -qscale:v 22" +
                    " -codec:a libmp3lame -ar 48000 -ac 6 -b:a 360000 -qscale:a 23 -an" +
                    " -codec:s subrip -sn" +
                    " \"oTest.mp4\"",
                ffmpegOutput.buildCommand());
    }

}