package com.richard.dev.common.mvvm.common;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.richard.library.context.util.ReflectUtil;

/**
 * @author: Richard
 * @createDate: 2023/4/8 11:09
 * @version: 1.0
 * @description: LifecycleOwner提供
 */
public class LifeViewModelFactory implements ViewModelProvider.Factory {

    private final LifecycleOwner lifecycleOwner;

    public LifeViewModelFactory(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
    }

    /**
     * Creates a new instance of the given {@code Class}.
     * <p>
     *
     * @param modelClass a {@code Class} whose instance is requested
     * @return a newly created ViewModel
     */
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return ReflectUtil.reflect(modelClass).newInstance(lifecycleOwner).get();
    }
}
