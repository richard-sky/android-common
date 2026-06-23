package com.richard.library.basic.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.richard.library.basic.R;
import com.richard.library.basic.immersionbar.SystemBarUtil;
import com.richard.library.basic.util.DrawableUtil;
import com.richard.library.basic.util.HideNavBarUtil;
import com.richard.library.context.AppContext;
import com.richard.library.context.simple.SimpleCallback;
import com.richard.library.context.util.DensityUtilKt;

/**
 * <pre>
 * Description : 加载对话框
 * Author : admin-richard
 * Date : 2015/11/5 16:23
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2015/11/5 16:23     admin-richard         new file.
 * </pre>
 */
public class Loading {

    private static final Config config = new Config();
    private LoadingDialog dialog;

    private ImageView iv_close;
    private TextView tv_message;
    private ImageView iv_loading;
    private ImageView iv_icon;
    private TextView tv_time;
    private ProgressDrawable progressDrawable;
    private CountDownTimer countDownTimer;
    private volatile boolean isSecond = false;//当前是否正在跑秒倒计时
    private int lastSecond;//上一次倒计时秒数


    public Loading(Context context) {
        initDialog(context);
    }

    private void initDialog(Context context) {
        progressDrawable = new ProgressDrawable();
        progressDrawable.setColor(config.progressColor);

        dialog = new LoadingDialog(context, R.style.basic_loading_dialog);
        dialog.setCancelable(false);
        dialog.setCallback(data -> {
            progressDrawable.stop();
            stopCountDownTimer();
        });

        View contentView = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null);
        iv_close = contentView.findViewById(R.id.iv_close);
        tv_message = contentView.findViewById(R.id.tv_message);
        iv_loading = contentView.findViewById(R.id.iv_loading);
        iv_icon = contentView.findViewById(R.id.iv_icon);
        tv_time = contentView.findViewById(R.id.tv_time);

        //绑定事件
        iv_close.setOnClickListener(v -> dismiss(null));
        dialog.setOnKeyListener((dialog, keyCode, event) -> true);

        //设置背景颜色
        contentView.setBackground(DrawableUtil.generatorGradientDrawable(
                config.backgroundColor, DensityUtilKt.dp2px(5)));

        //----应用配置
        contentView.setMinimumWidth(DensityUtilKt.dp2px(config.widthDp, context));
        contentView.setMinimumHeight(DensityUtilKt.dp2px(config.heightDp, context));

        //loading
        iv_loading.setImageDrawable(progressDrawable);
        ViewGroup.LayoutParams loadingLp = iv_loading.getLayoutParams();
        loadingLp.width = DensityUtilKt.dp2px(config.iconSizeDp, context);
        loadingLp.height = DensityUtilKt.dp2px(config.iconSizeDp, context);
        iv_loading.setLayoutParams(loadingLp);

        //icon
        ViewGroup.LayoutParams iconLp = iv_icon.getLayoutParams();
        iconLp.width = DensityUtilKt.dp2px(config.iconSizeDp, context);
        iconLp.height = DensityUtilKt.dp2px(config.iconSizeDp, context);
        iv_icon.setLayoutParams(iconLp);

        //倒计时文本控件
        tv_time.setTextColor(config.countDownTextColor);
        tv_time.setTextSize(config.countDownTextSize);

        //message
        tv_message.setTextColor(config.messageTextColor);
        tv_message.setTextSize(config.messageTextSize);
        tv_message.setText(config.defaultMessageText);

        dialog.setContentView(contentView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        //dim
        if (!config.isShowDim && dialog.getWindow() != null) {
            dialog.getWindow().setDimAmount(0F);
        }
    }

