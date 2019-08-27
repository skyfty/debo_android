package com.qcwl.debo.ui.found.jinshanyun;

import com.ksyun.media.streamer.encoder.VideoEncodeFormat;
import com.ksyun.media.streamer.framework.AVConst;
import com.ksyun.media.streamer.kit.StreamerConstants;

/**
 * 录制和输出参数类
 */

public class ShortVideoConfig {
    public boolean isLandscape = false;
    public float fps = StreamerConstants.DEFAULT_TARGET_FPS;
    public int resolution = StreamerConstants.VIDEO_RESOLUTION_480P;
    public int videoBitrate = StreamerConstants.DEFAULT_INIT_VIDEO_BITRATE;
    public int audioBitrate = StreamerConstants.DEFAULT_AUDIO_BITRATE;
    public int encodeType = AVConst.CODEC_ID_AVC;
    public int encodeMethod = StreamerConstants.ENCODE_METHOD_SOFTWARE;
    public int encodeProfile = VideoEncodeFormat.ENCODE_PROFILE_LOW_POWER;
    public int decodeMethod = StreamerConstants.DECODE_METHOD_HARDWARE;
    public int videoCRF = 24;
    public int audioChannel = 1;
    public int audioSampleRate = 44100;
    public int width = 480;
    public int height = 480;
}
