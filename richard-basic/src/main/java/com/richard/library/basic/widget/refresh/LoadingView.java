package com.richard.library.basic.widget.refresh;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.richard.library.basic.R;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * <pre>
 * Description :加载视图
 * Author : admin-richard
 * Date : 2018/7/13 15:25
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2018/7/13 15:25     admin-richard         new file.
 * </pre>
 */
public class LoadingView extends RelativeLayout {

    private RotateAnimation progressAnimation;
    private ImageView progressImage;
    private float offsetAngle;


    public LoadingView(Context context) {
        super(context);
        this.init(null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.setGravity(Gravity.CENTER);

        LayoutParams centerInParentLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        centerInParentLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

        progressImage = new ImageView(getContext());
//        progressImage.setImageResource(R.mipmap.icon_loading_logo);
        progressImage.setImageResource(R.mipmap.loading);
        progressImage.setLayoutParams(centerInParentLayoutParams);

        this.addView(progressImage);
    }

    private void initProgressAnimation(){
        //初始化旋转动画
        progressAnimation = new RotateAnimation(offsetAngle, 359, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        progressAnimation.setDuration(800);
        progressAnimation.setRepeatCount(-1);
    }

    /**
     * 根据offsetAngle偏移角度
     *
     * @param offsetAngle 偏移量（0-360）
     */
    public void offsetAngle(float offsetAngle) {
        this.offsetAngle = offsetAngle * 1.5F;
        progressImage.setRotation(offsetAngle);
    }


    /**
     * 启动动画
     */
    public void startAnimation() {
        if(progressAnimation == null){
            this.initProgressAnimation();
        }
        if (progressAnimation != null) {
            progressImage.startAnimation(progressAnimation);
        }
    }

    /**
     * 取消动画
     */
    public void cancelAnimation() {
        if (progressAnimation != null) {
            progressAnimation.cancel();
        }
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (progressAnimation != null && View.GONE == visibility) {
            this.cancelAnimation();
        }
        super.onWindowVisibilityChanged(visibility);
    }

    @Override
    protected void onDetachedFromWindow() {
        if (progressAnimation != null) {
            this.cancelAnimation();
            progressAnimation = null;
        }
        super.onDetachedFromWindow();
    }
}
