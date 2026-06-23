package com.richard.library.basic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

/**
 * @author: Richard
 * @createDate: 2024/3/26 16:21
 * @version: 1.0
 * @description: 描述
 */
public class ToastLayout extends LinearLayout {

    private Callback callback;

    public ToastLayout(Context context) {
        super(context);
    }

    public ToastLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ToastLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ToastLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDetachedFromWindow() {
        if(callback != null){
            callback.onDetached();
        }
        super.onDetachedFromWindow();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback{
        void onDetached();
    }
}
