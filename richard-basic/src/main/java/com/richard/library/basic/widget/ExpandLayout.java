package com.richard.library.basic.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.richard.library.basic.R;
import com.richard.library.basic.util.MeasuredUtil;
import com.richard.library.context.AppContext;

/**
 * <pre>
 * Description : 支持折叠动画的布局容器
 * Author : admin-richard
 * Date : 2017/10/13 18:57
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2017/10/13 18:57     admin-richard         new file.
 * </pre>
 */
public class ExpandLayout extends LinearLayout {

    private int viewHeight;//视图总高度
    private boolean isOpen = true;//默认为展开状态
    private boolean isExpandAnimation;//是否启用折叠动画
    private boolean isFirstMeasure = true;
    private View bgView;//列表展开时的蒙版view
    private OnExpandListener mOnExpandListener;


    public ExpandLayout(Context context) {
        super(context);
        init(null);
    }


    public ExpandLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ExpandLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ExpandLayout);
            isOpen = typedArray.getBoolean(R.styleable.ExpandLayout_el_open, true);
            typedArray.recycle();
        }

        this.post(() -> {
            if (viewHeight <= 0) {
                viewHeight = Math.min(MeasuredUtil.getMeasuredHeight(this), AppContext.getScreenHeight());
            }
            if (isFirstMeasure && !isOpen) {
                this.setContentViewHeight(0);
                isFirstMeasure = false;
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isOpen && isInEditMode()) {
            return;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 初始化展开或者折叠的状态
     *
     * @param isOpen
     */
    public void initExpandLayoutStatus(boolean isOpen) {
        this.isOpen = isOpen;
    }

    private void setContentViewHeight(int height) {
        ViewGroup.LayoutParams viewGroup = getLayoutParams();
        viewGroup.height = height;
        requestLayout();
    }


    /**
     * 展开
     */
    public void open() {
        if (isOpen) {
            return;
        }
        toggle(isExpandAnimation);
    }

    /**
     * 关闭
     */
    public void close() {
        if (!isOpen) {
            return;
        }
        toggle(isExpandAnimation);
    }

    /**
     * 展开
     */
    public void open(boolean isExpandAnimation) {
        if (isOpen) {
            return;
        }
        toggle(isExpandAnimation);
    }

    /**
     * 关闭
     */
    public void close(boolean isExpandAnimation) {
        if (!isOpen) {
            return;
        }
        toggle(isExpandAnimation);
    }

    public boolean toggle() {
        return this.toggle(isExpandAnimation);
    }

    public boolean toggle(boolean isExpandAnimation) {
        ValueAnimator valueAnimator = isOpen ? ValueAnimator.ofInt(100, 0) : ValueAnimator.ofInt(0, 100);
        valueAnimator.setDuration(isExpandAnimation ? 200 : 0);
        valueAnimator.setInterpolator(new DecelerateInterpolator(1.3f));
        valueAnimator.addUpdateListener(animation -> {
            int value = (int) animation.getAnimatedValue();
            setContentViewHeight((viewHeight / 100) * value);
        });
        valueAnimator.start();

        AlphaAnimation animation = new AlphaAnimation(isOpen ? 1f : 0f, isOpen ? 0f : 1f);
        animation.setDuration(150);
        this.startAnimation(animation);

        if (bgView != null) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(isOpen ? 1f : 0f, isOpen ? 0f : 1f);
            alphaAnimation.setDuration(isExpandAnimation ? 200 : 0);
            alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    bgView.setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    bgView.setVisibility(isOpen ? VISIBLE : GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            bgView.startAnimation(alphaAnimation);
        }


        isOpen = !isOpen;

        if (mOnExpandListener != null) {
            mOnExpandListener.expand(isOpen);
        }

        return isOpen;
    }

    /**
     * 获取展开状态
     *
     * @return isOpen
     */
    public boolean isOpen() {
        return isOpen;
    }

    public void setExpandAnimation(boolean expandAnimation) {
        isExpandAnimation = expandAnimation;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setBackView(View view_back) {
        this.bgView = view_back;
        if (view_back != null) {
            view_back.setOnTouchListener((v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    close();
                }
                return true;
            });
        }
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    public void setOnExpandListener(OnExpandListener onExpandListener) {
        mOnExpandListener = onExpandListener;
    }

    public interface OnExpandListener {
        void expand(boolean isExpand);
    }
}
