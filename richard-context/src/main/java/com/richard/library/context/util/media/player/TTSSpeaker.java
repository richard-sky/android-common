package com.richard.library.context.util.media.player;

import android.annotation.SuppressLint;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;

import androidx.annotation.FloatRange;

import com.richard.library.context.AppContext;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * <pre>
 * Description : 调用TTS引擎文字转语音播报
 * Author : admin-richard
 * Date : 2020/11/20 15:45
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020/11/20 15:45     admin-richard         new file.
 * </pre>
 */
public final class TTSSpeaker {

    private TextToSpeech speech;
    private boolean isInitSuccess;
    private Locale locale = Locale.getDefault();
    private String ttsEngine;//当前设置的语音引擎
    private Voice voice;//当前设置的语音角色
    private InitCallback initCallback;//初始化回调

    //语速0.0~2.0 默认1.0
    private float speechRate = 1.0F;

    //音调0.0~2.0 默认1.0
    private float pitch = 1.0F;

    private TTSSpeaker() {

    }

    private static final class InstanceHolder {
        static final TTSSpeaker instance = new TTSSpeaker();
    }

    public static TTSSpeaker getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 初始化TextToSpeech
     */
    public void init(InitCallback initCallback) {
        this.initCallback = initCallback;
        this.execInit(null);
    }

