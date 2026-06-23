package com.richard.library.basic.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.richard.library.basic.R;
import com.richard.library.context.util.DensityUtilKt;

/**
 * <pre>
 * Description : 空数据占位View
 * Author : admin-richard
 * Date : 2020-06-17 18:57
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2020-06-17 18:57     admin-richard         new file.
 * </pre>
 */
public class EmptyView extends LinearLayout {

    private TextView textView;
    private ImageView imageView;

    public EmptyView(Context context) {
        super(context);
        this.init(null);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(attrs);
    }


    /**
     * 初始化
     */
    private void init(AttributeSet attrs) {
        super.setOrientation(VERTICAL);
        super.setGravity(Gravity.CENTER);

        if (attrs != null) {
            //---获取自定义属性
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EmptyView);
            int iconId = typedArray.getResourceId(R.styleable.EmptyView_ev_icon, R.mipmap.default_empty_pic);
            int iconWidth = typedArray.getDimensionPixelSize(
                    R.styleable.EmptyView_ev_icon_width
                    , DensityUtilKt.dp2px(140, getContext())
            );
            int textMarginTop = typedArray.getDimensionPixelSize(
                    R.styleable.EmptyView_ev_text_margin_top
                    , DensityUtilKt.dp2px(8, getContext())
            );
            boolean isTextBold = typedArray.getBoolean(R.styleable.EmptyView_ev_text_bold, false);

            //---获取系统属性
            String text = typedArray.getString(R.styleable.EmptyView_android_text);
            int textColor = typedArray.getColor(R.styleable.EmptyView_android_textColor, ContextCompat.getColor(getContext(),
                    R.color.gray_text));
            int textSize = typedArray.getDimensionPixelSize(
                    R.styleable.EmptyView_android_textSize
                    , getResources().getDimensionPixelSize(R.dimen.text_view_textSize)
            );

            typedArray.recycle();

            textView = this.generatorTextView(
                    TextUtils.isEmpty(text) ? "暂无相关数据" : text
                    , textColor
                    , textSize
                    , isTextBold
                    , textMarginTop
            );
            imageView = this.generatorEmptyView(iconId, iconWidth);
            super.addView(imageView);
            super.addView(textView);
        } else {
            imageView = this.generatorEmptyView(R.mipmap.default_empty_pic, DensityUtilKt.dp2px(140, getContext()));
            super.addView(imageView);
            textView = this.generatorTextView(
                    "暂无相关数据"
                    , ContextCompat.getColor(getContext(), R.color.gray_text)
                    , getResources().getDimensionPixelSize(R.dimen.text_view_textSize)
                    , false
                    , DensityUtilKt.dp2px(8, getContext())
            );
            super.addView(textView);
        }
    }

    /**
     * 生成iconView
     */
    private ImageView generatorEmptyView(int imageRes, int width) {
        ImageView emptyImageView = new ImageView(getContext());
        emptyImageView.setLayoutParams(new ViewGroup.LayoutParams(width,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        emptyImageView.setAdjustViewBounds(true);
        emptyImageView.setImageResource(imageRes);
        return emptyImageView;
    }


    /**
     * 生成文本控件
     */
    private TextView generatorTextView(String text, int textColor, int textSize,
                                       boolean isBold, int marginTop) {
        TextView textView = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = marginTop;
        textView.setLayoutParams(lp);
        textView.setText(text);
        textView.setTextColor(textColor);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.getPaint().setFakeBoldText(isBold);
        textView.setGravity(Gravity.CENTER);

        return textView;
    }

    /**
     * 设置icon 图片
     */
    public void setIcon(int imageResource) {
        imageView.setImageResource(imageResource);
    }

    /**
     * 设置文本
     */
    public void setText(String text) {
        textView.setText(text);
    }

    /**
     * 设置文本大小
     */
    public void setTextSize(int textSize) {
        textView.setTextSize(textSize);
    }

    /**
     * 设置文本颜色
     */
    public void setTextColor(int textColor) {
        textView.setTextColor(textColor);
    }
}
