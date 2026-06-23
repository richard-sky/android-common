package com.richard.library.basic.basic;

import android.app.Presentation;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.uiview.UIView;
import com.richard.library.basic.basic.uiview.UIViewImpl;
import com.richard.library.basic.eventbus.EventData;
import com.richard.library.context.AppContext;
import com.richard.library.context.util.DensityUtilKt;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * <pre>
 * Description : 副屏（默认全屏显示）Presentation基类
 * Author : admin-richard
 * Date : 2019-09-25 14:36
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-09-25 14:36      admin-richard         new file.
 * </pre>
 */
public abstract class BasicPresentation extends Presentation implements UIInitializer, LifecycleOwner, ViewModelStoreOwner {

    private LifecycleRegistry lifecycleRegistry;
    private ViewModelStore viewModelStore;
    private UIView uiView;

    private float dimAmount = 0.5f;//背景昏暗度
    private boolean showBottomEnable;//是否底部显示
    private int marginLeftRightDp = 0;//左右边距
    private int marginTopBottomDp = 0;//上下边距
    private int mAnimStyle;//进入退出动画
    private int widthDp = 0;
    private int heightDp = 0;

    private boolean isFirstInit = true;//是否为首次初始化

    public BasicPresentation(Context outerContext, Display display) {
        this(outerContext, display, R.style.dialog_round_corner);
    }

    public BasicPresentation(Context outerContext, Display display, int theme) {
        super(outerContext, display, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiView = new UIViewImpl(getContext());
        lifecycleRegistry = new LifecycleRegistry(this);

        this.initLayoutView();
        this.initData();
        this.bindListener();
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
    }

    @Override
    public void onAttachedToWindow() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (lifecycleRegistry.getCurrentState() == Lifecycle.State.DESTROYED) {
            lifecycleRegistry = new LifecycleRegistry(this);
            lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        }

        if (isFirstInit) {
            isFirstInit = false;
            this.initParams();
        }
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
        if (viewModelStore != null) {
            viewModelStore.clear();
        }
        super.onDetachedFromWindow();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return this.lifecycleRegistry;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (viewModelStore == null) {
            viewModelStore = new ViewModelStore();
        }
        return viewModelStore;
    }

    @Override
    protected void onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (uiView != null) {
            uiView.dismissLoading();
            uiView.dismissMsgDialog();
        }
        super.onStop();
    }

    @Override
    public void show() {
        super.show();
        lifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
    }

    /**
     * 获取消息提示UIView
     */
    public UIView getUIView() {
        return uiView;
    }

    @SuppressWarnings("rawtypes")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventData event) {
        this.onReceiveMessageEvent(event);
    }

    /**
     * 当接收到EventBus事件时会调用
     */
    @SuppressWarnings("rawtypes")
    protected void onReceiveMessageEvent(EventData event) {
    }

    /**
     * 查找布局视图中的控件
     *
     * @param viewId 控件id
     */
    public <T extends View> T findViewById(@IdRes int viewId) {
        return super.findViewById(viewId);
    }

    /**
     * 初始化属性参数
     */
    private void initParams() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = dimAmount;

            //设置dialog显示位置
            if (showBottomEnable) {
                params.gravity = Gravity.BOTTOM;
            }

            //设置dialog宽度
            if (widthDp == 0) {
                params.width = AppContext.getDisplayWidth(super.getDisplay()) - 2 * DensityUtilKt.dp2px(marginLeftRightDp);
            } else if (widthDp == WindowManager.LayoutParams.WRAP_CONTENT
                    || widthDp == WindowManager.LayoutParams.MATCH_PARENT) {
                params.width = widthDp;
            } else {
                params.width = DensityUtilKt.dp2px(widthDp);
            }

            //设置dialog高度
            if (heightDp == 0) {
                params.height = AppContext.getDisplayHeight(super.getDisplay()) - 2 * DensityUtilKt.dp2px(marginTopBottomDp);
            } else if (heightDp == WindowManager.LayoutParams.WRAP_CONTENT
                    || heightDp == WindowManager.LayoutParams.MATCH_PARENT) {
                params.height = heightDp;
            } else {
                params.height = DensityUtilKt.dp2px(heightDp);
            }

            //设置dialog动画
            if (mAnimStyle != 0) {
                window.setWindowAnimations(mAnimStyle);
            }

            window.setAttributes(params);
        }
    }

    /**
     * 设置背景昏暗度
     *
     * @param level 黑色透明级别
     */
    public BasicPresentation setBackgroundBlackAlphaLevel(@FloatRange(from = 0, to = 1) float level) {
        dimAmount = level;
        return this;
    }

    /**
     * 是否显示底部
     *
     * @param showBottom 是否显示在底部
     */
    public BasicPresentation setShowBottom(boolean showBottom) {
        showBottomEnable = showBottom;
        return this;
    }

    /**
     * 设置宽高
     *
     * @param widthDp  以dp为单位的宽
     * @param heightDp 以dp为单位的高
     */
    public BasicPresentation setSize(int widthDp, int heightDp) {
        this.widthDp = widthDp;
        this.heightDp = heightDp;
        return this;
    }

    /**
     * 设置宽
     *
     * @param widthDp 以dp为单位的宽
     */
    public BasicPresentation setWidth(int widthDp) {
        this.widthDp = widthDp;
        return this;
    }

    /**
     * 设置高
     *
     * @param heightDp 以dp为单位的高
     */
    public BasicPresentation setHeight(int heightDp) {
        this.heightDp = heightDp;
        return this;
    }

    /**
     * 设置上下margin
     *
     * @param marginLeftRightDp 左右margin
     * @param marginTopBottomDp 上下margin
     */
    public BasicPresentation setMargin(int marginLeftRightDp, int marginTopBottomDp) {
        this.marginLeftRightDp = marginLeftRightDp;
        this.marginTopBottomDp = marginTopBottomDp;
        this.widthDp = 0;
        this.heightDp = 0;
        return this;
    }

    /**
     * 设置左右margin
     *
     * @param marginLeftRightDp 左右边距
     */
    public BasicPresentation setMarginLeftRight(int marginLeftRightDp) {
        this.marginLeftRightDp = marginLeftRightDp;
        this.widthDp = 0;
        return this;
    }

    /**
     * 设置上下margin
     *
     * @param marginTopBottomDp 上下边距
     */
    public BasicPresentation setMarginTopBottom(int marginTopBottomDp) {
        this.marginTopBottomDp = marginTopBottomDp;
        this.heightDp = 0;
        return this;
    }

    /**
     * 设置进入退出动画
     *
     * @param animStyle 动画样式资源id
     */
    public BasicPresentation setAnimStyle(@StyleRes int animStyle) {
        mAnimStyle = animStyle;
        return this;
    }

    /**
     * 设置是否点击外部取消
     *
     * @param outCancel 是否点击外部取消
     */
    public BasicPresentation setOutCancel(boolean outCancel) {
        super.setCancelable(outCancel);
        return this;
    }

    /**
     * 设置允许出现在其它应用上（必须先在系统设置中开启"出现在其它应用上"的权限）
     * 并且在清单文件中添加：
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
     * <uses-permission android:name="android.permission.SYSTEM_OVERLAY_WINDOW" />
     */
    public BasicPresentation allowCanShowOnOtherApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        } else {
            getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
        }
        return this;
    }
}
