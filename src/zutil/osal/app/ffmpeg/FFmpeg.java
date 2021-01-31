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

import zutil.osal.app.ffmpeg.FFmpegConstants.*;
import zutil.osal.app.ffmpeg.FFmpegProgressManager.FFmpegProgressListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for building a ffmpeg commandline for execution
 *
 * @see <a href="https://ffmpeg.org/ffmpeg.html">FFmpeg Commandline Documentation</a>
 */
public class FFmpeg {

    private FFmpegLogLevel logLevel;
    private boolean overwriteOutput = false;
    private List<FFmpegInput> inputs = new ArrayList<>();
    private List<FFmpegOutput> outputs = new ArrayList<>();
    private FFmpegProgressManager progressManager;


    public FFmpeg() {}


    public void setLogLevel(FFmpegLogLevel level) {
        this.logLevel = level;
    }

    /**
     * @param overwrite Set to true if all outputs should be overwritten if they exist, false if FFmpeg should exit with error.
     */
    public void enableOutputOverwrite(boolean overwrite) {
        this.overwriteOutput = overwrite;
    }

    public void addInput(FFmpegInput input) {
        inputs.add(input);
    }

    public void addOutput(FFmpegOutput output) {
        outputs.add(output);
    }


    public void setProgressListener(FFmpegProgressListener listener) {
        if (listener == null)
            throw new IllegalArgumentException("FFmpegProgressListener cannot be NULL.");

        if (progressManager != null)
            progressManager.close();
        progressManager = new FFmpegProgressManager(listener);
    }


    public String buildCommand() {
        StringBuilder command = new StringBuilder();
        command.append("ffmpeg");

        // General inputs

        if (logLevel != null) {
            command.append(" -loglevel ").append(logLevel.toString().toLowerCase());
        }

        if (progressManager != null) {
            command.append(" -progress ").append(progressManager.getAddress());
        }

        if (overwriteOutput) {
            command.append(" -y");
        }

        // TODO: -stdin Enable interaction on standard input

        // Inputs

        for (FFmpegInput input : inputs) {
            command.append(" ").append(input.buildCommand());
        }

        // Outputs

        for (FFmpegOutput output : outputs) {
            command.append(" ").append(output.buildCommand());
        }

        return command.toString();
    }
}
