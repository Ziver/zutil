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

public final class FFmpegConstants {

    public enum FFmpegLogLevel {
        /** Show nothing at all; be silent. **/
        QUIET,
        /** Only show fatal errors which could lead the process to crash, such as an assertion failure. This is not currently used for anything. **/
        PANIC,
        /** Only show fatal errors. These are errors after which the process absolutely cannot continue.**/
        FATAL,
        /** Show all errors, including ones which can be recovered from. **/
        ERROR,
        /** Show all warnings and errors. Any message related to possibly incorrect or unexpected events will be shown. **/
        WARNING,
        /** Show informative messages during processing. This is in addition to warnings and errors. This is the default value. **/
        INFO,
        /** Same as info, except more verbose. **/
        VERBOSE,
        /** Show everything, including debugging information. **/
        DEBUG,
        TRACE
    }

    public enum FFmpegHwDevice {
        cuda,
        dxva2,
        vaapi,
        vdpau,
        qsv,
        opencl,
        vulkan
    }

    public enum FFmpegVideoCodec {
        /** Multicolor charset for Commodore 64 (encoders: a64multi ) **/
        a64multi,
        /** Multicolor charset for Commodore 64, extended with 5th color (colram) (encoders: a64multi5 )**/
        a64multi5,
        /** Alias/Wavefront PIX image **/
        alias_pix,
        /** AMV Video **/
        amv,
        /** APNG (Animated Portable Network Graphics) image **/
        apng,
        /** ASUS V1 **/
        asv1,
        /** ASUS V2 **/
        asv2,
        /** Alliance for Open Media AV1 (decoders: libdav1d libaom-av1 ) (encoders: libaom-av1 librav1e ) **/
        libaom_av1,
        librav1e,
        /** Avid 1:1 10-bit RGB Packer **/
        avrp,
        /** AVS2-P2/IEEE1857.4 (decoders: libdavs2 ) (encoders: libxavs2 ) **/
        libxavs2,
        /** Avid Meridien Uncompressed **/
        avui,
        /** Uncompressed packed MS 4:4:4:4 **/
        ayuv,
        /** BMP (Windows and OS/2 bitmap) **/
        bmp,
        /** Cinepak **/
        cinepak,
        /** Cirrus Logic AccuPak **/
        cljr,
        /** Copy input to output without modification **/
        copy,
        /** Dirac (encoders: vc2 ) **/
        vc2,
        /** VC3/DNxHD **/
        dnxhd,
        /** DPX (Digital Picture Exchange) image **/
        dpx,
        /** DV (Digital Video) **/
        dvvideo,
        /** FFmpeg video codec #1 **/
        ffv1,
        /** Huffyuv FFmpeg variant **/
        ffvhuff,
        /** FITS (Flexible Image Transport System) **/
        fits,
        /** Flash Screen Video v1 **/
        flashsv,
        /** Flash Screen Video v2 **/
        flashsv2,
        /** FLV / Sorenson Spark / Sorenson H.263 (Flash Video) (decoders: flv ) (encoders: flv ) **/
        flv,
        /** CompuServe GIF (Graphics Interchange Format) **/
        gif,
        /** H.261 **/
        h261,
        /** H.263 / H.263-1996, H.263+ / H.263-1998 / H.263 version 2 **/
        h263,
        /** H.263+ / H.263-1998 / H.263 version 2 **/
        h263p,
        /** H.264 / AVC / MPEG-4 AVC / MPEG-4 part 10 (decoders: h264 h264_qsv h264_cuvid ) (encoders: libx264 libx264rgb h264_amf h264_mf h264_nvenc h264_qsv nvenc nvenc_h264 ) **/
        libx264,
        libx264rgb,
        h264_amf,
        h264_mf,
        h264_nvenc,
        h264_qsv,
        nvenc,
        nvenc_h264,
        /** H.265 / HEVC (High Efficiency Video Coding) (decoders: hevc hevc_qsv hevc_cuvid ) (encoders: libx265 nvenc_hevc hevc_amf hevc_mf hevc_nvenc hevc_qsv ) **/
        libx265,
        nvenc_hevc,
        hevc_amf,
        hevc_mf,
        hevc_nvenc,
        hevc_qsv,
        /** HuffYUV **/
        huffyuv,
        /** JPEG 2000 (decoders: jpeg2000 libopenjpeg ) (encoders: jpeg2000 libopenjpeg ) **/
        jpeg2000,
        libopenjpeg,
        /** JPEG-LS **/
        jpegls,
        /** Lossless JPEG **/
        ljpeg,
        /** MagicYUV video **/
        magicyuv,
        /** Motion JPEG (decoders: mjpeg mjpeg_cuvid mjpeg_qsv ) (encoders: mjpeg mjpeg_qsv ) **/
        mjpeg,
        mjpeg_qsv,
        /** MPEG-1 video (decoders: mpeg1video mpeg1_cuvid ) **/
        mpeg1video,
        /** MPEG-2 video (decoders: mpeg2video mpegvideo mpeg2_qsv mpeg2_cuvid ) (encoders: mpeg2video mpeg2_qsv ) **/
        mpeg2video,
        mpeg2_qsv,
        /** MPEG-4 part 2 (decoders: mpeg4 mpeg4_cuvid ) (encoders: mpeg4 libxvid ) **/
        mpeg4,
        libxvid,
        /** MPEG-4 part 2 Microsoft variant version 2 **/
        msmpeg4v2,
        /** MPEG-4 part 2 Microsoft variant version 3 (decoders: msmpeg4 ) (encoders: msmpeg4 ) **/
        msmpeg4,
        /** Microsoft Video 1 **/
        msvideo1,
        /** PAM (Portable AnyMap) image **/
        pam,
        /** PBM (Portable BitMap) image **/
        pbm,
        /** PC Paintbrush PCX image **/
        pcx,
        /** PGM (Portable GrayMap) image **/
        pgm,
        /** PGMYUV (Portable GrayMap YUV) image **/
        pgmyuv,
        /** PNG (Portable Network Graphics) image **/
        png,
        /** PPM (Portable PixelMap) image **/
        ppm,
        /** Apple ProRes (iCodec Pro) (encoders: prores prores_aw prores_ks ) **/
        prores,
        prores_aw,
        prores_ks,
        /** QuickTime Animation (RLE) video **/
        qtrle,
        /** AJA Kona 10-bit RGB Codec **/
        r10k,
        /** Uncompressed RGB 10-bit **/
        r210,
        /** raw video **/
        rawvideo,
        /** id RoQ video (decoders: roqvideo ) (encoders: roqvideo ) **/
        roqvideo,
        /** RealVideo 1.0 **/
        rv10,
        /** RealVideo 2.0 **/
        rv20,
        /** SGI image **/
        sgi,
        /** Snow **/
        snow,
        /** Sun Rasterfile image **/
        sunrast,
        /** Sorenson Vector Quantizer 1 / Sorenson Video 1 / SVQ1 **/
        svq1,
        /** Truevision Targa image **/
        targa,
        /** Theora (encoders: libtheora ) **/
        libtheora,
        /** TIFF image **/
        tiff,
        /** Ut Video **/
        utvideo,
        /** Uncompressed 4:2:2 10-bit **/
        v210,
        /** Uncompressed packed 4:4:4 **/
        v308,
        /** Uncompressed packed QT 4:4:4:4 **/
        v408,
        /** Uncompressed 4:4:4 10-bit **/
        v410,
        /** On2 VP8 (decoders: vp8 libvpx vp8_cuvid vp8_qsv ) (encoders: libvpx ) **/
        libvpx,
        /** Google VP9 (decoders: vp9 libvpx-vp9 vp9_cuvid vp9_qsv ) (encoders: libvpx-vp9 vp9_qsv ) **/
        libvpx_vp9,
        vp9_qsv,
        /** WebP (encoders: libwebp_anim libwebp ) **/
        libwebp_anim,
        libwebp,
        /** Windows Media Video 7 **/
        wmv1,
        /** Windows Media Video 8 **/
        wmv2,
        /** AVFrame to AVPacket passthrough **/
        wrapped_avframe,
        /** XBM (X BitMap) image **/
        xbm,
        /** X-face image **/
        xface,
        /** XWD (X Window Dump) image **/
        xwd,
        /** Uncompressed YUV 4:1:1 12-bit **/
        y41p,
        /** Uncompressed packed 4:2:0 **/
        yuv4,
        /** LCL (LossLess Codec Library) ZLIB **/
        zlib,
        /** Zip Motion Blocks Video **/
        zmbv
    }

