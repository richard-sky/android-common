package com.richard.library.basic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.richard.library.context.task.PollingTaskScheduler;
import com.richard.library.context.util.LogUtil;
import com.richard.library.context.util.UIThread;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Richard
 * @createDate: 2024/3/29 18:00
 * @version: 1.0
 * @description: 简单banner
 */
public abstract class SimpleBanner<T> extends AppCompatImageView {

    private final List<T> data = new ArrayList<>();
    private PollingTaskScheduler scheduler;
    private RunEvent<T> runEvent;
    private long intervalTime = 1500;
    private int imagePosition = -1;
    private boolean isInit;


    public SimpleBanner(@NonNull Context context) {
        super(context);
        this.init();
    }

    public SimpleBanner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public SimpleBanner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    private void init() {
        scheduler = new PollingTaskScheduler.Builder().build();
        runEvent = new RunEvent<>(this);
        this.setOnClickListener(v -> {
            T data = getCurrentImage();
            if (data != null) {
                this.onClickImage(data);
            }
        });
        isInit = true;
    }

    @Override
    protected void onAttachedToWindow() {
        if (!isInit) {
            this.init();
        }
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        this.stopScroll();
        scheduler = null;
        runEvent.banner = null;
        runEvent = null;
        this.setOnClickListener(null);
        isInit = false;
        super.onDetachedFromWindow();
    }

    /**
     * 获取图片加载地址
     */
    protected abstract void loadImage(ImageView imageView, T itemInfo);

    /**
     * 当点击image时回调
     */
    protected void onClickImage(T itemInfo) {

    }

    /**
     * 获取下次轮播间隔时间,单位：毫秒
     */
    protected long getNextIntervalTime(T itemInfo) {
        return intervalTime;
    }

    /**
     * 设置数据
     */
    public void setData(List<T> data) {
        this.stopScroll();
        this.data.clear();
        if (data != null && !data.isEmpty()) {
            this.data.addAll(data);
        }
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
        if (data.isEmpty()) {
            return;
        }
        if(!isInit){
            this.init();
        }
        if (scheduler != null) {
            scheduler.start(runEvent);
        }
    }

    /**
     * 暂停滚动
     */
    public void pauseScroll() {
        if (scheduler != null) {
            scheduler.stop();
        }
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        if (scheduler != null) {
            scheduler.stop();
        }
        this.imagePosition = -1;
    }

    /**
     * 获取下一个轮播图片列表下标
     */
    private T getNextImage() {
        if (data.isEmpty()) {
            imagePosition = -1;
            return null;
        }

        if (imagePosition + 1 < data.size()) {
            imagePosition++;
        } else {
            imagePosition = 0;
        }
        return data.get(imagePosition);
    }

    /**
     * 获取当前image 数据对象
     */
    private T getCurrentImage() {
        if (data.isEmpty()) {
            return null;
        }
        if (imagePosition <= data.size() - 1) {
            return data.get(Math.max(imagePosition, 0));
        }
        return null;
    }

    /**
     * 执行轮播的事件
     *
     * @param <T>
     */
    private static class RunEvent<T> implements PollingTaskScheduler.PollingRunnable {

        private SimpleBanner<T> banner;

        private RunEvent(SimpleBanner<T> banner) {
            this.banner = banner;
        }

        @Override
        public boolean run() throws Throwable {
            if(banner == null){
                return false;
            }
            UIThread.runOnUiThread(() -> {
                if(banner != null){
                    T data = banner.getNextImage();
                    if (data != null) {
                        banner.loadImage(banner, data);
                    }
                }
            });
            return true;
        }

        @Override
        public void onException(Throwable e) {
            LogUtil.eTag("SimpleBanner", e.toString());
        }

        @Override
        public long getNextIntervalTime(int currentQuantity) {
            return banner.getNextIntervalTime(banner.getCurrentImage());
        }
    }
}