    /**
     * 初始化TextToSpeech(异步)
     */
    private synchronized void execInit(SimpleInitCallback simpleInitCallback) {
        this.destroy();
        speech = new TextToSpeech(AppContext.get(), status -> {
            isInitSuccess = status == TextToSpeech.SUCCESS;

            if (status == TextToSpeech.SUCCESS) {
                this.setLanguage(locale);

                speech.setSpeechRate(speechRate);
                speech.setPitch(pitch);

                if (voice != null) {
                    speech.setVoice(voice);
                }

                speech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override
                    public void onStart(String utteranceId) {
                        if (initCallback != null)
                            initCallback.onStart(utteranceId);
                    }

                    @Override
                    public void onDone(String utteranceId) {
                        if (initCallback != null)
                            initCallback.onDone(utteranceId);
                    }

                    @Override
                    public void onError(String utteranceId) {
                        if (initCallback != null)
                            initCallback.onError(utteranceId);
                    }
                });

                if (simpleInitCallback != null) {
                    if (isInitSuccess) {
                        simpleInitCallback.onInitSuccess();
                    } else {
                        simpleInitCallback.onInitFailed();
                    }
                }
            }

            if (initCallback != null) {
                initCallback.onInitResult(isInitSuccess);
            }
        }, ttsEngine);
    }

    /**
     * 设置播放音调和语速
     *
     * @param rate  语速：0.0 - 2.0 默认1.0
     * @param pitch 音调：0.0 - 2.0 默认1.0
     */
    public void setSpeechParams(@FloatRange(from = 0.00F, to = 2.00F) float rate, @FloatRange(from = 0.00F, to = 2.00F) float pitch) {
        this.speechRate = rate;
        this.pitch = pitch;

        if (isAvailable()) {
            speech.setSpeechRate(speechRate);
            speech.setPitch(pitch);
        } else {
            Log.e(getClass().getName(), "请先初始化" + getClass().getName());
        }
    }

    /**
     * 设置当前语言
     *
     * @param locale 语言
     * @return 是否支持并成功设置该语言
     */
    public boolean setLanguage(Locale locale) {
        this.locale = locale;
        if (!this.isAvailable()) {
            Log.e(getClass().getName(), "请先初始化" + getClass().getName());
            return false;
        }

        int result = speech.setLanguage(locale);

        if (result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e(getClass().getName(), "：当前设备引擎不⽀持该语种");
            return false;
        }

        if (result == TextToSpeech.LANG_MISSING_DATA) {
            Log.e(getClass().getName(), "缺失语⾳数据包（需⽤⼾下载系统语⾳包）");
            return false;
        }

        return true;
    }

    /**
     * 开始文字转语音并播报
     *
     * @param text 语音播报内容
     */
    public void speak(String text) {
        this.speak(text, null);
    }

    /**
     * 开始文字转语音并播报
     *
     * @param text        语音播报内容
     * @param utteranceId 本次请求合成播放id
     */
    public void speak(String text, String utteranceId) {
        if (!this.isAvailable()) {
            this.execInit(() -> startSpeak(text, utteranceId, TextToSpeech.QUEUE_ADD));
            return;
        }
        this.startSpeak(text, utteranceId, TextToSpeech.QUEUE_ADD);
    }

    /**
     * 清空队列⽴即播报
     *
     * @param text 语音播报内容
     */
    public void speakImmediately(String text) {
        this.speakImmediately(text, null);
    }

    /**
     * 清空队列⽴即播报
     *
     * @param text        语音播报内容
     * @param utteranceId 本次请求合成播放id
     */
    public void speakImmediately(String text, String utteranceId) {
        if (!this.isAvailable()) {
            this.execInit(() -> startSpeak(text, utteranceId, TextToSpeech.QUEUE_FLUSH));
            return;
        }
        this.startSpeak(text, utteranceId, TextToSpeech.QUEUE_FLUSH);
    }


    /**
     * 开始合成播放
     *
     * @param text        语音播报内容
     * @param utteranceId 本次请求合成播放id
     * @param queueMode   队列模式：TextToSpeech.QUEUE_ADD：添加到队列播放、TextToSpeech.QUEUE_FLUSH:清空队列播放
     */
    @SuppressLint("ObsoleteSdkInt")
    private void startSpeak(String text, String utteranceId, int queueMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            speech.speak(text, queueMode, null, utteranceId);
        } else {
            HashMap<String, String> params = null;
            if (utteranceId != null) {
                params = new HashMap<>();
                params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, utteranceId);
            }
            speech.speak(text, queueMode, params);
        }
    }

    /**
     * 获取本机所有已安装TTS引擎包名
     *
     * @return 已安装的TTS引擎 key：TTS名称、value: 引擎包名
     */
    public List<TextToSpeech.EngineInfo> getInstalledTTSEngine() {
        List<TextToSpeech.EngineInfo> engines = speech.getEngines();
        if (!this.isAvailable()) {
            Log.e(getClass().getName(), "请先初始化" + getClass().getName());
        }
        return engines;
    }

    /**
     * 获取当前TTS不同角色语音
     */
    public List<Voice> getVoice() {
        if (!isAvailable()) {
            Log.e(getClass().getName(), "请先初始化" + getClass().getName());
            return new ArrayList<>();
        }
        Set<Voice> voices = speech.getVoices();
        if (voices == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(voices);
    }

    /**
     * 设置当前的语音角色
     */
    public boolean setVoice(Voice voice) {
        this.voice = voice;

        if (!isAvailable()) {
            Log.e(getClass().getName(), "请先初始化" + getClass().getName());
            return false;
        }

        if (voice != null) {
            return speech.setVoice(voice) == TextToSpeech.SUCCESS;
        }

        return false;
    }

    /**
     * 切换指定TTS引擎（讯⻜、百度、系统⾃带）
     *
     * @param enginePackageName 引擎包名
     */
    public void switchTTSEngine(String enginePackageName) {
        this.ttsEngine = enginePackageName;
        this.destroy();
        this.execInit(new SimpleInitCallback() {
            @Override
            public void onInitSuccess() {
                Log.d(getClass().getName(), String.format("切换 %s TTS引擎成功", enginePackageName));
            }

            @Override
            public void onInitFailed() {
                Log.d(getClass().getName(), String.format("切换 %s TTS引擎失败", enginePackageName));
            }
        });
    }

    /**
     * 将⽂字转为语⾳保存为⾳频⽂件
     */
    public void saveTextToAudio(String text, File saveFile) {
        this.saveTextToAudio(text, saveFile, null);
    }

    /**
     * 将⽂字转为语⾳保存为⾳频⽂件
     */
    public void saveTextToAudio(String text, File saveFile, String utteranceId) {
        if (!this.isAvailable()) {
            this.execInit(() -> {
                speech.synthesizeToFile(text, null, saveFile, utteranceId);
            });
            return;
        }
        speech.synthesizeToFile(text, null, saveFile, utteranceId);
    }

    /**
     * 是否正在播报
     */
    public boolean isSpeaking() {
        if (speech == null) {
            return false;
        }
        return speech.isSpeaking();
    }

    /**
     * 验证当前设置的语言环境是否可用
     */
    public boolean isAvailable() {
        return this.isInitSuccess && speech != null;
    }

    /**
     * 停止播报
     */
    public void stop() {
        if (speech != null && speech.isSpeaking()) {
            speech.stop();
        }
    }

    /**
     * 关闭并销毁播报资源
     */
    public void destroy() {
        if (speech != null) {
            speech.stop();
            speech.shutdown();
        }
        isInitSuccess = false;
    }

    /**
     * speakText初始化回调
     */
    public interface SimpleInitCallback {
        /**
         * 当初始化成功时回调
         */
        void onInitSuccess();

        /**
         * 当初始化失败时回调
         */
        default void onInitFailed() {
        }
    }

    /**
     * TextToSpeech初始化回调
     */
    public static abstract class InitCallback {

        /**
         * 当即将开始处理音频合成的时候回调
         *
         * @param utteranceId 语言请求合成播放id
         */
        public void onStart(String utteranceId) {
        }

        /**
         * 当完成音频处理的时候回调
         *
         * @param utteranceId 语言请求合成播放id
         */
        public void onDone(String utteranceId) {
        }

        /**
         * 当音频处理过程中发生错误的时候回调
         *
         * @param utteranceId 语言请求合成播放id
         */
        public void onError(String utteranceId) {
        }

        /**
         * 当初始化结束时回调
         *
         * @param isInitSuccess 是否初始化成功
         */
        public abstract void onInitResult(boolean isInitSuccess);
    }
}
