package com.richard.dev.common.mvp;

import androidx.lifecycle.LifecycleOwner;

/**
 * author：Richard
 * time：2021-06-30 19:48
 * version：v1.0.0
 * description：基础Presenter
 */
public abstract class BasicPresenter<V extends BasicView> {

    private LifecycleOwner lifecycleOwner;
    protected V mView;

    public BasicPresenter(){

    }

    public BasicPresenter(LifecycleOwner lifecycleOwner){
        this.lifecycleOwner = lifecycleOwner;
    }

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    public void attachView(V view){
        this.mView = view;
    }

    public void detachView(){
        this.mView = null;
    }

    public boolean isAttachedView(){
        return this.mView != null;
    }
}
