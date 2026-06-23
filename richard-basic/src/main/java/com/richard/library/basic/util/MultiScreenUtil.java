package com.richard.library.basic.util;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;

import com.richard.library.context.AppContext;

/**
 * <pre>
 * Description : 副屏工具类
 * Author : admin-richard
 * Date : 2020-02-17 11:09
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020-02-17 11:09     admin-richard         new file.
 * </pre>
 */
public final class MultiScreenUtil {

    public static final int REQUEST_OVERLAY = 9999;

    //屏幕管理器
    private DisplayManager mDisplayManager;

    private static final class InstanceHolder {
        static final MultiScreenUtil instance = new MultiScreenUtil();
    }

    public static MultiScreenUtil get() {
        return InstanceHolder.instance;
    }


    /**
     * 获取屏幕管理器
     */
    public DisplayManager getDisplayManager() {
        if (mDisplayManager == null) {
            mDisplayManager = (DisplayManager) AppContext.get().getSystemService(Context.DISPLAY_SERVICE);
        }

        return mDisplayManager;
    }


    /**
     * 获取屏幕数组
     */
    public Display[] getDisplays() {
        DisplayManager displayManager = getDisplayManager();
        if (displayManager == null) {
            return null;
        }
        return displayManager.getDisplays();
    }

    /**
     * 获取指定屏幕
     *
     * @param displayNum 屏幕序号
     */
    public Display getDisplay(int displayNum) {
        Display[] displays = this.getDisplays();
        if (displays == null || displays.length <= displayNum) {
            return null;
        }
        return displays[displayNum];
    }

    /**
     * 获取主屏
     */
    public Display getMainDisplay() {
        Display[] displays = this.getDisplays();
        if (displays == null) {
            return null;
        }

        for (Display display : displays) {
            if (display.getDisplayId() == Display.DEFAULT_DISPLAY) {
                return display;
            }
        }
        return null;
    }

    /**
     * 获取第一个副屏(仅为主副屏两个屏幕的情况下适用)
     */
    public Display getSecDisplay() {
        Display[] displays = this.getDisplays();
        if (displays == null) {
            return null;
        }
        for (Display display : displays) {
            if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                return display;
            }
        }
        return null;
    }

    /**
     * 判断是否属于主屏幕
     */
    public static boolean isMainDisplay(Context context) {
        return !isSecondaryDisplay(context);
    }

    /**
     * 判断是否属于主屏幕
     */
    public static boolean isMainDisplay(Display display) {
        return !isSecondaryDisplay(display);
    }

    /**
     * 判断是否属于副屏
     */
    public static boolean isSecondaryDisplay(Context context) {
        return isSecondaryDisplay(getContextDisplay(context));
    }

    /**
     * 判断是否属于副屏
     */
    public static boolean isSecondaryDisplay(Display display) {
        // 2. 如果获取不到 Display 对象，通常意味着此 Context 未与特定屏幕关联，可按需处理（例如返回 false）
        if (display == null) {
            return false;
        }
        // 4. 判断此 ID 是否不等于默认（主屏）的 ID
        // 如果不等于，则说明此 Context 关联的是副屏
        return display.getDisplayId() != Display.DEFAULT_DISPLAY;
    }

    /**
     * 获取Display对象，兼容Android 5.0到Android 15
     *
     * @param context 上下文对象
     * @return Display对象
     */
    public static Display getContextDisplay(Context context) {
        Context uiContext = AppContext.getUIContext(context);
        if (uiContext == null) {
            return null;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                return uiContext.getDisplay();
            }

            WindowManager windowManager = (WindowManager) uiContext.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager != null) {
                return windowManager.getDefaultDisplay();
            }
        } catch (Exception e) {
            // 异常处理
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 启动activity 显示到第二屏幕
     */
    public static void startActivity(Context context, Class activityClass) {
        startActivity(context, null, activityClass, get().getSecDisplay());
    }

    /**
     * 启动activity 显示到第二屏幕
     */
    public static void startActivity(Context context, Bundle bundle, Class activityClass) {
        startActivity(context, bundle, activityClass, get().getSecDisplay());
    }

    /**
     * 启动activity 显示到指定屏幕
     */
    public static void startActivity(Context context, Bundle bundle, Class activityClass, Display display) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            ToastUtil.showWarning("当前设备不支持第二屏幕");
            return;
        }

        if (display == null) {
            ToastUtil.showWarning("未找到显示的副屏幕");
            return;
        }

        int secondDisplayId = display.getDisplayId();

        // 3. 创建 Intent 启动目标 Activity
        Intent intent = new Intent(context, activityClass);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        // 4. 设置启动标志，允许任务分离
        intent.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        // 5. 创建 ActivityOptions 并设置目标显示屏 ID
        ActivityOptions options = ActivityOptions.makeBasic();
        options.setLaunchDisplayId(secondDisplayId);
        context.startActivity(intent, options.toBundle());
    }

    /**
     * 显示副屏
     */
    public void show(Presentation presentation) {
        if (presentation == null || presentation.isShowing()) {
            return;
        }
        presentation.show();
    }


    /**
     * 关闭副屏幕
     */
    public void dismiss(Presentation presentation) {
        if (presentation == null || !presentation.isShowing()) {
            return;
        }
        presentation.dismiss();
    }

    /**
     * 6.0以上获取副屏权限（Presentation方式的副屏需要请求权限）
     */
    public void requestOverlayPermission(Context context, IBack iBack) {
        Activity activity = (Activity) context;
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.getPackageName()));
                activity.startActivityForResult(intent, REQUEST_OVERLAY);
                iBack.onNeedPermission();
            } else {
                iBack.onSuccess();
            }
        } else {
            iBack.onSuccess();
        }
    }

    public interface IBack {

        void onSuccess();

        void onNeedPermission();
    }
}
