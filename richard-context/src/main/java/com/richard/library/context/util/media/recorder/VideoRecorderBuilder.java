package com.richard.library.context.util.media.recorder;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;

/**
 * 视频录制构建器，继承AudioRecorderBuilder复用全部音频参数与方法
 * 内置所有视频独有参数，支持系统全部视频编码/封装格式
 */
public class VideoRecorderBuilder extends AudioRecorderBuilder {
    // ===================== 内置全部视频专属参数 =====================
    /** Camera1实例，视频必填 */
    protected Camera camera;
    /** 视频采集源，仅CAMERA=1 */
    protected int videoSource = MediaRecorder.VideoSource.CAMERA;
    /**
     * 视频编码器全量支持
     * H264=2 / MPEG_4_SP=3 / VP8=4(4.3+) / VP9=5(Android10+) / HEVC=6(Android7+)
     */
    protected int videoEncoder = MediaRecorder.VideoEncoder.H264;
    /** 视频分辨率宽，必须为Camera支持尺寸 */
    protected int videoWidth = 1280;
    /** 视频分辨率高 */
    protected int videoHeight = 720;
    /** 帧率 15/24/30/60 依赖硬件 */
    protected int frameRate = 30;
    /** 视频比特率 bps 720P 3M~6M 1080P 6M~12M */
    protected int videoBitRate = 4000000;
    /** 画面旋转 0/90/180/270 */
    protected int orientationHint = 90;
    /** 模式标记：false=视频模式 */
    protected final boolean isAudioMode = false;

    public VideoRecorderBuilder(Context context, Camera camera) {
        super(context);
        this.camera = camera;
    }

    // ========== 视频专属链式方法 ==========
    public VideoRecorderBuilder videoSource(int source) {
        this.videoSource = source;
        return this;
    }

    public VideoRecorderBuilder videoEncoder(int encoder) {
        this.videoEncoder = encoder;
        return this;
    }

    public VideoRecorderBuilder videoSize(int width, int height) {
        this.videoWidth = width;
        this.videoHeight = height;
        return this;
    }

    public VideoRecorderBuilder frameRate(int fps) {
        this.frameRate = fps;
        return this;
    }

    public VideoRecorderBuilder videoBitRate(int bitRate) {
        this.videoBitRate = bitRate;
        return this;
    }

    public VideoRecorderBuilder orientationHint(int degree) {
        this.orientationHint = degree;
        return this;
    }

    @Override
    public boolean isAudioMode() {
        return isAudioMode;
    }
}