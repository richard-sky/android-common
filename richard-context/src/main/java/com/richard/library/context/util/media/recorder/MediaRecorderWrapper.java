package com.richard.library.context.util.media.recorder;

import android.hardware.Camera;
import android.media.MediaRecorder;

import java.io.File;

/**
 * MediaRecorder全局单例管理器
 * 直接读取AudioRecorderBuilder/VideoRecorderBuilder内置参数，无独立Param实体
 * 支持系统全部音视频封装、编码格式
 * <p>
 * 录音使用示例：
 * // 支持全部音频封装格式：MPEG4/OGG/AMR_NB/WEBM/TS
 * AudioRecorderBuilder audioBuilder = new AudioRecorderBuilder(this)
 * .outputFormat(MediaRecorder.OutputFormat.OGG)
 * .audioEncoder(MediaRecorder.AudioEncoder.OPUS)
 * .audioSampleRate(48000)
 * .audioBitRate(256000);
 * <p>
 * MediaRecorderWrapper wrapper = MediaRecorderWrapper.getInstance();
 * if (wrapper.prepare(audioBuilder)) {
 * wrapper.start();
 * }
 * // audioBuilder.videoSize(1280,720); 编译报错，不存在该方法
 * <p>
 * 录视频使用示例：
 * // mCamera 已 open + unlock
 * VideoRecorderBuilder videoBuilder = new VideoRecorderBuilder(this, mCamera)
 * // 音频配置
 * .outputFormat(MediaRecorder.OutputFormat.WEBM)
 * .audioBitRate(128000)
 * // 全量视频自定义
 * .videoEncoder(MediaRecorder.VideoEncoder.VP8)
 * .videoSize(1280,720)
 * .frameRate(30)
 * .videoBitRate(6000000)
 * .orientationHint(90);
 * <p>
 * MediaRecorderWrapper wrapper = MediaRecorderWrapper.getInstance();
 * if (wrapper.prepare(videoBuilder)) {
 * wrapper.start();
 * }
 * <p>
 * <p>
 * <p>
 * 停止与页面销毁：
 * File recordFile = MediaRecorderWrapper.getInstance().stop();
 *
 * @Override protected void onDestroy() {
 * super.onDestroy();
 * MediaRecorderWrapper.getInstance().release();
 * if(mCamera != null){
 * mCamera.release();
 * mCamera = null;
 * }
 * }
 */
public class MediaRecorderWrapper {
    private static volatile MediaRecorderWrapper sInstance;
    private MediaRecorder mRecorder;
    private String mSavePath;

    private MediaRecorderWrapper() {
    }

    public static MediaRecorderWrapper getInstance() {
        if (sInstance == null) {
            synchronized (MediaRecorderWrapper.class) {
                if (sInstance == null) {
                    sInstance = new MediaRecorderWrapper();
                }
            }
        }
        return sInstance;
    }

    // region 重载prepare：纯音频构建器
    public boolean prepare(AudioRecorderBuilder audioBuilder) {
        release();
        String outputPath = audioBuilder.buildOutputFilePath();
        mSavePath = outputPath;
        try {
            mRecorder = new MediaRecorder();
            // 音频全套配置
            mRecorder.setAudioSource(audioBuilder.audioSource);
            mRecorder.setOutputFormat(audioBuilder.outputFormat);
            mRecorder.setAudioEncoder(audioBuilder.audioEncoder);
            mRecorder.setAudioSamplingRate(audioBuilder.audioSampleRate);
            mRecorder.setAudioEncodingBitRate(audioBuilder.audioBitRate);
            mRecorder.setOutputFile(outputPath);
            mRecorder.prepare();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            release();
            return false;
        }
    }
    // endregion

    // region 重载prepare：视频构建器
    public boolean prepare(VideoRecorderBuilder videoBuilder) {
        release();
        String outputPath = videoBuilder.buildOutputFilePath();
        mSavePath = outputPath;
        Camera camera = videoBuilder.camera;
        if (camera == null) {
            throw new IllegalArgumentException("视频录制必须传入已解锁Camera实例");
        }
        try {
            mRecorder = new MediaRecorder();
            mRecorder.setCamera(camera);
            // 视频参数
            mRecorder.setVideoSource(videoBuilder.videoSource);
            mRecorder.setVideoEncoder(videoBuilder.videoEncoder);
            mRecorder.setVideoSize(videoBuilder.videoWidth, videoBuilder.videoHeight);
            mRecorder.setVideoFrameRate(videoBuilder.frameRate);
            mRecorder.setVideoEncodingBitRate(videoBuilder.videoBitRate);
            mRecorder.setOrientationHint(videoBuilder.orientationHint);
            // 音频参数（继承父类）
            mRecorder.setAudioSource(videoBuilder.audioSource);
            mRecorder.setAudioEncoder(videoBuilder.audioEncoder);
            mRecorder.setAudioSamplingRate(videoBuilder.audioSampleRate);
            mRecorder.setAudioEncodingBitRate(videoBuilder.audioBitRate);
            // 输出封装格式（支持全系统格式）
            mRecorder.setOutputFormat(videoBuilder.outputFormat);
            mRecorder.setOutputFile(outputPath);
            mRecorder.prepare();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            release();
            return false;
        }
    }
    // endregion

    /**
     * 开始录制
     */
    public void start() {
        if (mRecorder != null) {
            mRecorder.start();
        }
    }

    /**
     * 停止录制，返回生成文件
     */
    public File stop() {
        File file = null;
        if (mRecorder == null) return null;
        try {
            mRecorder.stop();
            file = new File(mSavePath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            release();
        }
        return file;
    }

    /**
     * 释放MediaRecorder，释放麦克风/相机占用
     */
    public void release() {
        if (mRecorder != null) {
            try {
                mRecorder.stop();
                mRecorder.release();
                mRecorder = null;
            } catch (RuntimeException e) {
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
            }
        }
        mSavePath = null;
    }


    /**
     * 获取当前录制文件路径
     */
    public String getCurrentPath() {
        return mSavePath;
    }
}