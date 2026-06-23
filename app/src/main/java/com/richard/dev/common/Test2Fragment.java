package com.richard.dev.common;

import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.richard.library.basic.basic.BasicScaffoldFragment;

/**
 * @author: Richard
 * @createDate: 2024/3/5 10:19
 * @version: 1.0
 * @description: 描述
 */
public class Test2Fragment extends BasicScaffoldFragment {
    @Override
    public void initLayoutView() {
        TextView textView = new TextView(getContext());
        textView.setText("Test2Fragment");
        setContentView(textView);
    }

    @Override
    public void initData() {

    }

    @Override
    public void bindListener() {
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d("testtt", "--Test2Fragment-onKeyUp--");
        if(event.isShiftPressed() && keyCode == KeyEvent.KEYCODE_A){
            Log.d("testtt", "--Test2Fragment-onKeyUp-->Shift + A");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onUserVisible(boolean isVisible) {
        Log.d("testtt","--Test2Fragment-onUserVisible-->" + isVisible);
    }
}
