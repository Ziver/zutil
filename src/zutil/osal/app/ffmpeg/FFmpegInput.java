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

import zutil.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class includes data related to a single input into FFmpeg
 */
public class FFmpegInput {
    // General Options

    private String input;
    private Float duration;
    private Float positionStart;
    private Float positionEnd;
    private List<String> additionalArgs = new ArrayList<>();

    // Audio Options

    private boolean audioEnabled = true;

    // Subtitle Options

    private boolean subtitleEnabled = true;



    public FFmpegInput(String input) {
        this.input = input;
    }


    /**
     * Limit the duration of data read from the input file.
     * {@link #setDuration(float)} and {@link #setEndPosition(float)} are mutually exclusive and {@link #setDuration(float)} has priority.
     *
     * @param duration duration in seconds
     */
    public void setDuration(float duration) {
        this.duration = duration;
    }
    /**
     * Seeks in this input file to position.
     *
     * @param position Position in seconds
     */
    public void setStartPosition(float position) {
        this.positionStart = position;
    }
    /**
     * Stop reading the input file at position.
     * {@link #setDuration(float)} and {@link #setEndPosition(float)} are mutually exclusive and {@link #setDuration(float)} has priority.
     *
     * @param position Position in seconds
     */
    public void setEndPosition(float position) {
        this.positionEnd = position;
    }

    /**
     * Add additional args that may not be supported by the API, these values will be inserted to the command line as is.
     *
     * @param args  a list of additional commands
     */
    public void addAdditionalArg(String... args) {
        additionalArgs.addAll(Arrays.asList(args));
    }

    // ----------------------------------------------------
    // Audio Options
    // ----------------------------------------------------

    /**
     * Enable or Blocks audio from input file. (enabled by default)
     *
     * @param enabled Set to false to disable audio
     */
    public void setAudioEnabled(boolean enabled) {
        this.audioEnabled = enabled;
    }

    // ----------------------------------------------------
    // Subtitle Options
    // ----------------------------------------------------

    /**
     * Enable or Blocks subtitles from input file. (enabled by default)
     *
     * @param enabled Set to false to disable subtitles
     */
    public void setSubtitleEnabled(boolean enabled) {
        this.subtitleEnabled = enabled;
    }

    // ----------------------------------------------------
    // Command Generation
    // ----------------------------------------------------

    protected String buildCommand() {
        StringBuilder command = new StringBuilder();

        // ----------------------------------------------------
        // General Options
        // ----------------------------------------------------

        if (positionStart != null)
            command.append(" -ss ").append(positionStart);
        if (positionEnd != null)
            command.append(" -to ").append(positionEnd);
        if (duration != null)
            command.append(" -t ").append(duration);

        // ----------------------------------------------------
        // Audio Options
        // ----------------------------------------------------

        if (!audioEnabled)
            command.append(" -an");

        // ----------------------------------------------------
        // Subtitle Options
        // ----------------------------------------------------

        if (!subtitleEnabled)
            command.append(" -sn");

        command.append(StringUtil.join(" ", additionalArgs));
        command.append(" -i ").append(input);
        return command.toString().trim();
    }
}