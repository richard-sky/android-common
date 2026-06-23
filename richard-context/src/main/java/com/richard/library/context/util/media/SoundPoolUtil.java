package com.richard.library.context.util.media;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.SparseLongArray;

import com.richard.library.context.util.ThreadUtil;
import com.richard.library.context.AppContext;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * <pre>
 * Description : 媒体播放器工具类(播放短时本地音频)
 * Author : admin-richard
 * Date : 2021/4/13 16:42
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2021/4/13 16:42     admin-richard         new file.
 * </pre>
 */
public final class SoundPoolUtil {

    private SoundPool soundPool;
    private final LinkedBlockingQueue<List<AudioItem>> playQueue = new LinkedBlockingQueue<>();
    private volatile boolean isPlayFinish = true;
    private OnPlayFinishListener onPlayFinishListener;
    private final MediaPlayerUtil mediaPlayerUtil = new MediaPlayerUtil();
    private ThreadUtil.RunTask playTask;
    private final SparseLongArray soundInfoMap = new SparseLongArray();

    /**
     * 初始化新的播放器实例
     */
    private void initParams() {
        this.releaseMediaPlayer();
        //sdk版本21是SoundPool 的一个分水岭
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入最多播放音频数量,
            builder.setMaxStreams(1);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        } else {
            /**
             * 第一个参数：int maxStreams：SoundPool对象的最大并发流数
             * 第二个参数：int streamType：AudioManager中描述的音频流类型
             *第三个参数：int srcQuality：采样率转换器的质量。 目前没有效果。 使用0作为默认值。
             */
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            if (soundInfoMap.size() == 0 || sampleId != soundInfoMap.keyAt(soundInfoMap.size() - 1)) {
                return;
            }

            ThreadUtil.executeBySingle(new ThreadUtil.RunTask() {
                @Override
                public void runEvent() {
                    try {
                        for (int i = 0; i < soundInfoMap.size(); i++) {
                            soundPool.play(soundInfoMap.keyAt(i), 1, 1, 1, 0, 1);
                            TimeUnit.MILLISECONDS.sleep(soundInfoMap.valueAt(i));
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
                        onPlayCompletion();
                    }
                }
            });
        });

        //可以通过四种途径来记载一个音频资源：
        //1.通过一个AssetFileDescriptor对象
        //int load(AssetFileDescriptor afd, int priority)
        //2.通过一个资源ID
        //int load(Context context, int resId, int priority)
        //3.通过指定的路径加载
        //int load(String path, int priority)
        //4.通过FileDescriptor加载
        //int load(FileDescriptor fd, long offset, long length, int priority)
        //异步需要等待加载完成，音频才能播放成功
//        soundPool.setOnLoadCompleteListener(onLoadCompleteListener);
    }

//    /**
//     * 当加载完成时回调监听
//     */
//    private final SoundPool.OnLoadCompleteListener onLoadCompleteListener = new SoundPool.OnLoadCompleteListener() {
//        @Override
//        public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//            if (status == 0) {
//                //第一个参数soundID
//                //第二个参数leftVolume为左侧音量值（范围= 0.0到1.0）
//                //第三个参数rightVolume为右的音量值（范围= 0.0到1.0）
//                //第四个参数priority 为流的优先级，值越大优先级高，影响当同时播放数量超出了最大支持数时SoundPool对该流的处理
//                //第五个参数loop 为音频重复播放次数，0为值播放一次，-1为无限循环，其他值为播放loop+1次
//                //第六个参数 rate为播放的速率，范围0.5-2.0(0.5为一半速率，1.0为正常速率，2.0为两倍速率)
//                soundPool.play(sampleId, 1, 1, 1, 0, 1);
//            }
//        }
//    };

    /**
     * MediaPlayer播放完成时调用
     */
    private void onPlayCompletion() {
        soundInfoMap.clear();
        if (playTask != null) {
            playTask.cancel();
            playTask = null;
        }

        if (playQueue.isEmpty()) {
            releaseMediaPlayer();
            invokeOnPlayFinishListener();
            return;
        }

        play(playQueue.poll());
    }


    /**
     * 释放mediaPlayer资源并置null
     */
    private void releaseMediaPlayer() {
        if (soundPool != null) {
            try {
                soundPool.release();
            } catch (Throwable e) {
                e.printStackTrace();
            } finally {
                soundPool = null;
            }
        }
    }

    /**
     * 添加播放
     */
    public synchronized void addPlay(AudioItem source) {
        if (source == null) {
            return;
        }
        if (isPlayFinish) {
            this.play(List.of(source));
            return;
        }
        playQueue.offer(List.of(source));
    }

    /**
     * 添加播放
     */
    public synchronized void addPlay(List<AudioItem> source) {
        if (source == null || source.isEmpty()) {
            return;
        }
        if (isPlayFinish) {
            this.play(source);
            return;
        }
        playQueue.offer(source);
    }

    /**
     * 播放
     */
    private synchronized void play(List<AudioItem> audioList) {
        this.isPlayFinish = false;

        if (playTask != null) {
            playTask.cancel();
            playTask = null;
        }

        playTask = new ThreadUtil.RunTask() {
            @Override
            public void runEvent() {
                try {
                    if (soundPool == null) {
                        initParams();
                    }

                    for (AudioItem item : audioList) {
                        switch (item.getMediaSourceType()) {
                            case AudioSourceType.RAW_ID:
                                soundInfoMap.append(
                                        Integer.parseInt(item.getSource())
                                        , mediaPlayerUtil.getDuration(item.getMediaSourceType(), item.getSource())
                                );
                                break;
                            case AudioSourceType.PATH_URL:
                                soundInfoMap.append(
                                        soundPool.load(item.getSource(), 1)
                                        , mediaPlayerUtil.getDuration(item.getMediaSourceType(), item.getSource())
                                );
                                break;
                            case AudioSourceType.ASSETS_FD:
                            default:
                                try (AssetFileDescriptor fileDescriptor = AppContext.get().getAssets().openFd(item.getSource())) {
                                    soundInfoMap.append(
                                            soundPool.load(fileDescriptor, 1)
                                            , mediaPlayerUtil.getDuration(item.getSource(), item.getMediaSourceType())
                                    );
                                }
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    onPlayCompletion();
                }
            }
        };

        ThreadUtil.executeBySingle(playTask);
    }

    /**
     * 停止播放
     */
    public void stop() {
        try {
            playQueue.clear();
            if (playTask != null) {
                playTask.cancel();
                playTask = null;
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
            playQueue.clear();
            if (playTask != null) {
                playTask.cancel();
                playTask = null;
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
