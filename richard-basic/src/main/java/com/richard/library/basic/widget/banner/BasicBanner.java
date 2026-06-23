package com.richard.library.basic.widget.banner;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.richard.library.context.task.PollingTaskScheduler;
import com.richard.library.context.util.DensityUtilKt;
import com.richard.library.context.util.UIThread;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * banner 基类
 *
 * @param <E> 数据源泛型
 * @param <T> banner实现泛型
 */
public abstract class BasicBanner<E, T extends BasicBanner<E, T>> extends RelativeLayout {

    private PollingTaskScheduler scheduler;

    /**
     * ViewPager
     */
    protected ViewPager mViewPager;
    /**
     * 数据源
     */
    protected List<E> data = new ArrayList<>();
    /**
     * 当前position
     */
    protected int currentPosition;
    /**
     * 上一个position
     */
    protected int lastPosition;
    /**
     * 多久后开始滚动(毫秒)
     */
    private long delay = 3000;
    /**
     * 滚动间隔
     */
    private long period = 2;
    /**
     * 是否自动滚动
     */
    private boolean isAutoScrollEnable = true;
    /**
     * 是否支持循环滑动
     */
    private boolean isSupportLooper = true;
    /**
     * 是否正在自动滚动中
     */
    private boolean isAutoScrolling;
    /**
     * 滚动速度
     */
    private int scrollSpeed = 450;
    /**
     * 是否显示指示器
     */
    private boolean isShowIndicator;

    /// 是否允许用户手动触摸操作
    private boolean isAllowUserTouch = true;

    /**
     * 轮询执行事件
     */
    private PollingRunEvent<E, T> pollingRunnable;

    /**
     * ViewPager中的View是否全部缓存（true：全部缓存、false：仅缓存当前显示页的左右两边页）
     */
    private boolean isCacheAll = false;

    /**
     * 显示器的的直接容器
     */
    private LinearLayout mLlIndicatorContainer;
    private OnItemClickListener<E> mOnItemClickListener;
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private OnNextIntervalTime<E> onNextIntervalTime;

    private InnerBannerAdapter<E, T> mInnerAdapter;


    public BasicBanner(Context context) {
        this(context, null, 0);
    }

