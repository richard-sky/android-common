package com.richard.library.context.util.media.recorder;

import android.media.MediaRecorder;
import android.os.Build;

import java.util.HashMap;
import java.util.Map;

/**
 * 全覆盖Android MediaRecorder 所有OutputFormat映射后缀
 * 包含全部系统内置音频/视频封装格式，自动匹配文件扩展名
 */
public class FormatSuffixHelper {
    private static final Map<Integer, String> FORMAT_SUFFIX_MAP;

    static {
        FORMAT_SUFFIX_MAP = new HashMap<>();
        // ========== 音频封装格式 ==========
        FORMAT_SUFFIX_MAP.put(MediaRecorder.OutputFormat.AMR_NB, ".amr");
        FORMAT_SUFFIX_MAP.put(MediaRecorder.OutputFormat.AMR_WB, ".awb");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            FORMAT_SUFFIX_MAP.put(MediaRecorder.OutputFormat.OGG, ".ogg");       // Android 10+
        }

        // ========== 视频封装格式（覆盖全部） ==========
        FORMAT_SUFFIX_MAP.put(MediaRecorder.OutputFormat.MPEG_4, ".mp4");
        FORMAT_SUFFIX_MAP.put(MediaRecorder.OutputFormat.THREE_GPP, ".3gp");
        FORMAT_SUFFIX_MAP.put(MediaRecorder.OutputFormat.WEBM, ".webm");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            FORMAT_SUFFIX_MAP.put(MediaRecorder.OutputFormat.MPEG_2_TS, ".ts");
        }
    }

    /**
     * 根据封装格式获取对应文件后缀
     *
     * @param outputFormat MediaRecorder.OutputFormat 全量枚举值
     * @return 文件后缀，默认 .m4a
     */
    public static String getSuffixByFormat(int outputFormat) {
        return FORMAT_SUFFIX_MAP.getOrDefault(outputFormat, ".m4a");
    }

    /**
     * 获取存储目录
     *
     * @param isAudio true=audio目录 false=video目录
     */
    public static String getDirName(boolean isAudio) {
        return isAudio ? "audio" : "video";
    }
}