    /**
     * 显示loading框
     */
    private void showLoading() {
        Activity activity = AppContext.getActivity(dialog.getContext());
        if (!SystemBarUtil.isHideBar(activity)) {
            this.dialog.show();
            return;
        }

        Window window = dialog.getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

        this.dialog.show();

        if (window != null) {
            HideNavBarUtil.hideBar(this.dialog.getWindow(), SystemBarUtil.getBarHide(activity));
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    /**
     * 显示loading框
     *
     * @param text 为null时，加载中
     */
    public void show(String text) {
        this.stopCountDownTimer();
        tv_message.setText(TextUtils.isEmpty(text) ? config.defaultMessageText : text);
        this.showLoading();
        progressDrawable.start();
    }

    /**
     * 显示loading框
     */
    public void show() {
        this.stopCountDownTimer();
        tv_message.setText(config.defaultMessageText);
        iv_icon.setVisibility(View.GONE);
        iv_loading.setVisibility(View.VISIBLE);
        this.showLoading();
        progressDrawable.start();
    }

    /**
     * 显示倒计时框
     *
     * @param second 倒计时秒数
     */
    public void show(int second) {
        this.show(null, second, null);
    }

    /**
     * 显示倒计时框
     *
     * @param text                 消息文本
     * @param second               倒计时秒数
     * @param countDownEndListener 倒计时结束回调
     */
    public void show(String text, int second, Runnable countDownEndListener) {
        if (countDownTimer != null) {
            this.stopCountDownTimer();
        }

        if (lastSecond != second) {
            this.lastSecond = second;
            countDownTimer = new CountDownTimer(second * 1000L, 1000) {
                @Override
                public void onTick(long l) {
                    if (dialog != null && dialog.isShowing()) {
                        tv_time.setText(String.format("%ss", (l / 1000) + 1));
                    }
                }

                @Override
                public void onFinish() {
                    if (isSecond && dialog != null && dialog.isShowing()) {
                        tv_time.setVisibility(View.GONE);
                        tv_message.setText(config.defaultMessageText);
                        iv_icon.setVisibility(View.GONE);
                        iv_loading.setVisibility(View.VISIBLE);
                        showLoading();
                        progressDrawable.start();

                        if (countDownEndListener != null) {
                            countDownEndListener.run();
                        }
                    }
                    isSecond = false;
                }
            };
        }

        this.show(text);
        tv_time.setVisibility(View.VISIBLE);
        countDownTimer.start();
        isSecond = true;
    }

    /**
     * 显示提示框(短暂显示)
     */
    public void showTip(String message, boolean isSuccess) {
        this.stopCountDownTimer();
        this.showLoading();
        this.handleShowTip(message, isSuccess, null);
    }

    /**
     * 关闭loading框
     */
    public void dismiss() {
        this.handleDismiss(null, false, null);
    }

    /**
     * 关闭loading框
     *
     * @param message dismiss之前提示的消息
     */
    public void dismiss(String message) {
        this.handleDismiss(message, false, null);
    }

    /**
     * 关闭loading框
     *
     * @param message    dismiss之前提示的消息
     * @param isSuccess  是否处理成功状态
     * @param onCloseRun dismiss时执行的业务
     */
    public void dismiss(String message, boolean isSuccess, Runnable onCloseRun) {
        this.handleDismiss(message, isSuccess, onCloseRun);
    }

    /**
     * 设置是否显示关闭按钮
     */
    public void setShowClose(boolean isShowClose) {
        iv_close.setVisibility(isShowClose ? View.VISIBLE : View.GONE);
    }

    /**
     * 停止倒计时器
     */
    private void stopCountDownTimer() {
        isSecond = false;
        tv_time.setVisibility(View.GONE);
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    /**
     * @param message    提示消息文本(值为“”或者为null时立即关闭加载框，否则验证关闭加载框)
     * @param isSuccess  是否成功（根据该值显示失败图标还是成功图标）
     * @param onCloseRun 在dismiss加载框时执行事件
     */
    private void handleDismiss(String message, boolean isSuccess, Runnable onCloseRun) {
        this.stopCountDownTimer();

        if (TextUtils.isEmpty(message)) {
            progressDrawable.stop();
            dialog.dismiss();
            if (onCloseRun != null) {
                onCloseRun.run();
            }
        } else {
            this.handleShowTip(message, isSuccess, onCloseRun);
        }
    }

    /**
     * 处理显示提示UI(短暂显示)
     */
    private void handleShowTip(String message, boolean isSuccess, Runnable onCloseRun) {
        boolean isShowClose = iv_close.getVisibility() == View.VISIBLE;
        iv_close.setVisibility(View.GONE);
        iv_loading.clearAnimation();
        iv_icon.setVisibility(View.VISIBLE);
        iv_icon.setImageResource(isSuccess ? config.successIcon : config.failIcon);
        iv_loading.setVisibility(View.GONE);
        tv_message.setText(message);

        tv_message.postDelayed(() -> {
            if (isShowClose) {
                iv_close.setVisibility(View.VISIBLE);
            }
            iv_icon.setVisibility(View.GONE);
            iv_loading.setVisibility(View.VISIBLE);
            progressDrawable.stop();
            dialog.dismiss();
            if (onCloseRun != null) {
                onCloseRun.run();
            }
        }, config.showTipTime);
    }

    /**
     * 加载框
     */
    private static class LoadingDialog extends Dialog {

        private SimpleCallback<Boolean> callback;

        public LoadingDialog(@NonNull Context context) {
            super(context);
        }

        public LoadingDialog(@NonNull Context context, int themeResId) {
            super(context, themeResId);
        }

        protected LoadingDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        public void onDetachedFromWindow() {
            if (callback != null) {
                callback.onCompleted(true);
            }
            super.onDetachedFromWindow();
        }

        public void setCallback(SimpleCallback<Boolean> callback) {
            this.callback = callback;
        }
    }

    /**
     * 获取配置信息
     */
    public static Config getConfig() {
        return config;
    }

    /**
     * 配置
     */
    public static class Config {

        /**
         * loading显示时是否显示背景蒙版
         */
        private boolean isShowDim = true;

        /**
         * 倒计时文本字体大小
         */
        private float countDownTextSize = 16;

        /**
         * 消息文本字体大小
         */
        private float messageTextSize = 13;

        /**
         * 倒计时文本字体颜色
         */
        private int countDownTextColor = Color.LTGRAY;

        /**
         * 消息文本字体颜色
         */
        private int messageTextColor = Color.LTGRAY;

        /**
         * 默认文本内容
         */
        private String defaultMessageText = "加载中";

        /**
         * 加载框宽度 dp
         */
        private int widthDp = 160;

        /**
         * 加载框高度 dp
         */
        private int heightDp = 120;

        /**
         * 图标宽高 dp
         */
        private int iconSizeDp = 45;

        /**
         * loading 背景颜色
         */
        private int backgroundColor = Color.parseColor("#323131");

        /**
         * 加载条颜色
         */
        private int progressColor = 0xffaaaaaa;

        /**
         * 操作成功图标资源id
         */
        private int successIcon = R.mipmap.icon_loading_success;

        /**
         * 操作失败图标资源id
         */
        private int failIcon = R.mipmap.icon_loading_fail;

        /**
         * 显示提示消息框时停留时间，毫秒
         */
        private long showTipTime = 1500;

        public Config setShowDim(boolean showDim) {
            isShowDim = showDim;
            return this;
        }

        public Config setCountDownTextSize(float countDownTextSize) {
            this.countDownTextSize = countDownTextSize;
            return this;
        }

        public Config setMessageTextSize(float messageTextSize) {
            this.messageTextSize = messageTextSize;
            return this;
        }

        public Config setDefaultMessageText(String defaultMessageText) {
            this.defaultMessageText = defaultMessageText;
            return this;
        }

        public Config setWidthDp(int widthDp) {
            this.widthDp = widthDp;
            return this;
        }

        public Config setHeightDp(int heightDp) {
            this.heightDp = heightDp;
            return this;
        }

        public Config setIconSizeDp(int iconSizeDp) {
            this.iconSizeDp = iconSizeDp;
            return this;
        }

        public Config setProgressColor(int progressColor) {
            this.progressColor = progressColor;
            return this;
        }

        public Config setSuccessIcon(int successIcon) {
            this.successIcon = successIcon;
            return this;
        }

        public Config setFailIcon(int failIcon) {
            this.failIcon = failIcon;
            return this;
        }

        public Config setBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Config setCountDownTextColor(int countDownTextColor) {
            this.countDownTextColor = countDownTextColor;
            return this;
        }

        public Config setMessageTextColor(int messageTextColor) {
            this.messageTextColor = messageTextColor;
            return this;
        }

        public Config setShowTipTime(long showTipTime) {
            this.showTipTime = showTipTime;
            return this;
        }
    }
}