    public BasicBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasicBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        scheduler = new PollingTaskScheduler.Builder().build();
        pollingRunnable = new PollingRunEvent<>(this);
    }

    /**
     * 创建ViewPager的Item布局
     */
    public abstract View onCreateItemView(E itemInfo, int position);

    /**
     * 创建显示器
     */
    public View onCreateIndicator() {
        return null;
    }

    /**
     * 设置当前显示器的状态,选中或者未选中
     */
    public void setCurrentIndicator(int position) {
    }

    /**
     * 获取数据源
     */
    public List<E> getData() {
        return data;
    }

    /**
     * 设置数据源并立即启动
     */
    public void setDataAndStartScroll(List<E> list) {
        this.setData(list);
        this.startScroll();
    }

    /**
     * 设置数据源
     */
    public T setData(List<E> list) {
        this.pauseScroll();
        this.data.clear();
        this.data.addAll(list);
        return (T) this;
    }

    /**
     * 设置是否允许用户触摸banner操作
     */
    public T setAllowUserTouch(boolean allowUserTouch) {
        this.isAllowUserTouch = allowUserTouch;
        return (T) this;
    }

    /**
     * 滚动延时,默认3000毫秒,单位毫秒
     */
    public T setDelay(long delay) {
        this.delay = delay;
        return (T) this;
    }

    /**
     * 滚动间隔,默认2秒
     */
    public T setPeriod(long period) {
        this.period = period;
        return (T) this;
    }

    /**
     * 设置ViewPager中的View是否全部缓存（true：全部缓存、false：仅缓存当前显示页的左右两边页）
     */
    public T setCacheAll(boolean cacheAll) {
        this.isCacheAll = cacheAll;
        return (T) this;
    }

    /**
     * 设置滚动速度
     */
    public T setScrollSpeed(int scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
        return (T) this;
    }

    /**
     * 设置是否支持自动滚动,默认true.仅对LoopViewPager有效
     */
    public T setAutoScrollEnable(boolean isAutoScrollEnable) {
        this.isAutoScrollEnable = isAutoScrollEnable;
        return (T) this;
    }

    /**
     * 设置是否支持循环滚动
     */
    public T setSupportLooper(boolean supportLooper) {
        isSupportLooper = supportLooper;
        return (T) this;
    }

    /**
     * 设置是否显示指示器
     */
    public T setShowIndicator(boolean showIndicator) {
        isShowIndicator = showIndicator;
        return (T) this;
    }

    /**
     * 设置翻页变化事件
     */
    public T setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
        return (T) this;
    }

    /**
     * 设置banner点击事件
     */
    public T setOnItemClickListener(OnItemClickListener<E> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return (T) this;
    }

    /**
     * 滚动到下一个item
     */
    private void scrollToNextItem(int position) {
        position++;
        mViewPager.setCurrentItem(position);
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
        if (!data.isEmpty() && currentPosition > data.size() - 1) {
            currentPosition = 0;
        }

        setViewPager();

        resumeScroll();

        ViewPager viewPager = getViewPager();
        if (viewPager != null && viewPager.getAdapter() != null) {
            viewPager.getAdapter().notifyDataSetChanged();
            if (isCacheAll) {
                mViewPager.setOffscreenPageLimit(data.size() + 1);
            } else {
                mViewPager.setOffscreenPageLimit(1);
            }
        }
    }

    /**
     * 继续滚动(for LoopViewPager)
     */
    public void resumeScroll() {
        if (!isValid()) {
            return;
        }

        if (isAutoScrolling) {
            return;
        }
        if (isLoopViewPager() && isAutoScrollEnable) {
            pauseScroll();
            scheduler.start(pollingRunnable, delay);
            isAutoScrolling = true;
        } else {
            isAutoScrolling = false;
        }
    }

    /**
     * 停止滚动(for LoopViewPager)
     */
    public void pauseScroll() {
        scheduler.stop();
        isAutoScrolling = false;
    }

    /**
     * 设置viewpager
     */
    private void setViewPager() {
        if (mViewPager == null || isSupportLooper && !isLoopViewPager()) {
            if (mViewPager != null) {
                this.removeView(mViewPager);
            }
            mViewPager = isSupportLooper ? new LoopViewPager(getContext()) : new ViewPager(getContext());
            this.addView(mViewPager);

            if (isLoopViewPager()) {
                setScrollSpeed();
            }
        }

        if (mInternalPageListener != null) {
            mViewPager.removeOnPageChangeListener(mInternalPageListener);
            mViewPager.addOnPageChangeListener(mInternalPageListener);
        }

        if (isShowIndicator) {
            if (mLlIndicatorContainer == null) {
                //init indicator ViewGroup
                mLlIndicatorContainer = new LinearLayout(getContext());
                mLlIndicatorContainer.setGravity(Gravity.CENTER);
                mLlIndicatorContainer.setVisibility(isShowIndicator ? VISIBLE : INVISIBLE);
                mLlIndicatorContainer.setClipChildren(false);
                mLlIndicatorContainer.setClipToPadding(false);
                LayoutParams rl = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
                rl.setMargins(mLlIndicatorContainer.getLeft(), mLlIndicatorContainer.getTop(), mLlIndicatorContainer.getRight(), DensityUtilKt.dp2px(15, getContext()));
                mLlIndicatorContainer.setLayoutParams(rl);
                this.addView(mLlIndicatorContainer);
            }
            //create indicator
            View indicatorViews = onCreateIndicator();
            if (indicatorViews != null) {
                mLlIndicatorContainer.removeAllViews();
                mLlIndicatorContainer.addView(indicatorViews);
            }
        }

        if (mInnerAdapter == null) {
            mInnerAdapter = new InnerBannerAdapter<E, T>(this);
            mViewPager.setAdapter(mInnerAdapter);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        this.pauseScroll();
        if (mViewPager != null && mInternalPageListener != null) {
            mViewPager.removeOnPageChangeListener(mInternalPageListener);
        }
        super.onDetachedFromWindow();
    }

    /**
     * 获取ViewPager对象
     */
    public ViewPager getViewPager() {
        return mViewPager;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isAllowUserTouch) {
            return true;
        }

        //解决手势冲突
        if (ev.getAction() != MotionEvent.ACTION_UP) {
            requestDisallowInterceptTouchEvent(true);
        }

        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                pauseScroll();
                break;
            case MotionEvent.ACTION_UP:
                resumeScroll();
                break;
            case MotionEvent.ACTION_CANCEL:
                resumeScroll();
                break;

        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 轮询滚动事件触发处理
     */
    private static class PollingRunEvent<E, T extends BasicBanner<E, T>> implements PollingTaskScheduler.PollingRunnable {

        private BasicBanner<E, T> banner;

        private PollingRunEvent(BasicBanner<E, T> banner) {
            this.banner = banner;
        }

        @Override
        public boolean run() throws Throwable {
            UIThread.runOnUiThread(() -> {
                banner.scrollToNextItem(banner.currentPosition);
            });
            return true;
        }

        @Override
        public void onException(Throwable e) {

        }

        @Override
        public long getNextIntervalTime(int currentQuantity) {
            if (banner.onNextIntervalTime == null) {
                return banner.period * 1000;
            }
            return banner.onNextIntervalTime.getNextIntervalTime(
                    banner.data.get(banner.currentPosition), banner.currentPosition) * 1000;
        }
    }

    private ViewPager.OnPageChangeListener mInternalPageListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position % data.size();
            lastPosition = currentPosition;
            //设置显示当前的指示器
            setCurrentIndicator(currentPosition);
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (mOnPageChangeListener != null) {
                mOnPageChangeListener.onPageScrollStateChanged(state);
            }
        }
    };

    /**
     * 内部实际banner adapter
     * 注意：内部重新的方法，必须在LoopPagerAdapterWrapper中重写方法调用InnerBannerAdapter 的方法才有效
     */
    private static class InnerBannerAdapter<E, T extends BasicBanner<E, T>> extends PagerAdapter {

        private BasicBanner<E, T> banner;

        private InnerBannerAdapter(BasicBanner<E, T> banner) {
            this.banner = banner;
        }

        @Override
        public int getCount() {
            return banner.data.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View inflate = banner.onCreateItemView(banner.data.get(position), position);
            inflate.setOnClickListener(v -> {
                if (banner.mOnItemClickListener != null) {
                    banner.mOnItemClickListener.onItemClick(banner.data.get(position), position);
                }
            });
            container.addView(inflate);

            return inflate;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

    /**
     * 是否为循环滑动的ViewPager
     */
    protected boolean isLoopViewPager() {
        return mViewPager instanceof LoopViewPager;
    }

    /**
     * banner滚动是否有效
     */
    protected boolean isValid() {
        if (mViewPager == null) {
            return false;
        }

        return !(data == null || data.size() == 0);
    }

    /**
     * 设置滚动速率
     */
    private void setScrollSpeed() {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
            FixedSpeedScroller myScroller = new FixedSpeedScroller(getContext(), interpolator, scrollSpeed);
            mScroller.set(mViewPager, myScroller);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected int dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5F);
    }

    public void setOnNextIntervalTime(OnNextIntervalTime<E> onNextIntervalTime) {
        this.onNextIntervalTime = onNextIntervalTime;
    }

    public interface OnItemClickListener<E> {
        void onItemClick(E itemInfo, int position);
    }

    public interface OnNextIntervalTime<E> {
        /**
         * 获取下次间隔时长
         *
         * @return 单位秒
         */
        long getNextIntervalTime(E itemInfo, int position);
    }

    /**
     * 控制速率
     */
    private static class FixedSpeedScroller extends Scroller {
        private int mScrollSpeed = 450;

        public FixedSpeedScroller(Context context) {
            super(context);
        }

        public FixedSpeedScroller(Context context, Interpolator interpolator, int scrollSpeed) {
            super(context, interpolator);
            this.mScrollSpeed = scrollSpeed;
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, this.mScrollSpeed);
        }
    }


    /**
     * 以下是为了解决手势冲突
     */
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() != MotionEvent.ACTION_UP) {
            requestDisallowInterceptTouchEvent(true);
        }
        return super.onTouchEvent(e);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() != MotionEvent.ACTION_UP) {
            requestDisallowInterceptTouchEvent(true);
        }
        return super.onInterceptTouchEvent(e);
    }
}
