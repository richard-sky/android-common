package com.richard.library.basic.widget.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.richard.library.basic.R;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;

/**
 * <pre>
 * Description : 刷新头部视图
 * Author : admin-richard
 * Date : 2017/5/3 15:25
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/5/3 15:25     admin-richard         new file.
 * </pre>
 */
@SuppressLint("RestrictedApi")
public class SmartHeader extends FrameLayout implements RefreshHeader {

    private LoadingView header_progress;
//    private TextView mTitleTextView;


    public SmartHeader(Context context) {
        super(context);
        this.initViews();
    }

    public SmartHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initViews();
    }

    public SmartHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initViews();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SmartHeader(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.initViews();
    }

    protected void initViews() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_smart_header, this);
        header_progress = findViewById(R.id.header_progress);
//        mTitleTextView = findViewById(R.id.header_text);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        header_progress.offsetAngle(offset);
    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
//        mTitleTextView.setVisibility(VISIBLE);
//        mTitleTextView.setText("正在努力加载");
        header_progress.startAnimation();
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        header_progress.cancelAnimation();//停止动画
//        mTitleTextView.setVisibility(VISIBLE);
        if (success) {
//            mTitleTextView.setText("加载数据完成");
        } else {
//            mTitleTextView.setText("刷新失败");
        }
        return 0;//延迟500毫秒之后再弹回
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public boolean autoOpen(int duration, float dragRate, boolean animationOnly) {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
//                mTitleTextView.setText("下拉开始刷新");
                break;
            case ReleaseToRefresh:
//                mTitleTextView.setText("释放立即刷新");
                break;
        }
    }
}