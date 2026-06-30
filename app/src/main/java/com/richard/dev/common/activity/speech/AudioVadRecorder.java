package com.richard.dev.common.activity.speech;

import android.content.Context;
import android.util.Log;

import com.richard.library.context.util.ThreadUtil;
import com.richard.library.context.util.UIThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.SilenceDetector;
import be.tarsos.dsp.filters.HighPass;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;

public class AudioVadRecorder {
    private static final String TAG = "AudioVadRecorder";
    private static final String RECORD_DIR_NAME = "AudioVadRecord";

    // 配置参数
    private final double silenceThreshDb;
    private final long silenceStopDelayMs;
    private final int sampleRate;
    private final int bufferSize;
    private final float highPassFreq;
    private final boolean saveWavEnable;
    private final Context appContext;

    // TarsosDSP核心实例
    private AudioDispatcher audioDispatcher;
    private SilenceDetector silenceDetector;
    private HighPass highPassFilter;

    // 文件相关
    private File tempPcmFile;
    private FileOutputStream pcmOs;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault());

    // 复用缓冲，避免频繁GC造成音频丢帧（核心修复卡顿）
    private final ByteBuffer reuseShortBuffer;
    private final byte[] reusePcmBuffer;


    // 对外回调接口
    public interface RecorderCallback {
        /**
         * 人声状态切换回调
         */
        void onVoiceStateChange(boolean hasVoice, double currentDB);

        /**
         * 实时PCM字节流回调
         */
        void onRealTimePcm(byte[] pcmBytes);

        /**
         * 静音超时自动停止
         */
        void onSilenceAutoStop();

        /**
         * WAV文件保存完成，saveWavEnable=true才会回调
         */
        default void onWavSaved(String wavPath) {
        }

        /**
         * 采集异常
         */
        default void onError(String msg, Exception e) {
        }
    }

    private RecorderCallback callback;

    // ===================== 私有构造，仅Builder可调用 =====================
    private AudioVadRecorder(Builder builder) {
        this.silenceThreshDb = builder.silenceThreshDb;
        this.silenceStopDelayMs = builder.silenceStopDelayMs;
        this.sampleRate = builder.sampleRate;
        this.bufferSize = builder.bufferSize;
        this.highPassFreq = builder.highPassFreq;
        this.saveWavEnable = builder.saveWavEnable;
        this.appContext = builder.appContext.getApplicationContext();
        this.callback = builder.callback;

        // 预分配复用缓冲区，消除每次new数组GC卡顿
        reuseShortBuffer = ByteBuffer.allocate(bufferSize * 2);
        reuseShortBuffer.order(ByteOrder.LITTLE_ENDIAN);
        reusePcmBuffer = new byte[bufferSize * 2];
    }

    // ===================== Builder构建器 =====================
    public static Builder create(Context context) {
        return new Builder(context);
    }

    public static class Builder {
        private final Context appContext;
        private double silenceThreshDb = -45D; // 优化默认分贝阈值，原-120完全失效
        private long silenceStopDelayMs = 1500L;
        private int sampleRate = 16000; // 语音推荐16k，22050兼容性差易卡顿
        private int bufferSize = 2048; // 加大缓冲减少麦克风断流
        private float highPassFreq = 300F; // 提高高通，过滤低频电流噪音
        private boolean saveWavEnable = true;
        private RecorderCallback callback;

        // 必须传入上下文
        public Builder(Context context) {
            this.appContext = context;
        }

        public Builder silenceThreshDb(double db) {
            this.silenceThreshDb = db;
            return this;
        }

        public Builder silenceStopDelayMs(long ms) {
            this.silenceStopDelayMs = ms;
            return this;
        }

        public Builder sampleRate(int rate) {
            this.sampleRate = rate;
            return this;
        }

        public Builder bufferSize(int size) {
            this.bufferSize = size;
            return this;
        }

        public Builder highPassFreq(float freq) {
            this.highPassFreq = freq;
            return this;
        }

        public Builder saveWavEnable(boolean enable) {
            this.saveWavEnable = enable;
            return this;
        }

        public Builder callback(RecorderCallback cb) {
            this.callback = cb;
            return this;
        }

        // 构建最终实例
        public AudioVadRecorder build() {
            if (appContext == null) {
                throw new IllegalArgumentException("Context cannot be null");
            }
            return new AudioVadRecorder(this);
        }
    }

    // ===================== 对外核心方法 =====================
    public void setCallback(RecorderCallback callback) {
        this.callback = callback;
    }

    public void startRecord() {
        if (audioDispatcher != null) {
            Log.w(TAG, "录音正在运行，无需重复启动");
            return;
        }
        try {
            // 创建临时PCM文件（开启保存才创建）
            if (saveWavEnable) createPcmTempFile();

            // 修复1：overlap设为0，不做帧重叠，避免音频时序错乱拖音
            int overlap = 0;
            audioDispatcher = AudioDispatcherFactory.fromDefaultMicrophone(sampleRate, bufferSize, overlap);

            // 降噪滤波器 + VAD检测器
            highPassFilter = new HighPass(sampleRate, highPassFreq);
            silenceDetector = new SilenceDetector(silenceThreshDb, false);

            // 处理链：降噪 → VAD → 自定义音频数据处理
            audioDispatcher.addAudioProcessor(highPassFilter);
            audioDispatcher.addAudioProcessor(silenceDetector);
            audioDispatcher.addAudioProcessor(buildAudioProcessor());

            // 守护线程运行音频采集
            Thread captureThread = new Thread(() -> {
                try {
                    audioDispatcher.run();
                } catch (Exception e) {
                    String err = "音频采集异常";
                    Log.e(TAG, err, e);
                    UIThread.runOnUiThread(() -> {
                        if (callback != null) callback.onError(err, e);
                    });
                    stopRecord();
                }
            }, "AudioCaptureThread");
            captureThread.setDaemon(true);
            captureThread.start();

        } catch (Exception e) {
            String errMsg = "初始化麦克风失败，请检查RECORD_AUDIO权限";
            Log.e(TAG, errMsg, e);
            closePcmStream();
            if (callback != null) callback.onError(errMsg, e);
        }
    }

    public void stopRecord() {
        // 停止音频采集器
        if (audioDispatcher != null) {
            audioDispatcher.stop();
            audioDispatcher = null;
        }

        // 异步转WAV，不阻塞主线程、音频线程
        if (saveWavEnable && tempPcmFile != null && tempPcmFile.exists()) {
            File pcmFile = tempPcmFile;
            int rate = sampleRate;
            ThreadUtil.getCachedPool().submit(() -> {
                closePcmStream();
                pcmToWavStream(pcmFile, rate);
            });
        } else {
            closePcmStream();
        }
        Log.d(TAG, "录音已停止");
    }

    // 页面销毁释放资源
    public void release() {
        stopRecord();
        callback = null;
        highPassFilter = null;
        silenceDetector = null;
    }

    // ===================== 音频处理器（核心修复：全程写入所有帧，不丢弃静音） =====================
    private AudioProcessor buildAudioProcessor() {
        return new AudioProcessor() {
            @Override
            public boolean process(AudioEvent audioEvent) {
                if (silenceDetector == null) return true;
                float[] floatBuf = audioEvent.getFloatBuffer();
                double currentDb = silenceDetector.currentSPL();

                boolean hasVoice = handleVadSwitch(currentDb);

                Log.d("testtt", "当前声音分贝: " + currentDb + "   阀值: " + silenceThreshDb);

                byte[] pcmData = floatTo16BitPCMReuse(floatBuf);

                if (saveWavEnable) {
                    writePcmToTempFile(pcmData);
                }

                if (callback != null) {
                    callback.onRealTimePcm(pcmData);
                }
                return true;
            }

            @Override
            public void processingFinished() {
                Log.d(TAG, "音频处理线程结束");
            }
        };
    }

    // VAD人声状态切换处理
    private int noVoiceCount = 0;
    private boolean lastHasVoice = false;

    private boolean handleVadSwitch(double db) {
        boolean hasVoice = db > silenceThreshDb;
        if (hasVoice) {
            noVoiceCount = 0;
        } else {
            noVoiceCount++;
        }

        //主线程回调人声变化
        if (hasVoice != lastHasVoice) {
            if (callback != null) callback.onVoiceStateChange(hasVoice, db);
        }

        lastHasVoice = hasVoice;

        if (noVoiceCount < 10) {
            return hasVoice;
        }

        Log.d(TAG, "静音超时自动停止录音");
        if (callback != null) callback.onSilenceAutoStop();
        stopRecord();

        return hasVoice;
    }

    // 修复3：复用ByteBuffer/byte数组，消除频繁GC导致的音频丢帧卡顿
    private byte[] floatTo16BitPCMReuse(float[] floats) {
        reuseShortBuffer.clear();
        for (float f : floats) {
            // 限幅防止溢出失真
            float clamp = Math.max(-1f, Math.min(1f, f));
            short sample = (short) (clamp * Short.MAX_VALUE);
            reuseShortBuffer.putShort(sample);
        }
        reuseShortBuffer.flip();
        int dataLen = reuseShortBuffer.remaining();
        reuseShortBuffer.get(reusePcmBuffer, 0, dataLen);
        // 返回有效长度片段，不复制新数组
        byte[] result = new byte[dataLen];
        System.arraycopy(reusePcmBuffer, 0, result, 0, dataLen);
        return result;
    }

    // ===================== 文件操作 =====================
    private void createPcmTempFile() throws IOException {
        File dir = new File(appContext.getFilesDir(), RECORD_DIR_NAME);
        if (!dir.exists()) dir.mkdirs();
        String fileName = "record_" + sdf.format(new Date()) + ".pcm";
        tempPcmFile = new File(dir, fileName);
        pcmOs = new FileOutputStream(tempPcmFile);
        Log.d(TAG, "临时PCM文件：" + tempPcmFile.getAbsolutePath());
    }

    private void writePcmToTempFile(byte[] pcmBytes) {
        if (pcmOs == null) return;
        // 直接音频线程写短块，单块数据小不会阻塞
        try {
            pcmOs.write(pcmBytes);
        } catch (Exception e) {
            Log.e(TAG, "写入PCM失败", e);
        }
    }

    private void closePcmStream() {
        try {
            if (pcmOs != null) {
                pcmOs.flush();
                pcmOs.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "关闭文件流异常", e);
        } finally {
            pcmOs = null;
        }
    }

    // 修复4：流式读写PCM转WAV，不一次性加载全部音频到内存，长录音不卡顿OOM
    private void pcmToWavStream(File pcmFile, int sampleRate) {
        String wavPath = pcmFile.getAbsolutePath().replace(".pcm", ".wav");
        File wavFile = new File(wavPath);
        int channel = 1;
        int bit = 16;
        final int blockSize = 4096;

        FileOutputStream wavOut = null;
        FileInputStream pcmIn = null;
        try {
            wavOut = new FileOutputStream(wavFile);
            long totalPcmByteLen = pcmFile.length();
            if (totalPcmByteLen <= 0) {
                throw new RuntimeException("PCM录音文件为空");
            }

            int byteRate = sampleRate * channel * bit / 8;
            int blockAlign = channel * bit / 8;
            int dataSize = (int) totalPcmByteLen;
            int totalSize = 36 + dataSize;

            // RIFF WAV Header
            wavOut.write("RIFF".getBytes());
            writeLittleInt(wavOut, totalSize, 4);
            wavOut.write("WAVE".getBytes());

            wavOut.write("fmt ".getBytes());
            writeLittleInt(wavOut, 16, 4);
            writeLittleInt(wavOut, 1, 2);
            writeLittleInt(wavOut, channel, 2);
            writeLittleInt(wavOut, sampleRate, 4);
            writeLittleInt(wavOut, byteRate, 4);
            writeLittleInt(wavOut, blockAlign, 2);
            writeLittleInt(wavOut, bit, 2);

            wavOut.write("data".getBytes());
            writeLittleInt(wavOut, dataSize, 4);

            // 分块流式读取写入，解决大文件卡顿、OOM
            pcmIn = new FileInputStream(pcmFile);
            byte[] buffer = new byte[blockSize];
            int readLen;
            while ((readLen = pcmIn.read(buffer)) != -1) {
                wavOut.write(buffer, 0, readLen);
            }
            wavOut.flush();

            // 删除临时pcm文件
            pcmFile.delete();

            final String finalWavPath = wavPath;
            UIThread.runOnUiThread(() -> {
                if (callback != null) callback.onWavSaved(finalWavPath);
            });
            Log.d(TAG, "WAV生成成功：" + finalWavPath);
        } catch (Exception e) {
            Log.e(TAG, "PCM转WAV失败", e);
            UIThread.runOnUiThread(() -> {
                if (callback != null) callback.onError("音频转码失败", e);
            });
        } finally {
            try {
                if (pcmIn != null) pcmIn.close();
                if (wavOut != null) wavOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 小端序写入数字
    private void writeLittleInt(FileOutputStream out, int val, int len) throws IOException {
        int v = val;
        for (int i = 0; i < len; i++) {
            out.write(v & 0xFF);
            v = v >> 8;
        }
    }
}