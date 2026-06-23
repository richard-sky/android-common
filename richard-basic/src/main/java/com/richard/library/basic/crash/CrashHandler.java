package com.richard.library.basic.crash;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.richard.library.context.util.ActivityUtil;
import com.richard.library.context.util.DateUtil;
import com.richard.library.context.util.DeviceUtil;
import com.richard.library.basic.util.ToastUtil;
import com.richard.library.context.AppContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <pre>
 * Description : 异常捕获 6.0需要动态读取和写入权限
 * Author : admin-richard
 * Date : 2019-03-05 9:25
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-03-05 9:25      admin-richard         new file.
 * </pre>
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 文件保存模式
     */
    public enum SaveLogMode {
        EVERY,//每次crash都生成一个文件保存
        DAY,//按天保存
        FIXED//始终保存到一个文件
    }

    private static CrashHandler INSTANCE;
    private File saveLogFile;
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private Handler mHandler;
    private SaveLogMode saveLogMode = SaveLogMode.DAY;
    private Callback callback;


    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            synchronized (CrashHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CrashHandler();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 初始化异常处理器
     */
    public void init() {
        this.saveLogFile = this.getDefaultSaveFile();
        this.initCrashHandler();
    }

    /**
     * 初始化异常处理器
     *
     * @param saveLogFileDir 保存日志文件目录
     */
    public void init(File saveLogFileDir) {
        this.saveLogFile = saveLogFileDir;
        this.initCrashHandler();
    }

    /**
     * 初始化异常处理器
     *
     * @param callback 回调
     */
    public void init(Callback callback) {
        this.callback = callback;
        if (callback == null) {
            this.saveLogFile = getDefaultSaveFile();
        }
        this.initCrashHandler();
    }

    /**
     * 初始化CrashHandler
     */
    private void initCrashHandler() {
        //主线程异常拦截
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
            mHandler.post(() -> {
                while (true) {
                    try {
                        Looper.loop();
                    } catch (Throwable e) {
                        handleException(e);
                    }
                }
            });
        }

        //获取到系统默认的异常处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //所有线程异常拦截，由于主线程的异常都被我们catch住了，所以下面的代码拦截到的都是子线程的异常
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 获取默认的日志文件保存路径
     */
    private File getDefaultSaveFile() {
        return new File(AppContext.get().getCacheDir()
                .getAbsolutePath().concat(File.separator).concat("crash_logs"));
    }

    /**
     * 设置日志文件保存模式
     */
    public void setSaveLogMode(SaveLogMode saveLogMode) {
        this.saveLogMode = saveLogMode;
    }

    /**
     * 获取日志文件路径
     */
    public File getLogFile() {
        if (callback != null) {
            saveLogFile = callback.getSaveLogPath();
        }
        return saveLogFile;
    }

    @Override
    public void uncaughtException(@NonNull Thread thread, @NonNull Throwable throwable) {
        this.handleException(throwable);
    }

    /**
     * 处理异常
     */
    private void handleException(Throwable throwable) {
        throwable.printStackTrace();
        if (this.check(1200)) {
            ActivityUtil.exitApp(true);
            return;
        }

        ToastUtil.showError("程序出错啦！");

        //保存异常日志到本地
        String logContent = this.saveExceptionLog(AppContext.get(), throwable);

        //退出Activity或者App
        if (throwable instanceof RuntimeException && ActivityUtil.getTopActivity() != null) {
            ActivityUtil.finish();
            if (AppContext.isDebug()) {
                try {
                    CrashReportActivity.start(throwable, logContent);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } else {
            ActivityUtil.exitApp(true);
            if (AppContext.isDebug()) {
                try {
                    CrashReportActivity.start(throwable, logContent);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 获取异常栈信息
     */
    public String getExceptionStack(Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        String result = "";
        Writer writer = null;
        PrintWriter printWriter = null;
        try {
            writer = new StringWriter();
            printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            Throwable cause = throwable.getCause();

            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            result = writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    /**
     * Des: 将异常信息保存至 SD 卡
     *
     * @param context   context
     * @param throwable 异常信息
     * @return 异常日志内容, 为null时视为保存日志失败
     */
    public String saveExceptionLog(Context context, Throwable throwable) {
        String logContent = null;

        try {
            StringBuilder stringBuffer = new StringBuilder();
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi == null) {
                return null;
            }

            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return null;
            }

            long currentTimeMillis = System.currentTimeMillis();
            String time = DateUtil.formatTimeStamp("yyyy-MM-dd HH:mm:ss", currentTimeMillis);
            stringBuffer.append("报错时间：").append(time).append("\n");
            stringBuffer.append("应用版本：").append(pi.versionName).append("\n");
            stringBuffer.append("应用版本号：").append(pi.versionCode).append("\n");
            stringBuffer.append("品牌：").append(Build.MANUFACTURER).append("\n");
            stringBuffer.append("机型：").append(Build.MODEL).append("\n");
            stringBuffer.append("设备ID：").append(DeviceUtil.getUniqueDeviceId()).append("\n");
            stringBuffer.append("Android 版本：").append(Build.VERSION.RELEASE).append("\n");
            stringBuffer.append("系统版本：").append(Build.DISPLAY).append("\n");
            stringBuffer.append("是否模拟器：").append(DeviceUtil.isEmulator() ? "是" : "否").append("\n");
            stringBuffer.append("是否root：").append(DeviceUtil.isDeviceRooted() ? "是" : "否").append("\n");
            stringBuffer.append("异常信息：" + "\n").append(getExceptionStack(throwable)).append("\n\n\n");

            String fileName;
            boolean isAppendContent = true;
            switch (saveLogMode) {
                case EVERY:
                    fileName = "crash_".concat(time).concat("_").concat(String.valueOf(currentTimeMillis)).concat(".log");
                    isAppendContent = false;
                    break;
                case FIXED:
                    fileName = "crash".concat(".log");
                    break;
                case DAY:
                default:
                    fileName = "crash_".concat(DateUtil.formatTimeStamp("yyyy-MM-dd", currentTimeMillis)).concat(".log");
            }

            File logFile = this.getLogFile();
            if (!logFile.exists()) {
                logFile.mkdirs();
            }
            File file = new File(logFile, fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            logContent = stringBuffer.toString();
            this.saveTextFile(logContent, file, isAppendContent);
        } catch (Exception e) {
            Log.e("CrashHandler", "保存Crash文件失败", e);
        }

        return logContent;
    }

    /**
     * 检查异常是否频繁
     */
    private long lastTime;

    private boolean check(long millisecond) {
        boolean flag = true;
        long currentClickTime = System.currentTimeMillis();
        if ((currentClickTime - lastTime) >= millisecond) {
            flag = false;
        }
        lastTime = currentClickTime;
        return flag;
    }

    /**
     * 保存文件
     *
     * @param text            文本内容
     * @param saveFile        指定File
     * @param isAppendContent 是否在原文件内容上追加内容
     */
    private File saveTextFile(String text, File saveFile, boolean isAppendContent) throws IOException {
        FileWriter writer = null;
        try {
            writer = new FileWriter(saveFile, isAppendContent);
            writer.write(text);
            return saveFile;
        } finally {
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 回调
     */
    public interface Callback {

        /**
         * 获取保存日志文件的路径
         */
        File getSaveLogPath();

    }
}
