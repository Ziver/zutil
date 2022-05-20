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
import zutil.osal.app.ffmpeg.FFmpegConstants.FFmpegAudioCodec;
import zutil.osal.app.ffmpeg.FFmpegConstants.FFmpegSubtitleCodec;
import zutil.osal.app.ffmpeg.FFmpegConstants.FFmpegVideoCodec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class includes data related to a single output from FFmpeg
 */
public class FFmpegOutput {
    // General Options

    private String output;
    private Float duration;
    private Float positionStart;
    private Float positionEnd;
    private Long fileSize;
    private Integer encodingPass;
    private List<String> additionalArgs = new ArrayList<>();

    // Video Options

    private String videoCodec;
    private Float videoFrameRate;
    private Integer videoWidth;
    private Integer videoHeight;
    private Integer videoBitRate;
    private Integer videoQuality;

    // Audio Options

    private String audioCodec;
    private Integer audioSampleRate;
    private Integer audioChannels;
    private Integer audioBitRate;
    private Integer audioQuality;
    private boolean audioEnabled = true;

    // Subtitle Options

    private String subtitleCodec;
    private boolean subtitleEnabled = true;


    /**
     * @param output     A path to a file or network resource, see FFmpeg documentation for details https://ffmpeg.org/ffmpeg-protocols.html#Protocols
     */
    public FFmpegOutput(String output) {
        this.output = output;
    }


    /**
     * Stop writing the output after its duration reaches duration.
     * {@link #setDuration(float)} and {@link #setEndPosition(float)} are mutually exclusive and {@link #setDuration(float)} has priority.
     *
     * @param duration duration in seconds
     */
    public void setDuration(float duration) {
        this.duration = duration;
    }
    /**
     * Decodes but discards input until the timestamps reach position.
     *
     * @param position Position in seconds
     */
    public void setStartPosition(float position) {
        this.positionStart = position;
    }
    /**
     * Stop writing the output at position.
     * {@link #setDuration(float)} and {@link #setEndPosition(float)} are mutually exclusive and {@link #setDuration(float)} has priority.
     *
     * @param position Position in seconds
     */
    public void setEndPosition(float position) {
        this.positionEnd = position;
    }

    /**
     * Set the file size limit.
     *
     * @param size Size of the file in bytes
     */
    public void setTargetFileSize(long size) {
        this.fileSize = size;
    }

    /**
     * Select the pass number (1 or 2). It is used to do two-pass video encoding.
     *
     * @param pass THe pass number
     */
    public void setEncodingPass(int pass) {
        this.encodingPass = pass;
    }

    /**
     * Add additional args that may not be supported by the API, these values will be inserted to the command line as is.
     *
     * @param args a list of FFmpeg arguments
     */
    public void addAdditionalArg(String... args) {
        additionalArgs.addAll(Arrays.asList(args));
    }

    // ----------------------------------------------------
    // Video Options
    // ----------------------------------------------------

    /**
     * Select an encoder.
     *
     * @param codec Codec name supported by FFmpeg
     */
    public void setVideoCodec(FFmpegVideoCodec codec) {
        this.videoCodec = codec.toString();
    }
    /**
     * Select an encoder.
     *
     * @param codec Codec name supported by FFmpeg
     */
    public void setVideoCodec(String codec) {
        this.videoCodec = codec;
    }

    /**
     * Duplicate or drop input frames to achieve provided constant output frame rate (fps).
     *
     * @param frameRate Frame rate in Hz
     */
    public void setVideoFrameRate(float frameRate) {
        this.videoFrameRate = frameRate;
    }

    /**
     * Set the target resolution of the output file.
     *
     * @param width The width in pixels
     * @param height The height in pixels
     */
    public void setVideoResolution(int width, int height) {
        this.videoWidth = width;
        this.videoHeight = height;
    }

    /**
     * Set constant video bitrate.
     *
     * @param bitrate Rate in number of bits
     */
    public void setVideoBitRate(int bitrate) {
        this.videoBitRate = bitrate;
    }

