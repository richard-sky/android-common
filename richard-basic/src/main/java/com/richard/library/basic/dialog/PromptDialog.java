package com.richard.library.basic.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.StringRes;

import com.richard.library.basic.R;
import com.richard.library.basic.immersionbar.SystemBarUtil;
import com.richard.library.basic.util.DrawableUtil;
import com.richard.library.basic.util.HideNavBarUtil;
import com.richard.library.context.AppContext;
import com.richard.library.context.util.DensityUtilKt;

/**
 * <pre>
 * Description : 提示对话框
 * Author : admin-richard
 * Date : 2017/1/23 14:15
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/1/23 14:15      admin-richard         new file.
 * </pre>
 */
public class PromptDialog extends Dialog {

    private PromptDialog(Context context, int theme) {
        super(context, theme);

    }

    private PromptDialog(Context context) {
        super(context);
    }

    @Override
    public void show() {
        Activity activity = AppContext.getActivity(getContext());
        if (!SystemBarUtil.isHideBar(activity)) {
            super.show();
            return;
        }

        Window window = getWindow();
        if (window != null) {
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }

        super.show();

        if (window != null) {
            HideNavBarUtil.hideBar(window, SystemBarUtil.getBarHide(activity));
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        }
    }

    public static class Builder {
        private final Context context;
        private String title;
        private Spanned message;
        private Integer widthDp;
        private String leftBtnText;
        private String centerBtnText;
        private String rightBtnText;
        private int leftTextColor;
        private int centerTextColor;
        private int rightTextColor;

        private boolean isClickDismiss = false;//是否点击弹出框中按钮的时候立即关闭弹出框，默认为true（立即关闭）

        private View contentView;
        private boolean cancelable = false;
        private int messageViewGravity = -1;//文本内容控件对齐方式

        private OnDialogClickListener leftBtnClickListener;
        private OnDialogClickListener centerBtnClickListener;
        private OnDialogClickListener rightBtnClickListener;

        public Builder(Context context) {
            this.leftTextColor = context.getResources().getColor(R.color.text);
            this.centerTextColor = context.getResources().getColor(R.color.text);
            this.rightTextColor = context.getResources().getColor(R.color.text);
            this.context = context;
        }

        public Builder setWidth(int widthDp) {
            this.widthDp = DensityUtilKt.dp2px(widthDp);
            return this;
        }

        public Builder setMarginLeftRight(int marginLeftRightDp) {
            widthDp = AppContext.getScreenWidth() - 2 * DensityUtilKt.dp2px(marginLeftRightDp);
            return this;
        }

        public Builder setMessage(String message) {
            this.setMessage(message, false);
            return this;
        }

        public Builder setMessage(String message, boolean isHtml) {
            this.message = TextUtils.isEmpty(message)
                    ? new SpannableString("")
                    : isHtml ? Html.fromHtml(message) : new SpannableString(message);
            return this;
        }

        public Builder setMessage(int message) {
            setMessage(AppContext.getString(message));
            return this;
        }

