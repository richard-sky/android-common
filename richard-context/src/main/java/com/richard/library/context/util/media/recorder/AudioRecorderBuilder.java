package com.richard.library.context.util.media.recorder;
import android.content.Context;
import android.media.MediaRecorder;
import java.io.File;

/**
 * 独立纯音频录制构建器
 * 所有音频参数内置本类，无任何视频/Camera相关代码
 * 仅提供音频配置链式方法，无法调用视频API
 */
public class AudioRecorderBuilder {
    protected final Context mAppContext;
    // ===================== 内置全部音频参数 =====================
    /**
     * 音频采集源
     * MediaRecorder.AudioSource.MIC(1)/CAMCORDER(5)/VOICE_RECOGNITION(6)/VOICE_COMMUNICATION(7)
     */
    protected int audioSource = MediaRecorder.AudioSource.MIC;
    /**
     * 音频编码器 全系统支持枚举
     * AAC=3 / AMR_NB=1 / AMR_WB=2 / OPUS=10(Android10+) / VORBIS=9
     */
    protected int audioEncoder = MediaRecorder.AudioEncoder.AAC;
    /**
     * 音频采样率 Hz
     * 合法值：8000/16000/22050/44100/48000
     * AMR仅支持8000
     */
    protected int audioSampleRate = 44100;
    /**
     * 音频比特率 bps
     * 语音64k~128k；音乐192k~320k
     */
    protected int audioBitRate = 128000;
    /**
     * 输出封装格式，覆盖系统全部OutputFormat
     * MPEG_4 / THREE_GPP / AMR_NB / OGG / WEBM / MPEG_2_TS
     */
    protected int outputFormat = MediaRecorder.OutputFormat.MPEG_4;
    /** 自定义输出绝对路径，null自动生成 */
    protected String customOutputPath;
    /** 模式标记：true=纯音频 */
    protected final boolean isAudioMode = true;

    public AudioRecorderBuilder(Context context) {
        mAppContext = context.getApplicationContext();
    }

    // ========== 音频链式配置方法 ==========
    public AudioRecorderBuilder audioSource(int source) {
        this.audioSource = source;
        return this;
    }

    public AudioRecorderBuilder audioEncoder(int encoder) {
        this.audioEncoder = encoder;
        return this;
    }

    public AudioRecorderBuilder audioSampleRate(int sampleRate) {
        this.audioSampleRate = sampleRate;
        return this;
    }

    public AudioRecorderBuilder audioBitRate(int bitRate) {
        this.audioBitRate = bitRate;
        return this;
    }

    public AudioRecorderBuilder outputFormat(int format) {
        this.outputFormat = format;
        return this;
    }

    public AudioRecorderBuilder outputPath(String path) {
        this.customOutputPath = path;
        return this;
    }

    /**
     * 构建输出文件路径，自动根据outputFormat匹配后缀
     * @return 最终生成的文件绝对路径
     */
    public String buildOutputFilePath() {
        if (customOutputPath != null && !customOutputPath.isEmpty()) {
            return customOutputPath;
        }
        String suffix = FormatSuffixHelper.getSuffixByFormat(outputFormat);
        String dirName = FormatSuffixHelper.getDirName(isAudioMode);
        File dir = mAppContext.getExternalFilesDir(dirName);
        if (dir != null && !dir.exists()) {
            dir.mkdirs();
        }
        return new File(dir, System.currentTimeMillis() + suffix).getAbsolutePath();
    }

    /** 获取模式标识 */
    public boolean isAudioMode() {
        return isAudioMode;
    }
}
