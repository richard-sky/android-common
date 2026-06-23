package com.richard.library.context.life;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

/**
 * @author: Richard
 * @createDate: 2025/6/30 9:52
 * @version: 1.0
 * @description: activity生命周期回调
 */
public class ActivityLifecycleCallbacks {

    public void onActivityCreated(@NonNull Activity activity) {/**/}

    public void onActivityStarted(@NonNull Activity activity) {/**/}

    public void onActivityResumed(@NonNull Activity activity) {/**/}

    public void onActivityPaused(@NonNull Activity activity) {/**/}

    public void onActivityStopped(@NonNull Activity activity) {/**/}

    public void onActivityDestroyed(@NonNull Activity activity) {/**/}

    public void onLifecycleChanged(@NonNull Activity activity, Lifecycle.Event event) {/**/}

}