        public Builder setTitle(int titleRes) {
            this.title = AppContext.getString(titleRes);
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setContentView(View contentView) {
            this.contentView = contentView;
            return this;
        }

        public Builder setLeftBtn(String text, OnDialogClickListener listener) {
            this.leftBtnText = text;
            this.leftBtnClickListener = listener;
            return this;
        }

        public Builder setLeftBtn(@StringRes int textRes, OnDialogClickListener listener) {
            this.leftBtnText = AppContext.getString(textRes);
            this.leftBtnClickListener = listener;
            return this;
        }

        public Builder setLeftBtn(String text, @ColorInt int leftTextColor, OnDialogClickListener listener) {
            this.leftBtnText = text;
            this.leftTextColor = leftTextColor;
            this.leftBtnClickListener = listener;
            return this;
        }

        public Builder setLeftBtn(@StringRes int textRes, @ColorInt int leftTextColor, OnDialogClickListener listener) {
            this.leftBtnText = AppContext.getString(textRes);
            this.leftTextColor = leftTextColor;
            this.leftBtnClickListener = listener;
            return this;
        }

        public Builder setCenterBtn(String text, OnDialogClickListener listener) {
            this.centerBtnText = text;
            this.centerBtnClickListener = listener;
            return this;
        }

        public Builder setCenterBtn(@StringRes int textRes, OnDialogClickListener listener) {
            this.centerBtnText = AppContext.getString(textRes);
            this.centerBtnClickListener = listener;
            return this;
        }

        public Builder setCenterBtn(String text, @ColorInt int centerTextColor, OnDialogClickListener listener) {
            this.centerBtnText = text;
            this.centerTextColor = centerTextColor;
            this.centerBtnClickListener = listener;
            return this;
        }

        public Builder setCenterBtn(@StringRes int textRes, @ColorInt int centerTextColor, OnDialogClickListener listener) {
            this.centerBtnText = AppContext.getString(textRes);
            this.centerTextColor = centerTextColor;
            this.centerBtnClickListener = listener;
            return this;
        }

        public Builder setRightBtn(String text, OnDialogClickListener listener) {
            this.rightBtnText = text;
            this.rightBtnClickListener = listener;
            return this;
        }

        public Builder setRightBtn(@StringRes int textRes, OnDialogClickListener listener) {
            this.rightBtnText = AppContext.getString(textRes);
            this.rightBtnClickListener = listener;
            return this;
        }

        public Builder setRightBtn(String text, @ColorInt int rightTextColor, OnDialogClickListener listener) {
            this.rightBtnText = text;
            this.rightTextColor = rightTextColor;
            this.rightBtnClickListener = listener;
            return this;
        }

        public Builder setRightBtn(@StringRes int textRes, @ColorInt int rightTextColor, OnDialogClickListener listener) {
            this.rightBtnText = AppContext.getString(textRes);
            this.rightTextColor = rightTextColor;
            this.rightBtnClickListener = listener;
            return this;
        }

        public Builder setMessageGravity(int gravity) {
            messageViewGravity = gravity;
            return this;
        }

        public void setClickDismiss(boolean clickDismiss) {
            isClickDismiss = clickDismiss;
        }

        public PromptDialog create() {
            float radius = DensityUtilKt.dp2px(7);
            int stateColor1 = context.getResources().getColor(R.color.prompt_dialog_button_un_press_bg);
            int stateColor2 = context.getResources().getColor(R.color.prompt_dialog_button_pressed_bg);

            final PromptDialog dialog = new PromptDialog(context, R.style.dialog_round_corner);
            View layout = LayoutInflater.from(context).inflate(R.layout.dialog_prompt, null);

            //找到组件
            TextView tvTitle = layout.findViewById(R.id.dialog_title);
            FrameLayout flContent = layout.findViewById(R.id.dialog_common_tip_fl_content_view);
            View contentBtn = layout.findViewById(R.id.content_btn);
            TextView tvLeft = layout.findViewById(R.id.dialog_left_tv);
            TextView tvCenter = layout.findViewById(R.id.dialog_center_tv);
            TextView tvRight = layout.findViewById(R.id.dialog_right_tv);
            TextView tvContentText = layout.findViewById(R.id.dialog_content_tv);
            View line_one = layout.findViewById(R.id.line_one);
            View line_two = layout.findViewById(R.id.line_two);

            //支持TextView可以滑动内容
            tvContentText.setMovementMethod(ScrollingMovementMethod.getInstance());

            //标题显示
            if (!TextUtils.isEmpty(title)) {
                tvTitle.setVisibility(View.VISIBLE);
                tvTitle.setText(title);
            }

            //提示消息内容显示
            if (!TextUtils.isEmpty(message)) {
                tvContentText.setVisibility(View.VISIBLE);
                tvContentText.setText(message);
            }

            //提示消息内容对齐方式
            if (messageViewGravity != -1) {
                tvContentText.setGravity(messageViewGravity);
            }

            //左边按钮
            if (!TextUtils.isEmpty(leftBtnText)) {
                contentBtn.setVisibility(View.VISIBLE);
                tvLeft.setVisibility(View.VISIBLE);
                tvLeft.setText(leftBtnText);
                tvLeft.setTextColor(leftTextColor);

                if (TextUtils.isEmpty(centerBtnText) && TextUtils.isEmpty(rightBtnText)) {
                    tvLeft.setBackground(DrawableUtil.generatorSelector(android.R.attr.state_pressed,
                            stateColor1, stateColor2, 0, 0, radius, radius));
                } else {
                    tvLeft.setBackground(DrawableUtil.generatorSelector(android.R.attr.state_pressed,
                            stateColor1, stateColor2, 0, 0, radius, 0));
                    line_one.setVisibility(View.VISIBLE);
                }

                bindListener(dialog, tvLeft, leftBtnClickListener);
            }

            //中间按钮
            if (!TextUtils.isEmpty(centerBtnText)) {
                contentBtn.setVisibility(View.VISIBLE);
                tvCenter.setVisibility(View.VISIBLE);
                tvCenter.setText(centerBtnText);
                tvCenter.setTextColor(centerTextColor);

                if (TextUtils.isEmpty(leftBtnText) && TextUtils.isEmpty(rightBtnText)) {
                    tvCenter.setBackground(DrawableUtil.generatorSelector(android.R.attr.state_pressed,
                            stateColor1, stateColor2, 0, 0, radius, radius));
                } else {
                    tvCenter.setBackground(DrawableUtil.generatorSelector(android.R.attr.state_pressed,
                            stateColor1, stateColor2, 0, 0, 0, 0));
                    line_two.setVisibility(View.VISIBLE);
                }

                bindListener(dialog, tvCenter, centerBtnClickListener);
            }

            //右边按钮
            if (!TextUtils.isEmpty(rightBtnText)) {
                contentBtn.setVisibility(View.VISIBLE);
                tvRight.setVisibility(View.VISIBLE);
                tvRight.setText(rightBtnText);
                tvRight.setTextColor(rightTextColor);

                if (TextUtils.isEmpty(leftBtnText) && TextUtils.isEmpty(centerBtnText)) {
                    tvRight.setBackground(DrawableUtil.generatorSelector(android.R.attr.state_pressed, stateColor1,
                            stateColor2, 0, 0, radius, radius));
                } else {
                    tvRight.setBackground(DrawableUtil.generatorSelector(android.R.attr.state_pressed, stateColor1,
                            stateColor2, 0, 0, 0, radius));
                }

                bindListener(dialog, tvRight, rightBtnClickListener);
            }

            //判断contentView（内容布局）是否为NULL，若不为NULL就使用该自定义布局内容，否则使用默认布局
            if (contentView != null) {
                flContent.setVisibility(View.VISIBLE);
                flContent.addView(contentView);
            }

            //设置是否能点击外部关闭弹出框
            dialog.setCancelable(cancelable);

            //若未指定dialog宽度就设置为默认宽度
            if (widthDp == null) {
                if (AppContext.isScreenLandscape()) {
                    setWidth(380);
                } else {
                    setMarginLeftRight(25);
                }
            }

            dialog.setContentView(layout, new LayoutParams(widthDp, LayoutParams.WRAP_CONTENT));

            return dialog;
        }


        /**
         * 绑定事件
         */
        private void bindListener(final Dialog dialog, View bindView, final OnDialogClickListener listener) {
            if (listener != null) {
                bindView.setOnClickListener(v -> {
                    if (isClickDismiss) {
                        dialog.dismiss();
                    }
                    listener.onClick(dialog, v);
                });
            } else {
                bindView.setOnClickListener(v -> dialog.dismiss());
            }
        }
    }

    public interface OnDialogClickListener {

        void onClick(DialogInterface dialogInterface, View view);

    }
}
