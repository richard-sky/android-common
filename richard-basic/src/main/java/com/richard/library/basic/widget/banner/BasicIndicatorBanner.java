package com.richard.library.basic.widget.banner;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.richard.library.basic.R;
import com.richard.library.context.util.DensityUtilKt;

import java.util.ArrayList;

/**
 * banner 带指示器的banner基类
 *
 * @param <E> 数据源泛型
 * @param <T> banner实现泛型
 */
public abstract class BasicIndicatorBanner<E, T extends BasicIndicatorBanner<E, T>> extends BasicBanner<E, T> {
    public static final int STYLE_DRAWABLE_RESOURCE = 0;
    public static final int STYLE_CORNER_RECTANGLE = 1;

    private ArrayList<ImageView> mIndicatorViews = new ArrayList<>();
    private int mIndicatorStyle = STYLE_CORNER_RECTANGLE;
    private int mIndicatorWidth;
    private int mIndicatorHeight;
    private int mIndicatorGap;
    private int mIndicatorCornerRadius;

    private Drawable mSelectDrawable;
    private Drawable mUnSelectDrawable;
    private int mSelectColor = Color.YELLOW;
    private int mUnselectColor = Color.WHITE;

    private LinearLayout mLlIndicators;

    public BasicIndicatorBanner(Context context) {
        this(context, null, 0);
    }

    public BasicIndicatorBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BasicIndicatorBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mSelectColor = ContextCompat.getColor(context, R.color.main);

        mIndicatorWidth = DensityUtilKt.dp2px(5, context);
        mIndicatorHeight = DensityUtilKt.dp2px(5, context);
        mIndicatorGap = DensityUtilKt.dp2px(6, context);
        mIndicatorCornerRadius = DensityUtilKt.dp2px(6, context);

        setShowIndicator(true);
        //create indicator container
        mLlIndicators = new LinearLayout(context);
        mLlIndicators.setGravity(Gravity.CENTER);
    }

    @Override
    public View onCreateIndicator() {
        if (mIndicatorStyle == STYLE_CORNER_RECTANGLE) {//rectangle
            this.mUnSelectDrawable = getDrawable(mUnselectColor, mIndicatorCornerRadius);
            this.mSelectDrawable = getDrawable(mSelectColor, mIndicatorCornerRadius);
        }

        int size = data.size();
        mIndicatorViews.clear();

        mLlIndicators.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView iv = new ImageView(getContext());
            iv.setImageDrawable(i == currentPosition ? mSelectDrawable : mUnSelectDrawable);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mIndicatorWidth, mIndicatorHeight);
            lp.leftMargin = i == 0 ? 0 : mIndicatorGap;
            mLlIndicators.addView(iv, lp);
            mIndicatorViews.add(iv);
        }

        setCurrentIndicator(currentPosition);

        return mLlIndicators;
    }

    @Override
    public void setCurrentIndicator(int position) {
        for (int i = 0; i < mIndicatorViews.size(); i++) {
            mIndicatorViews.get(i).setImageDrawable(i == position ? mSelectDrawable : mUnSelectDrawable);
        }
    }

    /**
     * 设置显示样式,STYLE_DRAWABLE_RESOURCE or STYLE_CORNER_RECTANGLE
     */
    public T setIndicatorStyle(int indicatorStyle) {
        this.mIndicatorStyle = indicatorStyle;
        return (T) this;
    }

    /**
     * 设置显示宽度,单位dp,默认6dp
     */
    public T setIndicatorWidth(float indicatorWidth) {
        this.mIndicatorWidth = dp2px(indicatorWidth);
        return (T) this;
    }

    /**
     * 设置显示器高度,单位dp,默认6dp
     */
    public T setIndicatorHeight(float indicatorHeight) {
        this.mIndicatorHeight = dp2px(indicatorHeight);
        return (T) this;
    }

    /**
     * 设置两个显示器间距,单位dp,默认6dp
     */
    public T setIndicatorGap(float indicatorGap) {
        this.mIndicatorGap = dp2px(indicatorGap);
        return (T) this;
    }

    /**
     * 设置显示器选中颜色(for STYLE_CORNER_RECTANGLE),默认"#ffffff"
     */
    public T setIndicatorSelectColor(int selectColor) {
        this.mSelectColor = selectColor;
        return (T) this;
    }

    /**
     * 设置显示器未选中颜色(for STYLE_CORNER_RECTANGLE),默认"#88ffffff"
     */
    public T setIndicatorUnselectColor(int unselectColor) {
        this.mUnselectColor = unselectColor;
        return (T) this;
    }

    /**
     * 设置显示器圆角弧度(for STYLE_CORNER_RECTANGLE),单位dp,默认3dp
     */
    public T setIndicatorCornerRadius(float indicatorCornerRadius) {
        this.mIndicatorCornerRadius = dp2px(indicatorCornerRadius);
        return (T) this;
    }

    /**
     * 设置显示器选中以及未选中资源(for STYLE_DRAWABLE_RESOURCE)
     */
    public T setIndicatorSelectorRes(int unselectRes, int selectRes) {
        try {
            if (mIndicatorStyle == STYLE_DRAWABLE_RESOURCE) {
                if (selectRes != 0) {
                    this.mSelectDrawable = ContextCompat.getDrawable(getContext(), selectRes);
                }
                if (unselectRes != 0) {
                    this.mUnSelectDrawable = ContextCompat.getDrawable(getContext(), unselectRes);
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        return (T) this;
    }


    private GradientDrawable getDrawable(int color, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(radius);
        drawable.setColor(color);

        return drawable;
    }
}