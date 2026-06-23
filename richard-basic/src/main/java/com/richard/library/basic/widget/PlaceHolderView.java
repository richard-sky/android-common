package com.richard.library.basic.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.richard.library.basic.R;
import com.richard.library.basic.util.MeasuredUtil;
import com.richard.library.context.util.DensityUtilKt;

/**
 * <pre>
 * Description : 界面内容占位图
 * Author : admin-richard
 * Date : 2018/7/29 14:29
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/7/29 14:29     admin-richard         new file.
 * </pre>
 */
@SuppressLint("ViewConstructor")
public class PlaceHolderView extends LinearLayout {

    public enum State {
        LOADING_DATA, EMPTY_DATA, SHOW_DATA, ERROR
    }

    private ProgressDrawable progress;
    private View loadingContent;
    private ImageView emptyImageView;
    private TextView textView;
    private State state;

    //和占位图切换对象内容view
    private final View targetView;


    public PlaceHolderView(Context context, View targetView) {
        super(context);
        this.targetView = targetView;
        this.init();


        //动态初始化内容占位图添加到界面layout中
        if (targetView.getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            targetView.getLayoutParams().height = MeasuredUtil.getMeasuredHeight(targetView);
        }
        this.setLayoutParams(targetView.getLayoutParams());

        if (targetView.getParent() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) targetView.getParent();
            viewGroup.addView(this, viewGroup.indexOfChild(targetView));
        }
    }

    private void init() {
        this.setVisibility(GONE);
        this.setGravity(Gravity.CENTER);
        this.setOrientation(LinearLayout.VERTICAL);

        //加载中视图
        progress = new ProgressDrawable();
        loadingContent = new ImageView(getContext());
        loadingContent.setBackground(progress);
        this.setLoadingSize(45, 45);

        //占位图片
        emptyImageView = new ImageView(getContext());
        emptyImageView.setImageResource(R.mipmap.default_empty_pic);
        this.setEmptyImageSize(230, 230);
        emptyImageView.setVisibility(GONE);

        //空数据|加载中|错误|时显示文本
        textView = new TextView(getContext());
        LayoutParams textViewLayoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT
        );
        textViewLayoutParams.topMargin = DensityUtilKt.dp2px(7, getContext());
        textView.setLayoutParams(textViewLayoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(13);
        textView.setTextColor(getResources().getColor(R.color.text));
        textView.setText("加载中");

        this.addView(loadingContent);
        this.addView(emptyImageView);
        this.addView(textView);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (progress != null) {
            progress.stop();
        }
        super.onDetachedFromWindow();
    }

    /**
     * 设置loading大小
     *
     * @param width  宽度,单位 dp
     * @param height 高度，单位 dp
     */
    public void setLoadingSize(int width, int height) {
        ViewGroup.LayoutParams lp = loadingContent.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(
                    width >= 0 ? DensityUtilKt.dp2px(width, getContext()) : width
                    , height >= 0 ? DensityUtilKt.dp2px(height, getContext()) : height
            );
        } else {
            lp.width = width >= 0 ? DensityUtilKt.dp2px(width, getContext()) : width;
            lp.height = height >= 0 ? DensityUtilKt.dp2px(height, getContext()) : height;
        }
        loadingContent.setLayoutParams(lp);
    }

    /**
     * 设置空数据展位图大小
     *
     * @param width  宽度,单位 dp
     * @param height 高度，单位 dp
     */
    public void setEmptyImageSize(int width, int height) {
        ViewGroup.LayoutParams lp = emptyImageView.getLayoutParams();
        if (lp == null) {
            lp = new ViewGroup.LayoutParams(
                    width >= 0 ? DensityUtilKt.dp2px(width, getContext()) : width
                    , height >= 0 ? DensityUtilKt.dp2px(height, getContext()) : height
            );
        } else {
            lp.width = width >= 0 ? DensityUtilKt.dp2px(width, getContext()) : width;
            lp.height = height >= 0 ? DensityUtilKt.dp2px(height, getContext()) : height;
        }
        emptyImageView.setLayoutParams(lp);
    }

    /**
     * 设置空占位图
     */
    public void setEmptyImage(@DrawableRes int emptyImageRes) {
        emptyImageView.setImageResource(emptyImageRes);
    }

    /**
     * 设置字体颜色
     */
    public void setTextColor(@ColorInt int textColor) {
        textView.setTextColor(textColor);
    }

    /**
     * 设置字体大小，单位 sp
     */
    public void setTextSize(int textSize) {
        textView.setTextSize(textSize);
    }

    /**
     * 设置内容状态
     */
    public void setState(@NonNull State state) {
        this.setState(state, null, null);
    }

    /**
     * 设置内容状态
     */
    public void setState(@NonNull State state, String text) {
        this.setState(state, text, null);
    }

    /**
     * 获取当前状态
     */
    public State getState() {
        return state;
    }

    /**
     * 设置内容状态
     * emptyImageResId 只有在加载中状态时有效
     */
    public void setState(@NonNull State state, String text, Integer statusImageResId) {
        this.state = state;
        switch (state) {
            case LOADING_DATA:
                this.setVisibility(VISIBLE);
                targetView.setVisibility(GONE);
                emptyImageView.setVisibility(GONE);
                loadingContent.setVisibility(VISIBLE);
                progress.start();
                textView.setText(TextUtils.isEmpty(text) ? "努力加载中" : text);
                break;
            case EMPTY_DATA:
                this.setVisibility(VISIBLE);
                targetView.setVisibility(GONE);
                emptyImageView.setVisibility(VISIBLE);
                loadingContent.setVisibility(GONE);
                progress.stop();

                if (statusImageResId != null) {
                    emptyImageView.setImageResource(statusImageResId);
                }
                textView.setText(TextUtils.isEmpty(text) ? "暂无数据内容" : text);
                break;
            case SHOW_DATA:
                this.setVisibility(GONE);
                targetView.setVisibility(VISIBLE);
                progress.stop();
                break;
            case ERROR:
                this.setVisibility(VISIBLE);
                targetView.setVisibility(GONE);
                emptyImageView.setVisibility(VISIBLE);
                loadingContent.setVisibility(GONE);
                progress.stop();

                if (statusImageResId != null) {
                    emptyImageView.setImageResource(statusImageResId);
                }
                textView.setText(TextUtils.isEmpty(text) ? "加载失败" : text);
                break;
        }
    }

}