    public enum FFmpegAudioCodec {
        /** AAC (Advanced Audio Coding) (decoders: aac aac_fixed ) (encoders: aac aac_mf ) **/
        aac,
        aac_mf,
        /** ATSC A/52A (AC-3) (decoders: ac3 ac3_fixed ) (encoders: ac3 ac3_fixed ac3_mf ) **/
        ac3,
        ac3_fixed,
        ac3_mf,
        /** SEGA CRI ADX ADPCM **/
        adpcm_adx,
        /** G.722 ADPCM (decoders: g722 ) (encoders: g722 ) **/
        g722,
        /** G.726 ADPCM (decoders: g726 ) (encoders: g726 ) **/
        g726,
        /** G.726 ADPCM little-endian (decoders: g726le ) (encoders: g726le ) **/
        g726le,
        /** ADPCM IMA QuickTime **/
        adpcm_ima_qt,
        /** ADPCM IMA Simon & Schuster Interactive **/
        adpcm_ima_ssi,
        /** ADPCM IMA WAV **/
        adpcm_ima_wav,
        /** ADPCM Microsoft **/
        adpcm_ms,
        /** ADPCM Shockwave Flash **/
        adpcm_swf,
        /** ADPCM Yamaha **/
        adpcm_yamaha,
        /** ALAC (Apple Lossless Audio Codec) **/
        alac,
        /** AMR-NB (Adaptive Multi-Rate NarrowBand) (decoders: amrnb libopencore_amrnb ) (encoders: libopencore_amrnb ) **/
        libopencore_amrnb,
        /** aptX (Audio Processing Technology for Bluetooth) **/
        aptx,
        /** aptX HD (Audio Processing Technology for Bluetooth) **/
        aptx_hd,
        /** Copy input to output without modification **/
        copy,
        /** RFC 3389 Comfort Noise **/
        comfortnoise,
        /** DCA (DTS Coherent Acoustics) (decoders: dca ) (encoders: dca ) **/
        dca,
        /** ATSC A/52B (AC-3, E-AC-3) **/
        eac3,
        /** FLAC (Free Lossless Audio Codec) **/
        flac,
        /** G.723.1 **/
        g723_1,
        /** MLP (Meridian Lossless Packing) **/
        mlp,
        /** MP2 (MPEG audio layer 2) (decoders: mp2 mp2float ) (encoders: mp2 mp2fixed libtwolame ) **/
        mp2,
        /** MP3 (MPEG audio layer 3) (decoders: mp3float mp3 ) (encoders: libmp3lame mp3_mf ) **/
        libmp3lame,
        mp3_mf,
        /** Nellymoser Asao **/
        nellymoser,
        /** Opus (Opus Interactive Audio Codec) (decoders: opus libopus ) (encoders: opus libopus ) **/
        opus,
        libopus,
        /** PCM A-law / G.711 A-law **/
        pcm_alaw,
        /** PCM signed 20|24-bit big-endian **/
        pcm_dvd,
        /** PCM 32-bit floating point big-endian **/
        pcm_f32be,
        /** PCM 32-bit floating point little-endian **/
        pcm_f32le,
        /** PCM 64-bit floating point big-endian **/
        pcm_f64be,
        /** PCM 64-bit floating point little-endian **/
        pcm_f64le,
        /** PCM mu-law / G.711 mu-law **/
        pcm_mulaw,
        /** PCM signed 16-bit big-endian **/
        pcm_s16be,
        /** PCM signed 16-bit big-endian planar **/
        pcm_s16be_planar,
        /** PCM signed 16-bit little-endian **/
        pcm_s16le,
        /** PCM signed 16-bit little-endian planar **/
        pcm_s16le_planar,
        /** PCM signed 24-bit big-endian **/
        pcm_s24be,
        /** PCM D-Cinema audio signed 24-bit **/
        pcm_s24daud,
        /** PCM signed 24-bit little-endian **/
        pcm_s24le,
        /** PCM signed 24-bit little-endian planar **/
        pcm_s24le_planar,
        /** PCM signed 32-bit big-endian **/
        pcm_s32be,
        /** PCM signed 32-bit little-endian **/
        pcm_s32le,
        /** PCM signed 32-bit little-endian planar **/
        pcm_s32le_planar,
        /** PCM signed 64-bit big-endian **/
        pcm_s64be,
        /** PCM signed 64-bit little-endian **/
        pcm_s64le,
        /** PCM signed 8-bit **/
        pcm_s8,
        /** PCM signed 8-bit planar **/
        pcm_s8_planar,
        /** PCM unsigned 16-bit big-endian **/
        pcm_u16be,
        /** PCM unsigned 16-bit little-endian **/
        pcm_u16le,
        /** PCM unsigned 24-bit big-endian **/
        pcm_u24be,
        /** PCM unsigned 24-bit little-endian **/
        pcm_u24le,
        /** PCM unsigned 32-bit big-endian **/
        pcm_u32be,
        /** PCM unsigned 32-bit little-endian **/
        pcm_u32le,
        /** PCM unsigned 8-bit **/
        pcm_u8,
        /** PCM Archimedes VIDC **/
        pcm_vidc,
        /** RealAudio 1.0 (14.4K) (decoders: real_144 ) (encoders: real_144 ) **/
        real_144,
        /** DPCM id RoQ **/
        roq_dpcm,
        /** SMPTE 302M **/
        s302m,
        /** SBC (low-complexity subband codec) **/
        sbc,
        /** Sonic **/
        sonic,
        /** Sonic lossless **/
        sonicls,
        /** TrueHD **/
        truehd,
        /** TTA (True Audio) **/
        tta,
        /** Vorbis (decoders: vorbis libvorbis ) (encoders: vorbis libvorbis ) **/
        vorbis,
        libvorbis,
        /** WavPack **/
        wavpack,
        /** Windows Media Audio 1 **/
        wmav1,
        /** Windows Media Audio 2 **/
        wmav2
    }

    public enum FFmpegSubtitleCodec {
        /** ASS (Advanced SSA) subtitle (decoders: ssa ass ) (encoders: ssa ass) **/
        ass,
        ssa,
        /** DVB subtitles (decoders: dvbsub ) (encoders: dvbsub ) **/
        dvbsub,
        /** DVD subtitles (decoders: dvdsub ) (encoders: dvdsub ) **/
        dvdsub,
        /** MOV text **/
        mov_text,
        /** SubRip subtitle (decoders: srt subrip ) (encoders: srt subrip ) **/
        srt,
        subrip,
        /** raw UTF-8 text **/
        text,
        /** WebVTT subtitle **/
        webvtt,
        /** XSUB **/
        xsub
    }
}
