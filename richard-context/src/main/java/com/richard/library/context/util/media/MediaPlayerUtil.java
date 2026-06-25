package com.richard.library.context.util.media;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.annotation.IntRange;

import com.richard.library.context.AppContext;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * <pre>
 * Description : 媒体播放器工具类
 * Author : admin-richard
 * Date : 2021/4/13 16:41
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2021/4/13 16:41     admin-richard         new file.
 * </pre>
 * <p>
 * 在有些设备中播放短时音频会出现播放不出声的问题，播放短时音频建议用SoundPool
 */
public final class MediaPlayerUtil {

    private MediaPlayer mediaPlayer;
    private LinkedBlockingQueue<AudioItem> playQueue;
    private boolean isPlayFinish = true;
    private OnPlayFinishListener onPlayFinishListener;

    public MediaPlayerUtil() {

    }

    public MediaPlayerUtil(boolean isQueueMode) {
        if (isQueueMode) {
            playQueue = new LinkedBlockingQueue<>();
        }
    }

    /**
     * 初始化新的播放器实例
     */
    private void initNewPlayer(int audioSourceType) {
        this.releaseMediaPlayer();

        mediaPlayer = new MediaPlayer();
        this.setAudioSourceType(audioSourceType);
        mediaPlayer.setLooping(false);
    }

    /**
     * 设置音频资源类型
     */
    private void setAudioSourceType(int audioSourceType) {
        //设置Audio流内容类型
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build());
        } else {
            switch (audioSourceType) {
                case AudioSourceType.PATH_URL:
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    break;
                case AudioSourceType.RAW_ID:
                case AudioSourceType.ASSETS_FD:
                default:
                    mediaPlayer.setAudioStreamType(AudioManager.USE_DEFAULT_STREAM_TYPE);
            }
        }
    }

    /**
     * MediaPlayer播放完成回调
     */
    private final MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            if (playQueue == null || playQueue.isEmpty()) {
                releaseMediaPlayer();
                invokeOnPlayFinishListener();
                return;
            }

            play(playQueue.poll());
        }
    };

    /**
     * 音频准备事件回调
     */
    private final MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            if (mediaPlayer != mp) {
                return;
            }
            try {
                mp.start();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    };

    /**
     * 释放mediaPlayer资源并置null
     */
    private synchronized void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            try {
                mediaPlayer.reset();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }

    /**
     * 添加播放
     */
    public void addPlay(List<AudioItem> audioList) {
        if (audioList == null || audioList.isEmpty()) {
            return;
        }
        for (AudioItem item : audioList) {
            this.addPlay(item);
        }
    }

    /**
     * 添加播放
     */
    public synchronized void addPlay(AudioItem audio) {
        if (playQueue == null) {
            throw new IllegalArgumentException("当前音频播放不是队列模式");
        }

        if (isPlayFinish) {
            this.play(audio);
            return;
        }
        playQueue.offer(audio);
    }

    /**
     * 添加播放
     */
    public synchronized void play(AudioItem audio) {
        this.isPlayFinish = false;
        try {
            if (mediaPlayer == null) {
                initNewPlayer(audio.getMediaSourceType());
            } else {
                mediaPlayer.reset();
                this.setAudioSourceType(audio.getMediaSourceType());
            }

            mediaPlayer.setOnCompletionListener(onCompletionListener);
            mediaPlayer.setOnPreparedListener(onPreparedListener);

            switch (audio.getMediaSourceType()) {
                case AudioSourceType.RAW_ID:
                    try (AssetFileDescriptor fileDescriptor = AppContext.getResources().openRawResourceFd(Integer.parseInt(audio.getSource()))) {
                        mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                                fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                    }
                    break;
                case AudioSourceType.PATH_URL:
                    mediaPlayer.setDataSource(audio.getSource());
                    break;
                case AudioSourceType.ASSETS_FD:
                default:
                    try (AssetFileDescriptor fileDescriptor = AppContext.get().getAssets().openFd(audio.getSource())) {
                        mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                                fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                    }
            }

            mediaPlayer.prepareAsync();
        } catch (Throwable e) {
            onCompletionListener.onCompletion(mediaPlayer);
            e.printStackTrace();
        }
    }

    /**
     * 获取媒体文件播放总共时长(耗时操作)
     *
     * @param source          音频源地址
     * @param audioSourceType 音频源类型-详见MediaPlayerUtil.MediaSourceType
     * @return 毫秒数
     */
    public long getDuration(String source, @IntRange(from = 0, to = 2) int audioSourceType) {
        return this.getDuration(audioSourceType, source);
    }

    /**
     * 获取媒体文件播放总共时长(耗时操作)
     *
     * @param audioSourceType 音频源类型，详见AudioSourceType
     * @param source          音频源地址
     * @return 毫秒数
     */
    public long getDuration(int audioSourceType, String source) {
        if (mediaPlayer == null) {
            initNewPlayer(audioSourceType);
        } else {
            mediaPlayer.reset();
            this.setAudioSourceType(audioSourceType);
        }

        try {
            switch (audioSourceType) {
                case AudioSourceType.RAW_ID:
                    try (AssetFileDescriptor fileDescriptor = AppContext.getResources().openRawResourceFd(Integer.parseInt(source))) {
                        mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                                fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                    }
                    break;
                case AudioSourceType.PATH_URL:
                    mediaPlayer.setDataSource(source);
                    break;
                case AudioSourceType.ASSETS_FD:
                default:
                    try (AssetFileDescriptor fileDescriptor = AppContext.get().getAssets().openFd(source)) {
                        mediaPlayer.setDataSource(fileDescriptor.getFileDescriptor(),
                                fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                    }
            }
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        long result = mediaPlayer.getDuration();
        this.releaseMediaPlayer();
        return result;
    }

    /**
     * 停止播放
     */
    public void stop() {
        try {
            if (playQueue != null) {
                playQueue.clear();
            }
            this.releaseMediaPlayer();
            invokeOnPlayFinishListener();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        try {
            if (playQueue != null) {
                playQueue.clear();
            }
            isPlayFinish = true;
            this.releaseMediaPlayer();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用播放语音结束回调事件（播放过程中出现错误，中断，都会回调）
     */
    private void invokeOnPlayFinishListener() {
        isPlayFinish = true;
        if (onPlayFinishListener != null) {
            onPlayFinishListener.onPlayFinish();
        }
    }

    public void setOnPlayFinishListener(OnPlayFinishListener onPlayFinishListener) {
        this.onPlayFinishListener = onPlayFinishListener;
    }

    public interface OnPlayFinishListener {

        /**
         * 播放结束时回调
         */
        void onPlayFinish();

    }
}
