package com.richard.library.basic.util;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.richard.library.context.util.UIThread;

/**
 * @author: Richard
 * @createDate: 2025/8/27 17:17
 * @version: 1.0
 * @description: 编辑框控件工具类
 */
public final class EditTextUtil {

    @SuppressLint("ClickableViewAccessibility")
    public static void setShowSoftInputAndSelectAllEvent(TextView view){
        view.setOnTouchListener((v, event) -> {
            if (event != null && event.getAction() == MotionEvent.ACTION_UP) {
                if (!KeyboardUtil.isSoftInputVisible(view)
                        || view.length() > 0 && view.getSelectionStart() == view.getSelectionEnd()) {
                    KeyboardUtil.showSoftInput(view,0,true,10);
                }else{
                    ((EditText)view).setSelection(view.getText().length());
                }
            }
            return false;
        });
    }

    /**
     * 选中控件中的全部值
     */
    public static void selectAll(TextView view){
        UIThread.runOnUiThreadDelayed(10, () -> {
            view.requestFocus();
            if(view instanceof EditText editText){
                editText.selectAll();
            }
        });
    }
}
