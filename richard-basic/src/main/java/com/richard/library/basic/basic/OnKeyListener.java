package com.richard.library.basic.basic;

import android.view.KeyEvent;

/**
 * @author: Richard
 * @createDate: 2025/8/22 11:23
 * @version: 1.0
 * @description: 按键事件
 */
public interface OnKeyListener {

    /**
     * 当前按键松开时回调
     *
     * @param keyCode 按键码
     * @param event   事件信息
     * @return 是否已消费事件
     */
    default boolean onKeyUp(int keyCode, KeyEvent event){
        return false;
    }

}
