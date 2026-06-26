package com.richard.dev.common.application;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.StrictMode;

import androidx.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.richard.library.basic.crash.CrashHandler;
import com.richard.library.basic.eventbus.CrossProcessReceiver;
import com.richard.library.context.immersionbar.BarHide;
import com.richard.library.context.immersionbar.SystemBarUtil;
import com.richard.library.context.util.LogUtil;
import com.richard.library.basic.widget.Loading;
import com.richard.library.bluetooth.core.BleManager;
import com.richard.library.context.AppContext;
import com.richard.library.context.util.media.TTSSpeaker;
import com.richard.library.net.http.request.LogCallback;
import com.richard.library.net.http.request.RequestClient;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.onAdaptListener;
import okhttp3.Request;

/**
 * author Richard
 * date 2019-06-04 11:03
 * version V1.0
 * description: App
 * \u0020为半角空格，\u3000为全角空格
 */
public class App extends MultiDexApplication {

    private static App instance;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.init();
    }


    /**
     * 初始化
     */
    private void init() {
        //初始化Context管理工具类
        AppContext.init(this, true);
        SystemBarUtil.hideBar(BarHide.FLAG_SHOW_BAR);

        if (/*AppContext.isDebug()*/false) {
            // 线程策略（检测主线程问题）
            StrictMode.ThreadPolicy threadPolicy = new StrictMode.ThreadPolicy.Builder()
                    //.detectDiskReads()      // 检测磁盘读
                    //.detectDiskWrites()     // 检测磁盘写
                    .detectNetwork()        // 检测网络请求
                    .detectCustomSlowCalls() // 检测自定义慢方法
                    .penaltyLog()           // 违例时输出日志
                    .penaltyDialog()        // 违例时弹窗（仅Debug）
                    .build();
            StrictMode.setThreadPolicy(threadPolicy);

            // 虚拟机策略（检测内存问题）
            StrictMode.VmPolicy vmPolicy = new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()    // 检测SQLite对象未关闭
                    .detectLeakedClosableObjects()   // 检测Closable资源未关闭
                    .detectActivityLeaks()           // 检测Activity泄漏
                    //.setClassInstanceLimit(MainActivity.class, 1) // 限制类实例数量
                    .penaltyLog()
                    .penaltyDeath()                  // 严重违例时崩溃应用
                    .build();
            StrictMode.setVmPolicy(vmPolicy);
        }

        //TTS初始化
        TTSSpeaker.getInstance().init(null);

        //初始化异常统一处理器
        CrashHandler.getInstance().init();

        //初始化发送EventBus跨进程广播
        CrossProcessReceiver.init(this);

        RequestClient.get().config()
                .readTimeout(10)
                .logCallback(new LogCallback() {

                    @Override
                    public boolean isLogHeader() {
                        return false;
                    }

                    @Override
                    public void onRequestLog(Request request, String log) {
                        LogUtil.file("request", "request", log);
                    }

                    @Override
                    public void log(Request request, String log) {
                        LogUtil.file("request", "response", log);
                    }
                });

        //初始化日志工具类
        LogUtil.getConfig()
                .setGlobalTag("api")
                .setDir(AppContext.get().getFilesDir() + "/logs")
                .setLogFileHoldDays(1)
                .setIs2FilePrintConsole(AppContext.isDebug())
                .setFilePrefix("Log")
                .setLogHeadSwitch(false)
                .setBorderSwitch(false)
                .setLogSwitch(true);

        ARouter.init(this);
        ARouter.openLog();
        ARouter.openDebug();

        //初始化蓝牙
        BleManager.getInstance()
                .enableLog(AppContext.isDebug())
                .setReConnectCount(2, 1000)
                .setConnectOverTime(3000)
                .init(this);

        //初始化Loading
        Loading.getConfig()
                .setWidthDp(100)
                .setHeightDp(90)
                .setIconSizeDp(30)
                .setShowDim(false);

        //AndroidAutoSize初始化配置
        AutoSizeConfig.getInstance().setOnAdaptListener(new onAdaptListener() {
            @Override
            public void onAdaptBefore(Object target, Activity activity) {
                // 根据当前屏幕方向设置不同的设计图尺寸 autoSize初始化(411dp*731dp 即 1080 * 1920)通过xml布局设计器可得到
                if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    // 横屏时使用横屏设计图尺寸
                    AutoSizeConfig.getInstance()
                            .setDesignWidthInDp(1366)  // 横屏设计图宽度
                            .setDesignHeightInDp(768); // 横屏设计图高度
                } else {
                    // 竖屏时使用竖屏设计图尺寸
                    AutoSizeConfig.getInstance()
                            .setDesignWidthInDp(360)   // 竖屏设计图宽度
                            .setDesignHeightInDp(640); // 竖屏设计图高度
                }
            }

            @Override
            public void onAdaptAfter(Object target, Activity activity) {
                // 适配完成后可执行的操作
            }
        });

    }
}
