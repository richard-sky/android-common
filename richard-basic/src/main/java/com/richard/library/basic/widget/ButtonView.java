package com.richard.library.basic.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.richard.library.basic.R;
import com.richard.library.basic.util.DrawableUtil;
import com.richard.library.context.util.DensityUtilKt;


/**
 * <br>Description : 多样式按钮
 * <br>Author : Richard
 * <br>Date : 2017/12/29 11:33
 * <br>Changelog:
 * <br>Version            Date            Author              Detail
 * <br> ----------------------------------------------------------------------
 * <br>1.0        2017/12/29 11:33       Richard      new file.
 */
public class ButtonView extends LinearLayout {

    private ImageView progressView;
    private ProgressDrawable progressDrawable;

    private TextView textView;

    private CharSequence originText;
    private int disEnabledBackColor = Color.parseColor("#C1C1C1");//该控件禁用时的背景颜色

    //控件属性参数
    private float radius;
    private float topLeftRadius;
    private float topRightRadius;
    private float bottomLeftRadius;
    private float bottomRightRadius;
    private int backColor;
    private float borderLineSize;//边框线大小
    private int borderLineColor;//边框线颜色
    private boolean isTextBold;//字体是否加粗

    private int iconSize;
    private int iconMargin;
    private int iconPosition;
    private Integer iconTint;


    public ButtonView(Context context) {
        super(context);
        init(null);
    }

    public ButtonView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.setOrientation(HORIZONTAL);
        this.setGravity(Gravity.CENTER);
        int size = getResources().getDimensionPixelSize(R.dimen.button_loading_size);

        //加载中视图
        progressDrawable = new ProgressDrawable();
        progressView = new ImageView(getContext());
        progressView.setImageDrawable(progressDrawable);
        LayoutParams lp = new LayoutParams(size, size);
        lp.rightMargin = DensityUtilKt.dp2px(5, getContext());
        progressView.setLayoutParams(lp);
        progressView.setVisibility(GONE);

        //textView
        textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        LayoutParams textViewLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLayoutParams.setMargins(0, 0, 0, 0);
        textView.setLayoutParams(textViewLayoutParams);


        this.addView(progressView);
        this.addView(textView);

        boolean isEnabled = true;

        if (attrs != null) {
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ButtonView);

            //按钮小图标
            if (typedArray.hasValue(R.styleable.ButtonView_bv_icon)) {
                int icon = typedArray.getResourceId(R.styleable.ButtonView_bv_icon, -1);
                //icon tint颜色
                if (typedArray.hasValue(R.styleable.ButtonView_bv_icon_tint)) {
                    iconTint = typedArray.getColor(R.styleable.ButtonView_bv_icon_tint, -1);
                }

                //按钮小图标宽和高、间距
                iconSize = typedArray.getDimensionPixelSize(R.styleable.ButtonView_bv_icon_size, 0);
                iconMargin = typedArray.getDimensionPixelSize(R.styleable.ButtonView_bv_icon_margin, 0);
                iconPosition = typedArray.getInt(R.styleable.ButtonView_bv_icon_position, 3);

                this.setIcon(icon);
                this.setIconMargin(iconMargin);
                if (iconSize > 0) {
                    this.setIconSize(iconSize, iconSize);
                }
                if (iconTint != null) {
                    this.setIconTint(iconTint);
                }
            }

