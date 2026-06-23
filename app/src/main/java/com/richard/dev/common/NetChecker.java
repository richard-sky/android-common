package com.richard.dev.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.richard.library.context.task.PollingTaskScheduler;
import com.richard.library.context.util.ThreadUtil;
import com.richard.library.context.AppContext;
import com.richard.library.context.util.UIThread;
import com.richard.library.net.http.request.Requester;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import kotlin.Suppress;
import okhttp3.Response;

public final class NetChecker {

    private static final String TAG = "NetChecker";

    private static volatile NetChecker instance;
    private final Context applicationContext;
    private final ConnectivityManager connectivityManager;
    private final PollingTaskScheduler pollingScheduler;
    private final NotifyTask notifyAllTask;
    private volatile Boolean lastNetConnected;
    private volatile boolean isSupportPing = true;

    //测试网络是否可用可达的远端地址(只能是域名，不能为IP形式)
    private String[] testUrl = new String[]{"https://www.huawei.com/", "https://www.so.com", "https://www.baidu.com", "https://www.aliyun.com", "https://www.tencent.com"};

    // 监听器相关
    private final List<NetworkStateListener> listeners = new CopyOnWriteArrayList<>();
    private boolean isMonitoring = false;

    // 传统广播方式（兼容低版本）
    private BroadcastReceiver networkBroadcastReceiver;

    // NetworkCallback方式（Android 5.0+）
    private ConnectivityManager.NetworkCallback networkCallback;

    public static NetChecker get() {
        if (instance == null) {
            synchronized (NetChecker.class) {
                if (instance == null) {
                    instance = new NetChecker(AppContext.get());
                }
            }
        }
        return instance;
    }

