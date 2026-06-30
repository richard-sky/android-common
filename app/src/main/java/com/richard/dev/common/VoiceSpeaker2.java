package com.richard.dev.common;

import android.text.TextUtils;

import com.richard.library.context.util.FileUtil;
import com.richard.library.context.util.ObjectUtilKt;
import com.richard.library.context.util.StringUtilKt;
import com.richard.library.context.util.media.player.common.AudioItem;
import com.richard.library.context.util.media.player.common.AudioSourceType;
import com.richard.library.context.util.media.player.MediaPlayerUtil;
import com.richard.library.context.AppContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * author：Richard
 * time：2021-11-02 14:58
 * version：v1.0.0
 * description：收款语音播报(语音文件合成方式)
 */
public final class VoiceSpeaker2 {

    private final String DOT = ".";
    private final char[] NUM = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private final char[] CHINESE_UNIT = {'元', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟'};
    private final String numVoiceAssetsDir = "voice";
    private static VoiceSpeaker2 instance;
    private final MediaPlayerUtil mediaPlayerUtil = new MediaPlayerUtil(true);
    private final File voiceSaveDir = new File(FileUtil.getCacheDir(), "speaker");

    public static VoiceSpeaker2 get() {
        if (instance != null) {
            return instance;
        }
        synchronized (VoiceSpeaker2.class) {
            if (instance == null) {
                instance = new VoiceSpeaker2();
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
         * 添加后置播报音频
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
            VoiceSpeaker2.get().speak(prefix, numString, suffix);
        }
    }

    /**
     * 开始播报
     */
    private void speak(List<AudioItem> prefix, String numString, List<AudioItem> suffix) {
        try {
            List<File> audioSourceList = new ArrayList<>();

            if (ObjectUtilKt.isNotEmpty(prefix)) {
                for (AudioItem item : prefix) {
                    if (item != null && StringUtilKt.isNotBlank(item.getSource())) {
                        audioSourceList.add(copyFile(item.getSource()));
                    }
                }
            }

            if (ObjectUtilKt.isNotEmpty(numString)) {
                List<String> numAudioSourceList = this.genReadableMoney(numString);
                for (String item : numAudioSourceList) {
                    audioSourceList.add(copyFile(String.format("%s/tts_%s.mp3", numVoiceAssetsDir, item)));
                }
            }

            if (ObjectUtilKt.isNotEmpty(suffix)) {
                for (AudioItem item : suffix) {
                    if (item != null && StringUtilKt.isNotBlank(item.getSource())) {
                        audioSourceList.add(copyFile(item.getSource()));
                    }
                }
            }

            File file = new File(FileUtil.getCacheDir().concat(File.separator).concat("merge_voice_.mp4"));
            mergeAudio(file, audioSourceList);

            mediaPlayerUtil.addPlay(new AudioItem(file.getAbsolutePath(), AudioSourceType.PATH_URL));
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
     * 清空语音文件缓存
     */
    public void clearVoiceCache() {
        FileUtil.deleteAllInDir(voiceSaveDir);
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mediaPlayerUtil != null) {
            mediaPlayerUtil.release();
        }
    }

    /**
     * 将assets音频复制到本地存储
     *
     * @param assetsFile 需要copy的assets文件
     * @return 复制到本地存储得文件
     */
    private File copyFile(String assetsFile) {
        FileUtil.createOrExistsDir(voiceSaveDir);

        File saveFile = new File(voiceSaveDir.getAbsolutePath() + File.separator + assetsFile);
        FileUtil.createOrExistsDir(FileUtil.getDirName(saveFile));

        InputStream in = null;
        FileOutputStream out = null;// path为指定目录

        if (!saveFile.exists()) {
            try {
                in = AppContext.get().getAssets().open(assetsFile); // 从assets目录下复制
                out = new FileOutputStream(saveFile);
                int length = -1;
                byte[] buf = new byte[1024];
                while ((length = in.read(buf)) != -1) {
                    out.write(buf, 0, length);
                }
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

        return saveFile;
    }

    /**
     * 合并音频
     *
     * @param mergedFile 最终合并完成得音频文件
     * @param mp3Files   需要合并得音频文件列表
     */
    private void mergeAudio(File mergedFile, List<File> mp3Files) {
        FileInputStream fisToFinal = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mergedFile);
            fisToFinal = new FileInputStream(mergedFile);
            for (File mp3File : mp3Files) {
                if (!mp3File.exists())
                    continue;
                FileInputStream fisSong = new FileInputStream(mp3File);
                SequenceInputStream sis = new SequenceInputStream(fisToFinal, fisSong);
                byte[] buf = new byte[1024];
                try {
                    for (int readNum; (readNum = fisSong.read(buf)) != -1; )
                        fos.write(buf, 0, readNum);
                } finally {
                    fisSong.close();
                    sis.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                if (fisToFinal != null) {
                    fisToFinal.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}