            //设置文本内容
            originText = typedArray.getText(R.styleable.ButtonView_android_text);
            this.setText(originText);
            //设置字体大小
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, typedArray.getDimension(R.styleable.ButtonView_android_textSize, getResources().getDimensionPixelSize(R.dimen.textSize_15)));
            //设置字体颜色
            this.setTextColor(typedArray.getColor(R.styleable.ButtonView_android_textColor, getResources().getColor(R.color.text)));
            //获取按钮背景颜色
            backColor = typedArray.getColor(R.styleable.ButtonView_bv_backColor, Color.TRANSPARENT);

            //边框线大小
            borderLineSize = typedArray.getDimensionPixelSize(R.styleable.ButtonView_bv_border_line_size, 0);
            //边框线颜色
            borderLineColor = typedArray.getColor(R.styleable.ButtonView_bv_border_line_color, Color.TRANSPARENT);
            //获取控件边角弧度属性参数
            radius = typedArray.getDimensionPixelSize(R.styleable.ButtonView_bv_radius, 0);
            if (radius == 0) {
                topLeftRadius = typedArray.getDimensionPixelSize(R.styleable.ButtonView_bv_topLeftRadius, 0);
                topRightRadius = typedArray.getDimensionPixelSize(R.styleable.ButtonView_bv_topRightRadius, 0);
                bottomRightRadius = typedArray.getDimensionPixelSize(R.styleable.ButtonView_bv_bottomRightRadius, 0);
                bottomLeftRadius = typedArray.getDimensionPixelSize(R.styleable.ButtonView_bv_bottomLeftRadius, 0);
            }
            //初始化控件的可用性
            isEnabled = typedArray.getBoolean(R.styleable.ButtonView_android_enabled, true);
            //字体是否加粗
            isTextBold = typedArray.getBoolean(R.styleable.ButtonView_bv_text_is_bold, false);

            typedArray.recycle();
            typedArray = null;
        }

        textView.getPaint().setFakeBoldText(isTextBold);

        this.updateBackgroundColor();
        setEnabled(isEnabled);
    }


    /**
     * 设置按钮Selector
     *
     * @param backColor         背景颜色
     * @param borderLineSize    边框线大小
     * @param borderLineColor   边框线颜色
     * @param topLeftRadius     左上角圆角度
     * @param topRightRadius    右上角圆角度
     * @param bottomLeftRadius  左下角圆角度
     * @param bottomRightRadius 右上角圆角度
     */
    public void setSelector(
            int backColor
            , float borderLineSize
            , int borderLineColor
            , float topLeftRadius
            , float topRightRadius
            , float bottomLeftRadius
            , float bottomRightRadius
    ) {
        this.setBackground(DrawableUtil.generatorSelector(
                android.R.attr.state_pressed
                , backColor
                , borderLineSize
                , borderLineColor
                , topLeftRadius
                , topRightRadius
                , bottomLeftRadius
                , bottomRightRadius
        ));
    }


    /**
     * 设置按钮Selector
     *
     * @param backColor       背景颜色
     * @param borderLineSize  边框线大小
     * @param borderLineColor 边框线颜色
     * @param radius          圆角角度
     */
    public void setSelector(int backColor, float borderLineSize, int borderLineColor, float radius) {
        this.setBackground(DrawableUtil.generatorSelector(
                android.R.attr.state_pressed
                , backColor
                , borderLineSize
                , borderLineColor
                , radius
                , radius
                , radius
                , radius
        ));
    }

    /**
     * 设置icon边距
     *
     * @param margin px
     */
    public void setIconMargin(int margin) {
        textView.setCompoundDrawablePadding(margin);
    }

    /**
     * 设置icon 大小
     *
     * @param width  宽 px
     * @param height 高 px
     */
    public void setIconSize(int width, int height) {
        Drawable[] drawables = textView.getCompoundDrawables();
        if (drawables == null || drawables.length == 0) {
            return;
        }

        for (Drawable item : drawables) {
            if (item != null) {
                // 2. 获取Drawable的原始固有尺寸
                int intrinsicWidth = item.getIntrinsicWidth();
                int intrinsicHeight = item.getIntrinsicHeight();

                // 4. 计算缩放后新的边界坐标（核心步骤）
                // 计算中心点坐标（通常可以相对于某个容器或画布）
                int centerX = intrinsicWidth / 2;
                int centerY = intrinsicHeight / 2;
                // 基于中心点，计算新边界的左上角和右下角坐标
                int newLeft = centerX - (width / 2);
                int newTop = centerY - (height / 2);
                int newRight = centerX + (width / 2);
                int newBottom = centerY + (height / 2);

                // 5. 应用新的边界
                item.setBounds(newLeft, newTop, newRight, newBottom);
            }
        }
    }

    /**
     * 设置小图标
     *
     * @param icon 图片资源ID
     */
    public void setIcon(@DrawableRes int icon) {
        Drawable drawable = null;
        if (icon != -1) {
            drawable = ContextCompat.getDrawable(getContext(), icon);
        }
        if (drawable != null) {
            drawable.setBounds(0, 0, iconSize, iconSize);
            textView.setCompoundDrawablePadding(iconMargin);
            if (iconTint != null) {
                drawable.setTint(iconTint);
            }

            switch (iconPosition) {
                case 1://上
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                    break;
                case 2://下
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
                    break;
                case 4://右
                    textView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
                    break;
                case 3://左
                default:
                    textView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            }
        }
    }

    /**
     * 设置icon tint
     */
    public void setIconTint(ColorStateList colorStateList) {
        Drawable[] drawables = textView.getCompoundDrawables();
        if (drawables == null || drawables.length == 0) {
            return;
        }
        for (Drawable item : drawables) {
            if (item != null) {
                item.setTintList(colorStateList);
            }
        }
    }

    /**
     * 设置icon tint
     */
    public void setIconTint(@ColorInt int tintColor) {
        Drawable[] drawables = textView.getCompoundDrawables();
        if (drawables == null || drawables.length == 0) {
            return;
        }
        for (Drawable item : drawables) {
            if (item != null) {
                item.setTint(tintColor);
            }
        }
    }

    /**
     * 获取文本
     *
     * @return
     */
    public CharSequence getText() {
        return textView.getText();
    }

    /**
     * 设置文本
     *
     * @param text 文本内容
     */
    public void setText(CharSequence text) {
        textView.setText(text);
    }

    /**
     * 设置文本
     *
     * @param stringResId 文本内容ID
     */
    public void setText(@StringRes int stringResId) {
        setText(getContext().getString(stringResId));
    }

    /**
     * 设置文本字体大小
     *
     * @param textSize 字体大小
     */
    public void setTextSize(int textSize) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    /**
     * 设置文本颜色
     *
     * @param textColor 字体颜色
     */
    public void setTextColor(int textColor) {
        textView.setTextColor(textColor);
    }

    /**
     * 设置字体是否加粗
     */
    public void setTextBold(boolean isTextBold) {
        this.isTextBold = isTextBold;
        textView.getPaint().setFakeBoldText(isTextBold);
    }

    /**
     * 设置背景颜色
     */
    public void setBackColor(int backColor) {
        this.backColor = backColor;
        this.updateBackgroundColor();
    }

    /**
     * 设置边框线
     *
     * @param borderLineSize  边框线大小
     * @param borderLineColor 边框线颜色
     */
    public void setBorderLine(float borderLineSize, int borderLineColor) {
        this.borderLineSize = borderLineSize;
        this.borderLineColor = borderLineColor;
        this.updateBackgroundColor();
    }

    /**
     * 设置按钮圆角弧度
     *
     * @param radius 圆角弧度
     */
    public void setRadius(float radius) {
        this.radius = radius;
        this.updateBackgroundColor();
    }

    /**
     * 设置按钮圆角弧度
     *
     * @param topLeftRadius     左上角圆角度
     * @param topRightRadius    右上角圆角度
     * @param bottomLeftRadius  左下角圆角度
     * @param bottomRightRadius 右上角圆角度
     */
    public void setRadius(float topLeftRadius
            , float topRightRadius
            , float bottomLeftRadius
            , float bottomRightRadius) {
        this.topLeftRadius = topLeftRadius;
        this.topRightRadius = topRightRadius;
        this.bottomLeftRadius = bottomLeftRadius;
        this.bottomRightRadius = bottomRightRadius;
        this.updateBackgroundColor();
    }

    /**
     * 显示加载视图
     */
    public void showLoading(String text) {
        this.setText(text);
        progressView.setVisibility(VISIBLE);
        progressDrawable.start();
        this.setEnabled(false);
    }

    /**
     * 隐藏加载视图
     */
    public void hideLoading() {
        this.setText(originText);
        progressView.setVisibility(GONE);
        progressDrawable.stop();
        this.setEnabled(true);
    }

    /**
     * 设置禁用时的背景颜色
     */
    public void setDisEnabledBackColor(int disEnabledBackColor) {
        this.disEnabledBackColor = disEnabledBackColor;
        this.setEnabled(false);
        this.updateBackgroundColor();
    }

    /**
     * 更新背景颜色
     */
    private void updateBackgroundColor() {
        if (backColor == Color.TRANSPARENT && borderLineColor == Color.TRANSPARENT && borderLineSize <= 0) {
            if (getBackground() == null) {
                this.setBackgroundColor(Color.TRANSPARENT);
            }
            return;
        }
        if (radius == 0) {
            if (isEnabled()) {
                this.setSelector(
                        backColor
                        , borderLineSize
                        , borderLineColor
                        , topLeftRadius
                        , topRightRadius
                        , bottomLeftRadius
                        , bottomRightRadius
                );
            } else {
                this.setBackground(DrawableUtil.generatorGradientDrawable(
                        Color.parseColor("#C1C1C1")
                        , new float[]{
                                topLeftRadius
                                , topLeftRadius
                                , topRightRadius
                                , topRightRadius
                                , bottomRightRadius
                                , bottomRightRadius
                                , bottomLeftRadius
                                , bottomLeftRadius
                        }
                        , borderLineSize
                        , borderLineColor
                ));
            }
        } else {
            if (isEnabled()) {
                this.setSelector(
                        backColor
                        , borderLineSize
                        , borderLineColor
                        , radius
                );
            } else {
                this.setBackground(DrawableUtil.generatorGradientDrawable(
                        disEnabledBackColor
                        , new float[]{
                                radius
                                , radius
                                , radius
                                , radius
                                , radius
                                , radius
                                , radius
                                , radius
                        }, borderLineSize
                        , borderLineColor
                ));
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        progressView.setVisibility(GONE);
        progressDrawable.stop();
        super.onDetachedFromWindow();
    }

    /**
     * 设置文本字体样式
     *
     * @param tf
     */
    public void setTypeface(Typeface tf) {
        textView.setTypeface(tf);
    }
}
