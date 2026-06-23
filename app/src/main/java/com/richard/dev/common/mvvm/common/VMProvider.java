package com.richard.dev.common.mvvm.common;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.richard.library.basic.basic.BasicDialog;
import com.richard.library.basic.basic.BasicPopupWindow;
import com.richard.library.basic.basic.BasicView;

/**
 * @author: Richard
 * @createDate: 2023/4/8 11:11
 * @version: 1.0
 * @description: ViewModel 提供者
 */
public class VMProvider {

    private final ViewModelProvider viewModelProvider;

    private VMProvider(ComponentActivity activity) {
        viewModelProvider = new ViewModelProvider(activity, new LifeViewModelFactory(activity));
    }

    private VMProvider(Fragment fragment) {
        viewModelProvider = new ViewModelProvider(fragment, new LifeViewModelFactory(fragment));
    }

    private VMProvider(BasicDialog dialog) {
        viewModelProvider = new ViewModelProvider(dialog, new LifeViewModelFactory(dialog));
    }

    private VMProvider(BasicPopupWindow window) {
        viewModelProvider = new ViewModelProvider(window, new LifeViewModelFactory(window));
    }

    private VMProvider(BasicView view) {
        viewModelProvider = new ViewModelProvider(view, new LifeViewModelFactory(view));
    }

    public static VMProvider of(@NonNull ComponentActivity activity) {
        return new VMProvider(activity);
    }

    public static VMProvider of(@NonNull Fragment fragment) {
        return new VMProvider(fragment);
    }

    public static VMProvider of(@NonNull BasicDialog dialog) {
        return new VMProvider(dialog);
    }

    public static VMProvider of(@NonNull BasicPopupWindow window) {
        return new VMProvider(window);
    }

    public static VMProvider of(@NonNull BasicView view) {
        return new VMProvider(view);
    }

    public <T extends ViewModel> T get(Class<T> vmClass) {
        return this.viewModelProvider.get(vmClass);
    }

}
