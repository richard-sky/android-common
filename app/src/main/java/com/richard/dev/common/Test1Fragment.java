package com.richard.dev.common;

import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.richard.dev.common.databinding.FragmentTest1Binding;
import com.richard.library.basic.basic.BasicBindingFragment;

/**
 * @author: Richard
 * @createDate: 2024/3/5 10:19
 * @version: 1.0
 * @description: 描述
 */
public class Test1Fragment extends BasicBindingFragment<FragmentTest1Binding> {

    @Override
    public void initLayoutView() {
        setContentView(R.layout.fragment_test1);
    }

    @Override
    public void initData() {
        binding.tvText.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(@NonNull View v) {
                Log.d("testttt", "Test1Fragment -> onViewAttachedToWindow");
            }

            @Override
            public void onViewDetachedFromWindow(@NonNull View v) {
                Log.d("testttt", "Test1Fragment -> onViewDetachedFromWindow");
            }
        });
    }

    @Override
    public void bindListener() {

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("testtt", "--Test1Fragment-onKeyUp--");
        if (event.isShiftPressed() && keyCode == KeyEvent.KEYCODE_A) {
            Log.d("testtt", "--Test1Fragment-onKeyUp-->Shift + A");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onUserVisible(boolean isVisible) {
        Log.d("testtt", "--Test1Fragment-onUserVisible-->" + isVisible);
    }
}
