package com.richard.library.basic.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;

import com.richard.library.basic.R;

/**
 * Created by Lenovo on 2017/7/11.
 */
public class ClearEditText extends AppCompatEditText implements OnFocusChangeListener, TextWatcher {

    private Drawable mClearDrawable;
    private boolean hasFocus;
    private TextWatcher mTextWatcher;
    private OnFocusChangeListener mOnFocusChangeListener;
    private OnClickClearButtonListener mOnClickClearButtonListener;

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        //这里构造方法也很重要，不加这个很多属性不能再XML里面定义
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        //获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
        mClearDrawable = getCompoundDrawables()[2];
        if (mClearDrawable == null) {
//        	throw new NullPointerException("You can add drawableRight attribute in XML");
            mClearDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_edit_delete);
        }

        if (mClearDrawable != null) {
            mClearDrawable.setBounds(0, 0, this.dp2px(15), this.dp2px(15));
        }

        //默认设置隐藏图标
        setClearIconVisible(false);
        //设置焦点改变的监听
        super.setOnFocusChangeListener(this);
    }

    /**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度  和
     * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getCompoundDrawables()[2] != null) {
                boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if (touchable) {
                    this.setText("");
                    if (mOnClickClearButtonListener != null) {
                        mOnClickClearButtonListener.onClickClearButton();
                    }
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
     */
    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        this.hasFocus = hasFocus;
        if (hasFocus) {
            setClearIconVisible(this.length() > 0);
        } else {
            setClearIconVisible(false);
        }
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(v, hasFocus);
        }
    }

    @NonNull
    @Override
    public Editable getText() {
        Editable text = super.getText();
        return text == null ? new SpannableStringBuilder() : text;
    }

    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     *
     * @param visible
     */
    public void setClearIconVisible(boolean visible) {
        Drawable right = visible && isEnabled() ? mClearDrawable : null;
        setCompoundDrawables(getCompoundDrawables()[0],
                getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
    }

    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int count,
                              int after) {
        if (hasFocus) {
            setClearIconVisible(s.length() > 0);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    /**
     * 设置晃动动画
     */
    public void setShakeAnimation() {
        this.setAnimation(shakeAnimation(5));
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (!enabled) {
            this.setClearIconVisible(false);
        }
        super.setEnabled(enabled);
    }

    /**
     * 晃动动画
     *
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public Animation shakeAnimation(int counts) {
        Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
        translateAnimation.setInterpolator(new CycleInterpolator(counts));
        translateAnimation.setDuration(1000);
        return translateAnimation;
    }

    public void setTextChangedListener(@NonNull TextWatcher watcher) {
        this.removeTextWatcher();
        this.mTextWatcher = watcher;
        this.addTextChangedListener(mTextWatcher);
    }

    private void removeTextWatcher() {
        if (mTextWatcher != null) {
            this.removeTextChangedListener(mTextWatcher);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        if (mTextWatcher != null) {
            this.setTextChangedListener(mTextWatcher);
        }
        this.addTextChangedListener(this);
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        this.removeTextWatcher();
        this.clearAnimation();
        this.removeTextChangedListener(this);
        super.onDetachedFromWindow();
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener;
    }

    public void setOnClickClearButtonListener(OnClickClearButtonListener onClickClearButtonListener) {
        mOnClickClearButtonListener = onClickClearButtonListener;
    }

    /**
     * dp转px
     *
     * @param dpVal dp值
     */
    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    /**
     * 当点击清除按钮时监听事件
     */
    public interface OnClickClearButtonListener {
        void onClickClearButton();
    }
}

