package com.richard.library.context.life;

import android.app.Activity;

/**
 * @author: Richard
 * @createDate: 2025/6/30 9:51
 * @version: 1.0
 * @description: App状态切换监听事件
 */
public interface OnAppStatusChangedListener {
    void onForeground(Activity activity);

    void onBackground(Activity activity);
}