    /**
     * Use fixed quality scale (VBR).
     *
     * @param vbr The meaning of q/qscale is codec-dependent.
     */
    public void setVideoQuality(int vbr) {
        this.videoQuality = vbr;
    }

    // ----------------------------------------------------
    // Audio Options
    // ----------------------------------------------------

    /**
     * Select an encoder.
     *
     * @param codec Codec name supported by FFmpeg
     */
    public void setAudioCodec(FFmpegAudioCodec codec) {
        this.audioCodec = codec.toString();
    }
    /**
     * Select an encoder.
     *
     * @param codec Codec name supported by FFmpeg
     */
    public void setAudioCodec(String codec) {
        this.audioCodec = codec;
    }

    /**
     * Set the audio sampling frequency. For output streams it is set by default to the frequency of the corresponding input stream.
     *
     * @param sampleRate Audio sampling frequency
     */
    public void setAudioSampleRate(int sampleRate) {
        this.audioSampleRate = sampleRate;
    }

    /**
     * Set the number of audio channels. For output streams it is set by default to the number of input audio channels.
     *
     * @param channels Number of audio channels in output file
     */
    public void setAudioChannels(int channels) {
        this.audioChannels = channels;
    }

    /**
     * Set constant audio bitrate
     *
     * @param bitrate Rate in number of bits
     */
    public void setAudioBitRate(int bitrate) {
        this.audioBitRate = bitrate;
    }

    /**
     * Use fixed quality scale (VBR).
     *
     * @param vbr The meaning of q/qscale is codec-dependent.
     */
    public void setAudioQuality(int vbr) {
        this.audioQuality = vbr;
    }

    /**
     * Enable or Disables audio recording in output file. (enabled by default)
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
     * Select an encoder.
     *
     * @param codec Codec name supported by FFmpeg
     */
    public void setSubtitleCodec(FFmpegSubtitleCodec codec) {
        this.subtitleCodec = codec.toString();
    }
    /**
     * Select an encoder.
     *
     * @param codec Codec name supported by FFmpeg
     */
    public void setSubtitleCodec(String codec) {
        this.subtitleCodec = codec;
    }

    /**
     * Enable or Disables subtitles in output file. (enabled by default)
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
        if (fileSize != null)
            command.append(" -fs ").append(fileSize);

        if (encodingPass != null)
            command.append(" -pass ").append(encodingPass);

        // ----------------------------------------------------
        // Video Options
        // ----------------------------------------------------

        if (videoCodec != null)
            command.append(" -codec:v ").append(videoCodec);
        if (videoFrameRate != null)
            command.append(" -frames ").append(videoFrameRate);
        if (videoWidth != null && videoHeight != null)
            command.append(" -filter:v scale=").append(videoWidth).append(':').append(videoHeight);
        if (videoBitRate != null)
            command.append(" -b:v ").append(videoBitRate);
        if (videoQuality != null)
            command.append(" -qscale:v ").append(videoQuality);

        // ----------------------------------------------------
        // Audio Options
        // ----------------------------------------------------

        if (audioCodec != null)
            command.append(" -codec:a ").append(audioCodec);
        if (audioSampleRate != null)
            command.append(" -ar ").append(audioSampleRate);
        if (audioChannels != null)
            command.append(" -ac ").append(audioChannels);
        if (audioBitRate != null)
            command.append(" -b:a ").append(audioBitRate);
        if (audioQuality != null)
            command.append(" -qscale:a ").append(audioQuality);

        if (!audioEnabled)
            command.append(" -an");

        // ----------------------------------------------------
        // Subtitle Options
        // ----------------------------------------------------

        if (subtitleCodec != null)
            command.append(" -codec:s ").append(subtitleCodec);

        if (!subtitleEnabled)
            command.append(" -sn");

        command.append(StringUtil.join(" ", additionalArgs));
        command.append(" \"").append(output).append("\"");
        return command.toString().trim();
    }
}