package com.richard.library.context.util.media;

import android.media.MediaRecorder;

import com.richard.library.context.util.FileUtil;

import java.io.File;

/**
 * <pre>
 * Description : 音频采集
 * Author : admin-richard
 * Date : 2021-07-19 15:51
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2021-07-19 15:51     admin-richard         new file.
 * </pre>
 */
public class MediaRecorderUtil {

    private MediaRecorder mediaRecorder;
    private final File tempFile = new File(FileUtil.getCacheDir(), "recorder_temp.m4a");

    /**
     * 开始录音
     */
    public void startRecord() {
        try {
            if (tempFile.exists()) {
                tempFile.delete();
            }

            if(mediaRecorder == null){
                mediaRecorder = new MediaRecorder();
            }

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mediaRecorder.setOutputFile(tempFile.getAbsolutePath());
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    /**
     * 获取录音文件
     */
    public File getRecordFile() {
        return tempFile;
    }

    /**
     * 释放相关资源
     */
    public void release() {
        if(mediaRecorder == null){
            return;
        }
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
        } catch (RuntimeException e) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}