    private NetChecker(Context context) {
        this.applicationContext = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) this.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        pollingScheduler = new PollingTaskScheduler.Builder()
                .setFastIntervalTime(10000)
                .setSlowIntervalTime(10000)
                .build();
        notifyAllTask = new NotifyTask(listeners, testUrl, false);
    }


    /**
     * 初始化测试网络是否可用的远端地址(只能是域名，不能为IP形式)
     */
    public void setTestUrl(String[] testUrl) {
        this.testUrl = testUrl;
    }

    public interface NetworkStateListener {
        void onNetworkStateChanged(boolean isConnectedInternet);
    }

    /**
     * 开始监听网络状态变化
     */
    @Suppress(names = "all")
    public synchronized void start() {
        if (isMonitoring) {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.registerNetworkCallback();
        } else {
            this.registerLegacyReceiver();
        }

        isMonitoring = true;
        this.notifyListeners();

        if (testUrl != null && testUrl.length > 0) {
            pollingScheduler.start(new PollingTaskScheduler.PollingRunnable() {
                @Override
                public boolean run() throws Throwable {
                    notifyListeners();
                    return true;
                }

                @Override
                public void onException(Throwable e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 停止监听
     */
    @Suppress(names = "all")
    public synchronized void stop() {
        if (!isMonitoring) {
            return;
        }
        this.unregisterNetworkCallback();
        this.unregisterLegacyReceiver();
        this.pollingScheduler.stop();
        ThreadUtil.cancel(notifyAllTask);
        isMonitoring = false;
        lastNetConnected = null;
    }

    /**
     * 释放资源
     */
    public void release() {
        this.stop();
        this.listeners.clear();
    }

    /**
     * 当前是否已连接到互联网
     */
    public boolean isConnectedInternet() {
        if (!isMonitoring) {
            return true;
        }
        return lastNetConnected != null ? lastNetConnected : true;
    }

    /**
     * 注册监听器
     */
    public void addListener(NetworkStateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            // 立即通知当前状态
            notifyCurrentState(listener);
        }
    }

    /**
     * 取消注册监听器
     */
    public void removeListener(NetworkStateListener listener) {
        listeners.remove(listener);
    }

    /**
     * 现代API监听（Android 5.0+）
     */
    @Suppress(names = "all")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void registerNetworkCallback() {
        if (networkCallback == null) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    notifyListeners();
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    notifyListeners();
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities capabilities) {
                    // 当指定网络的能力发生变化时调用（这是获取网络详细信息的关键位置）
                    super.onCapabilitiesChanged(network, capabilities);
                    notifyListeners();
                }
            };
        }

        try {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        } catch (Exception e) {
            Log.e(TAG, "registerNetworkCallback error: " + e.getMessage());
            // 降级到传统方式
            registerLegacyReceiver();
        }
    }

    @Suppress(names = "all")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void unregisterNetworkCallback() {
        if (networkCallback != null) {
            try {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            } catch (Exception e) {
                Log.e(TAG, "unregisterNetworkCallback error: " + e.getMessage());
            }
            networkCallback = null;
        }
    }

    /**
     * 传统广播方式（兼容Android 4.x）
     */
    private void registerLegacyReceiver() {
        if (networkBroadcastReceiver == null) {
            networkBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                        notifyListeners();
                    }
                }
            };
        }

        try {
            IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
            applicationContext.registerReceiver(networkBroadcastReceiver, filter);
        } catch (Exception e) {
            Log.e(TAG, "registerLegacyReceiver error: " + e.getMessage());
        }
    }

    private void unregisterLegacyReceiver() {
        if (networkBroadcastReceiver != null) {
            try {
                applicationContext.unregisterReceiver(networkBroadcastReceiver);
            } catch (Exception e) {
                Log.e(TAG, "unregisterLegacyReceiver error: " + e.getMessage());
            }
            networkBroadcastReceiver = null;
        }
    }

    /**
     * 通知全部回调
     */
    private void notifyListeners() {
        if (isMonitoring) {
            ThreadUtil.executeByCached(notifyAllTask);
        }
    }

    /**
     * 通知执行执行回调
     */
    private void notifyCurrentState(NetworkStateListener listener) {
        if (isMonitoring) {
            ThreadUtil.executeByCached(new NotifyTask(List.of(listener), testUrl, true));
        } else if (listener != null) {
            UIThread.runOnUiThread(() -> listener.onNetworkStateChanged(isConnectedInternet()));
        }
    }

    /**
     * 通知网络监听事件任务
     */
    private static class NotifyTask extends ThreadUtil.RunTask {

        private final List<NetworkStateListener> listeners;
        private final String[] testUrl;
        private final boolean isForceExec;

        private NotifyTask(List<NetworkStateListener> listeners, String[] testUrl, boolean isForceExec) {
            this.listeners = listeners;
            this.testUrl = testUrl;
            this.isForceExec = isForceExec;
        }

        @Override
        public void runEvent() {
            final boolean isAvailable = checkNet();
            if (!isForceExec && NetChecker.get().lastNetConnected != null && NetChecker.get().lastNetConnected == isAvailable) {
                return;
            }
            NetChecker.get().lastNetConnected = isAvailable;

            for (int i = 0; i < listeners.size(); i++) {
                NetworkStateListener listener = listeners.get(i);
                try {
                    if (listener != null) {
                        UIThread.runOnUiThread(() -> listener.onNetworkStateChanged(isAvailable));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error notifying listener: " + e.getMessage());
                }
            }
        }

        /**
         * 检查网络是否真正畅通
         */
        private boolean checkNet() {
            try {
                if (!NetChecker.get().isConnected()) {
                    return false;
                }

                final String randUrl = testUrl[(int) (Math.random() * testUrl.length)];

                //DNS验证方案
                Callable<Boolean> checkByDnsCallable = () -> NetChecker.get().isAvailableByDns(randUrl);
                try {
                    if (ThreadUtil.getCachedPool().invokeAny(List.of(checkByDnsCallable), 2, TimeUnit.SECONDS)) {
                        return true;
                    }
                } catch (Throwable e) {
                    android.util.Log.d(TAG, e.getMessage() == null ? "" : e.getMessage());
                }

                //ping命令方案
                if (NetChecker.get().isSupportPing) {
                    Callable<Boolean> checkByPingCallable = () -> {
                        try {
                            return NetChecker.get().ping(randUrl);
                        } catch (IOException e) {
                            NetChecker.get().isSupportPing = false;
                            Log.e(TAG, "checkNet ping method error: " + e.getMessage());
                            return null;
                        }
                    };
                    try {
                        Boolean result = ThreadUtil.getCachedPool().invokeAny(List.of(checkByPingCallable), 3, TimeUnit.SECONDS);
                        if (result != null) {
                            return result;
                        }
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                        return false;
                    }
                }

                //head请求方案
                try (Response response = Requester.create().url(randUrl).head().timeout(3, TimeUnit.SECONDS).request()) {
                    if (response.isSuccessful()) {
                        return true;
                    }
                }

            } catch (Throwable e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    //------------------------------------------以下为工具方法-----------------------------------------

    /**
     * Return whether network is connected.
     * <p>Must hold {@code <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />}</p>
     *
     * @return {@code true}: connected<br>{@code false}: disconnected
     */
    private boolean isConnected() {
        NetworkInfo info = getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) AppContext.get().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return null;
        return cm.getActiveNetworkInfo();
    }


    /**
     * 判断是否有外网连接(通用方法)
     * -c ping的次数
     * -w 超时时间，单位：秒
     *
     * @param ipOrDomain 必填 ip地址或者域名（比如https://www.baidu.com/,则需传递www.baidu.com）
     * @return 网络是否畅通
     */
    private boolean ping(String ipOrDomain) throws IOException, InterruptedException {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec("ping -c 1 -w 2 " + convertDomain(ipOrDomain));
            int ret = process.waitFor();
            if (ret == 0) {
                return true;
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
            runtime.gc();
        }
        return false;
    }

    /**
     * Return whether network is available using domain.
     * <p>Must hold {@code <uses-permission android:name="android.permission.INTERNET" />}</p>
     *
     * @param domain The name of domain.
     * @return {@code true}: yes<br>{@code false}: no
     */
    private boolean isAvailableByDns(final String domain) {
        final String realDomain = TextUtils.isEmpty(domain) ? "www.baidu.com" : convertDomain(domain);
        try {
            InetAddress inetAddress = InetAddress.getByName(realDomain);
            return inetAddress.isReachable(2000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 去掉域名多余部分
     *
     * @param domain 域名
     */
    private String convertDomain(String domain) {
        if (TextUtils.isEmpty(domain)) {
            return domain;
        }
        return domain.replaceAll("https:|http:|/", "");
    }

}
