package com.richard.library.basic.basic;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.lifecycle.ViewTreeLifecycleOwner;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.uiview.UIView;
import com.richard.library.basic.basic.uiview.UIViewImpl;
import com.richard.library.basic.eventbus.EventData;
import com.richard.library.basic.widget.PlaceHolderView;
import com.richard.library.context.AppContext;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * <pre>
 * Description : 内容View基类
 * Author : admin-richard
 * Date : 2021/4/14 17:28
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2021/4/14 17:28      admin-richard         new file.
 * </pre>
 */
public abstract class BasicView extends FrameLayout implements UIInitializer, LifecycleOwner, ViewModelStoreOwner {

    /**
     * view 生命周期模式
     */
    public interface LifeMode {
        /// 当前View onAttachedToWindow 开始到onDetachedFromWindow 结束---默认该模式
        int SELF = 0;

        /// 跟随Activity、Fragment、BasicDialog 等的生命周期
        int MAIN = 1;
    }

    private int lifeMode = LifeMode.SELF;
    private LifecycleOwner outLifecycleOwner;
    private LifecycleRegistry lifecycleRegistry;
    private ViewModelStore viewModelStore;

    private UIView mUIView;
    private PlaceHolderView mPlaceHolderView;
    private FragmentManager manager;
    private boolean isFirstInit = true;//是否为首次初始化
    private int[] locations = null;//当前view在显示屏幕中坐标位置[0]x坐标，[1]y坐标

    public BasicView(@NonNull Context context) {
        super(context);
        this.init();
    }

    public BasicView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public BasicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BasicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init();
    }

    private void init() {
        lifeMode = this.getLifeMode();
        if (isInEditMode()) {
            this.initLayoutView();
            this.initData();
            this.bindListener();
        }
    }

    /**
     * 调用初始化相关
     */
    private void invokeInitMethod() {
        if (!isFirstInit) {
            return;
        }
        isFirstInit = false;

        if (lifeMode == LifeMode.MAIN) {
            outLifecycleOwner = ViewTreeLifecycleOwner.get(this);
            if (outLifecycleOwner != null) {
                outLifecycleOwner.getLifecycle().addObserver(new LifecycleEventObserver() {
                    @Override
                    public void onStateChanged(@NonNull LifecycleOwner lifecycleOwner, @NonNull Lifecycle.Event event) {
                        if (event.getTargetState() == Lifecycle.State.DESTROYED) {
                            destroy();
                            lifecycleOwner.getLifecycle().removeObserver(this);
                        }
                    }
                });
            }
        }

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (AppContext.getUIContext(this) instanceof FragmentActivity act) {
            manager = act.getSupportFragmentManager();
        }

        mUIView = new UIViewImpl(getContext());
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);

        this.initLayoutView();
        this.initData();
        this.bindListener();
    }

    @Override
    protected void onFinishInflate() {
        this.invokeInitMethod();
        super.onFinishInflate();
    }

    @Override
    protected void onAttachedToWindow() {
        this.invokeInitMethod();
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (outLifecycleOwner == null) {
            this.destroy();
        }
        super.onDetachedFromWindow();
    }

    /**
     * 销毁
     */
    private void destroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        if (mUIView != null) {
            mUIView.dismissLoading();
            mUIView.dismissMsgDialog();
        }

        if (lifecycleRegistry != null && lifeMode == LifeMode.SELF) {
            lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
        }

        if (viewModelStore != null) {
            viewModelStore.clear();
        }

        mUIView = null;
        lifecycleRegistry = null;
        viewModelStore = null;
        mPlaceHolderView = null;
        manager = null;
        locations = null;
        isFirstInit = true;

        this.onDestroy();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return outLifecycleOwner != null ? outLifecycleOwner.getLifecycle() : lifecycleRegistry;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        if (viewModelStore == null) {
            viewModelStore = new ViewModelStore();
        }
        return viewModelStore;
    }

    /**
     * 获取FragmentManager
     */
    public FragmentManager getFragmentManager() {
        return this.manager;
    }

    /**
     * 外部手动传入fragmentManger
     */
    public void setFragmentManager(FragmentManager manager) {
        this.manager = manager;
    }

    /**
     * 获取当前view所在显示屏幕中的x坐标
     */
    public int getOnScreenX() {
        if (locations == null) {
            locations = new int[2];
        }
        super.getLocationOnScreen(locations);
        return locations != null ? locations[0] : 0;
    }

    /**
     * 获取当前view所在显示屏幕中的y坐标
     */
    public int getOnScreenY() {
        if (locations == null) {
            locations = new int[2];
        }
        super.getLocationOnScreen(locations);
        return locations != null ? locations[1] : 0;
    }

    public void setContentView(int layoutResID) {
        if (super.getChildCount() > 0) {
            this.removeAllViews();
        }
        LayoutInflater.from(getContext()).inflate(layoutResID, this);
    }

    public void setContentView(View view) {
        if (super.getChildCount() > 0) {
            this.removeAllViews();
        }
        this.addView(view);
    }

    /**
     * 设置视图内容占位目标view
     */
    protected void setPlaceHolderTarget(View targetView) {
        if (mPlaceHolderView != null) {
            return;
        }
        mPlaceHolderView = new PlaceHolderView(getContext(), targetView);
    }


    /**
     * 获取内容占位图
     */
    public PlaceHolderView getPlaceHolderView() {
        //默认contentView为操纵View
        if (mPlaceHolderView == null) {
            this.setPlaceHolderTarget(findViewById(R.id.content_root));
        }
        return mPlaceHolderView;
    }

    /**
     * 获取ui状态view实现
     */
    public UIView getUIView() {
        return mUIView;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventData event) {
        this.onReceiveMessageEvent(event);
    }


    /**
     * 当接收到EventBus事件时会调用
     */
    protected void onReceiveMessageEvent(EventData event) {
    }

    /**
     * 获取当前view的生命周期模式
     */
    protected abstract int getLifeMode();

    /**
     * 销毁
     */
    protected abstract void onDestroy();

}
