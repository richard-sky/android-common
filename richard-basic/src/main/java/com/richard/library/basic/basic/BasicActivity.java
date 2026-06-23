package com.richard.library.basic.basic;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AppCompatActivity;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.dict.Direction;
import com.richard.library.basic.basic.uiview.UIView;
import com.richard.library.basic.basic.uiview.UIViewImpl;
import com.richard.library.basic.eventbus.EventData;
import com.richard.library.context.immersionbar.ImmersionBar;
import com.richard.library.context.immersionbar.SystemBarUtil;
import com.richard.library.context.AppContext;
import com.richard.library.context.util.DensityUtilKt;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * <pre>
 * Description : Activity基类
 * Author : admin-richard
 * Date : 2019-05-10 17:57
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-10 17:57      admin-richard         new file.
 * </pre>
 */
public abstract class BasicActivity extends AppCompatActivity implements UIInitializer {

    private Context context;
    private UIView mUIView;
    private ImmersionBar systemBar;
    private boolean isOpenTranslucentStatusBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        isOpenTranslucentStatusBar = getResources().getBoolean(R.bool.navigation_bar_open_translucent_bar);
        systemBar = SystemBarUtil.withBar(this);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        mUIView = new UIViewImpl(context);
        this.initLayoutView();
        this.initData();
        this.bindListener();

        if (!systemBar.initialized()) {
            if (isOpenTranslucentStatusBar && !systemBar.isSetTitleBarView()) {
                systemBar.titleBarMarginTop(findViewById(android.R.id.content));
            }
            systemBar.init();
        }

        this.initParams();
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (mUIView != null) {
            mUIView.dismissLoading();
            mUIView.dismissMsgDialog();
            mUIView = null;
        }
        context = null;
        super.onDestroy();
    }

    /**
     * 设置是否开启沉侵式状态栏
     */
    public void setOpenTranslucentStatusBar(boolean openTranslucentStatusBar) {
        isOpenTranslucentStatusBar = openTranslucentStatusBar;
    }

    /**
     * 获取context
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取ui状态view实现
     */
    public UIView getUIView() {
        return mUIView;
    }

    /**
     * 获取控制系统状态栏和导航栏控制
     */
    public ImmersionBar getSystemBar() {
        return systemBar;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventData event) {
        this.onReceiveMessageEvent(event);
    }

    /**
     * 当接收到EventBus事件时会调用
     */
    protected void onReceiveMessageEvent(EventData event) {
    }


    //----------------------------------------------------------------------------------------------
    private Float mDimAmount;//背景昏暗度
    private Direction direction;//dialog显示方向位置
    private Integer marginLeftRightDp;//左右边距
    private Integer marginTopBottomDp;//上下边距
    private Integer mAnimStyle;//进入退出动画
    private Integer widthDp;
    private Integer heightDp;

    private void initParams() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();

            if (mDimAmount != null) {
                params.dimAmount = mDimAmount;
            }

            //设置dialog显示位置
            if (direction != null) {
                switch (direction) {
                    case TOP:
                        params.gravity = Gravity.TOP;
                        break;
                    case BOTTOM:
                        params.gravity = Gravity.BOTTOM;
                        break;
                    case LEFT:
                        params.gravity = Gravity.START;
                        break;
                    case RIGHT:
                        params.gravity = Gravity.END;
                        break;
                }
            }

            //设置dialog宽度
            if (widthDp != null) {
                if (widthDp == WindowManager.LayoutParams.WRAP_CONTENT
                        || widthDp == WindowManager.LayoutParams.MATCH_PARENT) {
                    params.width = widthDp;
                }
            } else {
                if (marginLeftRightDp != null) {
                    params.width = AppContext.getScreenWidth() - 2 * DensityUtilKt.dp2px(marginLeftRightDp);
                }
            }

            //设置dialog高度
            if (heightDp != null) {
                if (heightDp == WindowManager.LayoutParams.WRAP_CONTENT
                        || heightDp == WindowManager.LayoutParams.MATCH_PARENT) {
                    params.height = heightDp;
                }
            } else {
                if (marginTopBottomDp != null) {
                    params.height = AppContext.getScreenHeight() - 2 * DensityUtilKt.dp2px(marginTopBottomDp);
                }
            }

            //设置dialog动画
            if (mAnimStyle != null) {
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
    public void setBackgroundBlackAlphaLevel(@FloatRange(from = 0, to = 1) float level) {
        mDimAmount = level;
    }

    /**
     * 显示方向位置
     */
    public void setShowDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * 设置宽高
     *
     * @param widthDp  以dp为单位的宽
     * @param heightDp 以dp为单位的高
     */
    public void setSize(int widthDp, int heightDp) {
        this.widthDp = widthDp;
        this.heightDp = heightDp;
    }

    /**
     * 设置宽
     *
     * @param widthDp 以dp为单位的宽
     */
    public void setWidth(int widthDp) {
        this.widthDp = widthDp;
    }

    /**
     * 设置高
     *
     * @param heightDp 以dp为单位的高
     */
    public void setHeight(int heightDp) {
        this.heightDp = heightDp;
    }

    /**
     * 设置上下margin
     *
     * @param marginLeftRightDp 左右margin
     * @param marginTopBottomDp 上下margin
     */
    public void setMargin(int marginLeftRightDp, int marginTopBottomDp) {
        this.marginLeftRightDp = marginLeftRightDp;
        this.marginTopBottomDp = marginTopBottomDp;
        this.widthDp = null;
        this.heightDp = null;
    }

    /**
     * 设置左右margin
     *
     * @param marginLeftRightDp 左右边距
     */
    public void setMarginLeftRight(int marginLeftRightDp) {
        this.marginLeftRightDp = marginLeftRightDp;
        this.widthDp = null;
    }

    /**
     * 设置上下margin
     *
     * @param marginTopBottomDp 上下边距
     */
    public void setMarginTopBottom(int marginTopBottomDp) {
        this.marginTopBottomDp = marginTopBottomDp;
        this.heightDp = null;
    }

    /**
     * 设置进入退出动画
     *
     * @param animStyle 动画样式资源id
     */
    public void setAnimStyle(@StyleRes int animStyle) {
        mAnimStyle = animStyle;
    }
}
