package com.richard.library.basic.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * <pre>
 * Description : 支持选中之前和取消选中前置条件回调的复选框
 * Author : admin-richard
 * Date : 2019-08-12 15:15
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-08-12 15:15     admin-richard         new file.
 * </pre>
 */
public class XCheckBox extends AppCompatCheckBox {

    private Callback mCallback;
    private boolean isEnabled = true;

    public XCheckBox(Context context) {
        super(context);
    }

    public XCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled) {
            return false;
        }

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (mCallback != null) {
                if (isChecked()) {
                    mCallback.onBeforeCancelCheckCallback();
                } else {
                    mCallback.onBeforeConfirmCheckCallback();
                }
            }
        }
        return true;
    }

    @Override
    public boolean performClick() {
        if (mCallback != null) {
            if (isChecked()) {
                mCallback.onBeforeCancelCheckCallback();
            } else {
                mCallback.onBeforeConfirmCheckCallback();
            }
            return false;
        }
        return super.performClick();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

    public interface Callback {
        /**
         * 选中之前回调
         */
        void onBeforeConfirmCheckCallback();

        /**
         * 取消选中之前回调
         */
        void onBeforeCancelCheckCallback();
    }
}
