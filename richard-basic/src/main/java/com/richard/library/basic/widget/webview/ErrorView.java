package com.richard.library.basic.widget.webview;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.richard.library.basic.R;
import com.richard.library.context.util.DensityUtilKt;


/**
 * <pre>
 * Description : 错误视图
 * Author : admin-richard
 * Date : 2018/12/28 13:39
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/12/28 13:39     admin-richard         new file.
 * </pre>
 */
public class ErrorView extends LinearLayout {

    private TextView errorTextView;
    private Callback mCallback;

    public ErrorView(Context context) {
        super(context);
        this.initView();
    }

    public ErrorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public ErrorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ErrorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initView();
    }

    private void initView() {
        this.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT
        ));
        this.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);

        //错误文本显示控件
        errorTextView = new TextView(getContext());
        errorTextView.setGravity(Gravity.CENTER);
        errorTextView.setTextColor(Color.GRAY);
        errorTextView.setTextSize(13);
        errorTextView.setText("加载页面失败");

        //错误图片显示控件
        ImageView errorImageView = new ImageView(getContext());
        errorImageView.setImageResource(R.mipmap.icon_webview_empty);

        //重新加载按钮
        TextView retryLoadButton = new TextView(getContext());
        retryLoadButton.setGravity(Gravity.CENTER);
        retryLoadButton.setBackgroundResource(R.drawable.shape_back_main_color_radius);
        retryLoadButton.setText("重新加载");
        retryLoadButton.setTextSize(14);
        retryLoadButton.setTextColor(Color.WHITE);

        //设置该控件的上边距
        LayoutParams retryLoadButtonLP = new LayoutParams(
                DensityUtilKt.dp2px(140F, getContext())
                , DensityUtilKt.dp2px(35F, getContext())
        );
        retryLoadButtonLP.topMargin = DensityUtilKt.dp2px(30F, getContext());

        retryLoadButton.setLayoutParams(retryLoadButtonLP);
        retryLoadButton.setOnClickListener((View view) -> {
            if (mCallback != null) {
                ErrorView.this.setVisibility(GONE);
                mCallback.onClickReload();
            }
        });

        this.addView(errorImageView);
        this.addView(errorTextView);
        this.addView(retryLoadButton);
        this.setVisibility(GONE);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorTextView.setText(errorMessage);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {

        void onClickReload();

    }
}
