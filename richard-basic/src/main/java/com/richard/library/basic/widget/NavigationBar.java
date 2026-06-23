package com.richard.library.basic.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.richard.library.basic.R;
import com.richard.library.basic.util.DrawableUtil;

/**
 * <pre>
 * Description : 导航条
 * Author : admin-richard
 * Date : 2016/10/25 16:23
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2016/10/25 16:23     admin-richard         new file.
 * </pre>
 */
public class NavigationBar extends LinearLayout {

    private ViewGroup navRoot;
    private ImageView ivLeft;
    private TextView tvLeft;
    private TextView tvTitle;
    private TextView tvRight;
    private ImageView ivRight;
    private FrameLayout navContent;
    private View lineBottom;

    private int radius = 0;
    private int topLeftRadius = 0;
    private int topRightRadius = 0;
    private int bottomLeftRadius = 0;
    private int bottomRightRadius = 0;


    public NavigationBar(Context context) {
        super(context);
        this.init(context, null);
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public NavigationBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        super.setOrientation(VERTICAL);
        View view = View.inflate(context, R.layout.navigation_bar, this);

        navRoot = view.findViewById(R.id.content_nav);
        tvLeft = view.findViewById(R.id.navigation_bar_tv_left);
        ivLeft = view.findViewById(R.id.navigation_bar_iv_left);
        tvTitle = view.findViewById(R.id.navigation_bar_tv_title);
        tvRight = view.findViewById(R.id.navigation_bar_tv_right);
        ivRight = view.findViewById(R.id.navigation_bar_iv_right);
        navContent = view.findViewById(R.id.navigation_content);
        lineBottom = view.findViewById(R.id.navigation_bar_bottom_line);

        tvTitle.getPaint().setFakeBoldText(
                getResources().getBoolean(R.bool.navigation_bar_title_bold));

        boolean isColorValue = false;
        int backColor = Color.TRANSPARENT;
        int navTextColor = getResources().getColor(R.color.navigation_text_color);
        int navBottomLineColor = getResources().getColor(R.color.navigation_bottom_line_color);
        int navBottomLineSize = getResources().getDimensionPixelSize(R.dimen.navigation_bottom_line_size);
        boolean navBottomLineShow = getResources().getInteger(R.integer.navigation_bar_bottom_line_show) == View.VISIBLE;
        int titleTextSize = getResources().getDimensionPixelSize(R.dimen.navigation_bar_title_text_size);
        boolean titleTextBold = getResources().getBoolean(R.bool.navigation_bar_title_bold);
        String titleText = "";
        int leftImg = -1;
        String leftText = "";
        int rightImg = -1;
        String rightText = "";
        int centerLayout = -1;
        int leftImageTint = getResources().getColor(R.color.navigation_left_image_tint);
        int rightImageTint = getResources().getColor(R.color.navigation_right_image_tint);

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.NavigationBar);

            try {
                backColor = typedArray.getColor(R.styleable.NavigationBar_android_background, backColor);
                isColorValue = true;
            } catch (Throwable ignored) {
            }

            navTextColor = typedArray.getColor(R.styleable.NavigationBar_nav_text_color, navTextColor);
            navBottomLineColor = typedArray.getColor(R.styleable.NavigationBar_nav_bottom_line_color, navBottomLineColor);
            navBottomLineSize = typedArray.getDimensionPixelSize(R.styleable.NavigationBar_nav_bottom_line_size, navBottomLineSize);
            navBottomLineShow = typedArray.getBoolean(R.styleable.NavigationBar_nav_bottom_line_show, navBottomLineShow);
            titleTextSize = typedArray.getDimensionPixelSize(R.styleable.NavigationBar_nav_title_text_size, titleTextSize);
            titleTextBold = typedArray.getBoolean(R.styleable.NavigationBar_nav_title_text_bold, titleTextBold);
            titleText = typedArray.getString(R.styleable.NavigationBar_nav_title_text);
            leftImg = typedArray.getResourceId(R.styleable.NavigationBar_nav_left_img, leftImg);
            leftText = typedArray.getString(R.styleable.NavigationBar_nav_left_text);
            rightImg = typedArray.getResourceId(R.styleable.NavigationBar_nav_right_img, rightImg);
            rightText = typedArray.getString(R.styleable.NavigationBar_nav_right_text);
            centerLayout = typedArray.getResourceId(R.styleable.NavigationBar_nav_center_layout, centerLayout);

            radius = typedArray.getDimensionPixelSize(R.styleable.NavigationBar_nav_radius, radius);
            topLeftRadius = typedArray.getDimensionPixelSize(R.styleable.NavigationBar_nav_topLeftRadius, topLeftRadius);
            topRightRadius = typedArray.getDimensionPixelSize(R.styleable.NavigationBar_nav_topRightRadius, topRightRadius);
            bottomLeftRadius = typedArray.getDimensionPixelSize(R.styleable.NavigationBar_nav_bottomLeftRadius, bottomLeftRadius);
            bottomRightRadius = typedArray.getDimensionPixelSize(R.styleable.NavigationBar_nav_bottomRightRadius, bottomRightRadius);

