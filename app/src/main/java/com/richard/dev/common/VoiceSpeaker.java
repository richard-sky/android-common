package com.richard.dev.common;

import android.text.TextUtils;

import com.richard.library.context.util.ObjectUtilKt;
import com.richard.library.context.util.StringUtilKt;
import com.richard.library.context.util.media.AudioItem;
import com.richard.library.context.util.media.AudioSourceType;
import com.richard.library.context.util.media.MediaPlayerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * author：Richard
 * time：2021-11-02 14:58
 * version：v1.0.0
 * description：收款语音播报（语音文件顺序播放方式）
 */
public final class VoiceSpeaker {

    private final String DOT = ".";
    private final char[] NUM = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final char[] CHINESE_UNIT = {'元', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟'};
    private final String numVoiceAssetsDir = "voice";
    //        private static final SoundPoolUtil player = new SoundPoolUtil();
    private static final MediaPlayerUtil player = new MediaPlayerUtil(true);
    private static VoiceSpeaker instance;

    public static VoiceSpeaker get() {
        if (instance != null) {
            return instance;
        }
        synchronized (VoiceSpeaker.class) {
            if (instance == null) {
                instance = new VoiceSpeaker();
            }
        }
        return instance;
    }

    /**
     * 创建Builder
     */
    public Builder newBuilder() {
        return new Builder();
    }

    /**
     * 语音播报
     */
    public static class Builder {
        /**
         * 前置播报音频
         */
        private final List<AudioItem> prefix = new ArrayList<>();

        /**
         * 金额播报音频
         */
        private String numString;

        /**
         * 后置播报音频
         */
        private final List<AudioItem> suffix = new ArrayList<>();

        /**
         * 添加前置播报音频
         */
        public Builder prefix(AudioItem prefix) {
            this.prefix.add(prefix);
            return this;
        }

        /**
         * 前置播报音频
         */
        public Builder prefix(List<AudioItem> prefix) {
            this.prefix.addAll(prefix);
            return this;
        }

        /**
         * 金额播报音频
         */
        public Builder numString(String numString) {
            this.numString = numString;
            return this;
        }

        /**
         * 后置播报音频
         */
        public Builder suffix(AudioItem suffix) {
            this.suffix.add(suffix);
            return this;
        }

        /**
         * 后置播报音频
         */
        public Builder suffix(List<AudioItem> suffix) {
            this.suffix.addAll(suffix);
            return this;
        }

        /**
         * 完成构造并开始播报
         */
        public void buildAndSpeak() {
            VoiceSpeaker.get().speak(prefix, numString, suffix);
        }
    }

    /**
     * 开始播报
     */
    private void speak(List<AudioItem> prefix, String numString, List<AudioItem> suffix) {
        try {
            List<AudioItem> audioSourceList = new ArrayList<>();

            if (ObjectUtilKt.isNotEmpty(prefix)) {
                for (AudioItem item : prefix) {
                    if (item != null && StringUtilKt.isNotBlank(item.getSource())) {
                        audioSourceList.add(item);
                    }
                }
            }

            if (StringUtilKt.isNotBlank(numString)) {
                List<String> numAudioSourceList = this.genReadableMoney(numString);
                for (String item : numAudioSourceList) {
                    audioSourceList.add(new AudioItem(String.format("%s/tts_%s.mp3", numVoiceAssetsDir, item), AudioSourceType.ASSETS_FD));
                }
            }

            if (ObjectUtilKt.isNotEmpty(suffix)) {
                for (AudioItem item : suffix) {
                    if (item != null && StringUtilKt.isNotBlank(item.getSource())) {
                        audioSourceList.add(item);
                    }
                }
            }

            player.addPlay(audioSourceList);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理小数点之前和之后的数据
     */
    private List<String> genReadableMoney(String numString) {
        List<String> result = new ArrayList<>();
        if (!TextUtils.isEmpty(numString)) {
            if (numString.contains(DOT)) {
                String integerPart = numString.split("\\.")[0];
                String decimalPart = numString.split("\\.")[1];
                List<String> intList = readIntPart(integerPart);
                List<String> decimalList = readDecimalPart(decimalPart);
                result.addAll(intList);
                if (!decimalList.isEmpty()) {
                    result.add("dot");
                    result.addAll(decimalList);
                }
            } else {
                result.addAll(readIntPart(numString));
            }
        }
        return result;
    }

    /**
     * 读取小数部分
     */
    private List<String> readDecimalPart(String decimalPart) {
        List<String> result = new ArrayList<>();
        if (!"00".equals(decimalPart)) {
            char[] chars = decimalPart.toCharArray();
            for (char ch : chars) {
                result.add(String.valueOf(ch));
            }
        }
        return result;
    }

    /**
     * 读取整数位部分
     */
    private List<String> readIntPart(String integerPart) {
        List<String> result = new ArrayList<>();
        String intString = readInt(Integer.parseInt(integerPart));
        int len = intString.length();
        for (int i = 0; i < len; i++) {
            char current = intString.charAt(i);
            if (current == '拾') {
                result.add("ten");
            } else if (current == '佰') {
                result.add("hundred");
            } else if (current == '仟') {
                result.add("thousand");
            } else if (current == '万') {
                result.add("ten_thousand");
            } else if (current == '亿') {
                result.add("billion");
            } else {
                result.add(String.valueOf(current));
            }
        }
        return result;
    }

    /**
     * 返回关于钱的中文式大写数字,支仅持到亿
     */
    private String readInt(int moneyNum) {
        String res = "";
        int i = 0;
        if (moneyNum == 0) {
            return "0";
        }
        if (moneyNum == 10) {
            return "拾";
        }
        if (moneyNum > 10 && moneyNum < 20) {
            return "拾" + moneyNum % 10;
        }
        while (moneyNum > 0) {
            res = CHINESE_UNIT[i++] + res;
            res = NUM[moneyNum % 10] + res;
            moneyNum /= 10;
        }
        return res.replaceAll("0[拾佰仟]", "0")
                .replaceAll("0+亿", "亿")
                .replaceAll("0+万", "万")
                .replaceAll("0+元", "元")
                .replaceAll("0+", "0")
                .replace("元", "");
    }

    /**
     * 释放资源
     */
    public void release() {
        if (player != null) {
            player.release();
        }
    }
}