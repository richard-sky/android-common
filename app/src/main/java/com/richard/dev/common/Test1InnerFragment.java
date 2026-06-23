package com.richard.dev.common;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.richard.library.basic.basic.BasicScaffoldFragment;

/**
 * @author: Richard
 * @createDate: 2024/3/5 10:20
 * @version: 1.0
 * @description: 描述
 */
public class Test1InnerFragment extends BasicScaffoldFragment {
    @Override
    public void initLayoutView() {
        TextView textView = new TextView(getContext());
        textView.setText("Test1InnerFragment");
        setContentView(textView);

        textView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                Log.d("testttt","Test1InnerFragment -> onViewAttachedToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                Log.d("testttt","Test1InnerFragment -> onViewDetachedFromWindow");
            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void bindListener() {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("testtt", "--Test1InnerFragment-onKeyUp--");
        if(event.isShiftPressed() && keyCode == KeyEvent.KEYCODE_A){
            Log.d("testtt", "--Test1InnerFragment-onKeyUp-->Shift + A");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onUserVisible(boolean isVisible) {
        Log.d("testtt", "--Test1InnerFragment-onUserVisible-->" + isVisible);
    }
}