            typedArray.recycle();
        }

        //文本颜色
        this.setAllTextColor(navTextColor);

        //设置底部线条
        this.lineBottom.setBackgroundColor(navBottomLineColor);
        this.lineBottom.setVisibility(navBottomLineShow ? VISIBLE : GONE);
        ViewGroup.LayoutParams lineLP = this.lineBottom.getLayoutParams();
        if (lineLP == null) {
            lineLP = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , navBottomLineSize
            );
        } else {
            lineLP.height = navBottomLineSize;
        }
        this.lineBottom.setLayoutParams(lineLP);

        //title
        this.tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        this.tvTitle.getPaint().setFakeBoldText(titleTextBold);
        this.tvTitle.setText(titleText);
        if (!TextUtils.isEmpty(titleText)) {
            this.setTitleTextViewShow(true);
        }

        //左侧
        if (leftImg != -1) {
            this.ivLeft.setImageResource(leftImg);
            this.setLeftImageViewShow(true);
        }
        this.tvLeft.setText(leftText);
        if (!TextUtils.isEmpty(leftText)) {
            this.setLeftTextViewShow(true);
        }
        if (leftImageTint != Color.TRANSPARENT) {
            this.ivLeft.setImageTintList(ColorStateList.valueOf(leftImageTint));
        }

        //右侧
        if (rightImg != -1) {
            this.ivRight.setImageResource(rightImg);
            this.setRightImageViewShow(true);
        }
        this.tvRight.setText(rightText);
        if (!TextUtils.isEmpty(rightText)) {
            this.setRightTextViewShow(true);
        }
        if (rightImageTint != Color.TRANSPARENT) {
            this.ivRight.setImageTintList(ColorStateList.valueOf(rightImageTint));
        }

        //中部layout
        if (centerLayout != -1) {
            LayoutInflater.from(context).inflate(centerLayout, navContent);
            this.setContentViewShow(true);
        }

        //设置背景颜色
        int useBackColor = getResources().getColor(R.color.navigation_bar_backcolor);
        if (isColorValue) {
            if (backColor != Color.TRANSPARENT) {
                useBackColor = backColor;
            }
        }
        this.setXBackgroundColor(useBackColor);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        ViewGroup.LayoutParams lp = navRoot.getLayoutParams();
        lp.width = params.width;
        lp.height = params.height;
        navRoot.setLayoutParams(lp);

        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        super.setLayoutParams(params);
    }

    /**
     * 设置四角圆角弧度
     *
     * @param radius 单位 px
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * 设置四角圆角弧度
     *
     * @param topLeftRadius     左上角弧度px
     * @param topRightRadius    右上角弧度px
     * @param bottomLeftRadius  左下角弧度px
     * @param bottomRightRadius 右下角弧度px
     */
    public void setRadius(int topLeftRadius, int topRightRadius, int bottomLeftRadius, int bottomRightRadius) {
        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius = bottomRightRadius;
    }

    /**
     * 通知更新圆角弧度
     *
     * @param backColor 背景颜色
     */
    public void notifyUpdateRadius(@ColorInt int backColor) {
        this.setXBackgroundColor(backColor);
    }

    /**
     * 扩展父类的背景颜色设置
     */
    public void setXBackgroundColor(@ColorInt int color) {
        //圆角弧度
        if (radius > 0) {
            setBackground(DrawableUtil.generatorGradientDrawable(color, radius));
        } else if (topLeftRadius > 0
                || topRightRadius > 0
                || bottomLeftRadius > 0
                || bottomRightRadius > 0) {
            setBackground(DrawableUtil.generatorGradientDrawable(
                    color
                    , topLeftRadius
                    , topRightRadius
                    , bottomLeftRadius
                    , bottomRightRadius
            ));
        } else {
            setBackgroundColor(color);
        }
    }

    //**************************设置单击事件***************************
    public void setLeftImageViewClickListener(OnClickListener listener) {
        ivLeft.setOnClickListener(listener);
    }

    public void setLeftTextViewClickListener(OnClickListener listener) {
        tvLeft.setOnClickListener(listener);
    }

    public void setRightImageViewClickListener(OnClickListener listener) {
        ivRight.setOnClickListener(listener);
    }

    public void setRightTextViewClickListener(OnClickListener listener) {
        tvRight.setOnClickListener(listener);
    }


    //**************************设置控件显示与隐藏***************************
    public void setLeftImageViewShow(boolean isShow) {
        ivLeft.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setLeftTextViewShow(boolean isShow) {
        tvLeft.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setRightImageViewShow(boolean isShow) {
        ivRight.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setTitleTextViewShow(boolean isShow) {
        tvTitle.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setRightTextViewShow(boolean isShow) {
        tvRight.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setContentViewShow(boolean isShow) {
        navContent.setVisibility(isShow ? VISIBLE : GONE);
    }

    public void setBottomLineViewShow(boolean isShow) {
        lineBottom.setVisibility(isShow ? VISIBLE : GONE);
    }

    //**************************设置图片控件图片***************************
    public void setLeftImageView(int imageRes) {
        ivLeft.setImageResource(imageRes);
    }

    public void setRightImageView(int imageRes) {
        ivRight.setImageResource(imageRes);
    }


    //**************************设置TextView文本***************************
    public void setTitle(String title) {
        tvTitle.setText(title);
    }


    public void setLeftText(String text) {
        tvLeft.setText(text);
    }

    public void setRightText(String text) {
        tvRight.setText(text);
    }


    //**************************设置所有TextView字体颜色***************************
    public void setAllTextColor(@ColorInt int color) {
        tvLeft.setTextColor(color);
        tvTitle.setTextColor(color);
        tvRight.setTextColor(color);
    }

    //**************************获取navigationBar控件****************************
    public TextView getLeftTextView() {
        return tvLeft;
    }

    public ImageView getLeftImageView() {
        return ivLeft;
    }

    public TextView getTitleTextView() {
        return tvTitle;
    }

    public ImageView getRightImageView() {
        return ivRight;
    }

    public TextView getRightTextView() {
        return tvRight;
    }

    //***************************方法体*****************************************
    public void setContentView(View contentView) {
        this.navContent.removeAllViews();
        this.navContent.addView(contentView);
    }

    public void addContentView(View childView) {
        this.navContent.addView(childView);
    }
}
