package com.richard.library.basic.basic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.richard.library.basic.R;
import com.richard.library.basic.basic.dict.Direction;
import com.richard.library.basic.basic.uiview.UIView;
import com.richard.library.basic.basic.uiview.UIViewImpl;
import com.richard.library.basic.eventbus.EventData;
import com.richard.library.basic.widget.NavigationBar;
import com.richard.library.basic.widget.PlaceHolderView;
import com.richard.library.context.AppContext;
import com.richard.library.context.util.DensityUtilKt;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;


/**
 * <pre>
 * Description : PopupWindow基类
 * Author : admin-richard
 * Date : 2019-09-25 14:36
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-09-25 14:36      admin-richard         new file.
 * </pre>
 */
public abstract class BasicPopupWindow extends PopupWindow implements UIInitializer, LifecycleOwner, ViewModelStoreOwner {

    private LifecycleRegistry lifecycleRegistry;
    private ViewModelStore viewModelStore;

    private final WeakReference<Context> context;
    private OnShowListener onShowListener;
    private PopupWindow.OnDismissListener onDismissListener;
    private FragmentManager manager;
    private Bundle arguments;
    protected NavigationBar navigationbar;
    private boolean isFirstInit = true;//是否为首次初始化
    private UIView uIView;
    private PlaceHolderView placeHolderView;
    //当前window显示锚点view
    private WeakReference<View> anchor;
    //设置fragment或者Activity中锚点view（用于PopupWindow中嵌套显示PopupWindow，若打开PopupWindow时未传入该view，默认为当前window显示锚点view）
    private WeakReference<View> rootAnchor;

    //关闭PopupWindow时携带数据返回相关
    private OnWindowResult onWindowResult;
    private int requestCode;
    private int resultCode;
    private Intent resultData;

    public BasicPopupWindow(Context context) {
        this.context = new WeakReference<>(context);
        this.init();
    }

    /**
     * 初始化
     */
    private void init() {
        super.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        super.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        if (getContext() instanceof AppCompatActivity) {
            manager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            super.setBackgroundDrawable(new GradientDrawable());
        }

        lifecycleRegistry = new LifecycleRegistry(this);
        uIView = new UIViewImpl(getContext());

        super.setOnDismissListener(() -> {
            uIView.dismissLoading();
            uIView.dismissMsgDialog();
            if (placeHolderView != null) {
                placeHolderView.setState(PlaceHolderView.State.EMPTY_DATA);
            }
            if (viewModelStore != null) {
                viewModelStore.clear();
            }
            lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
            if (EventBus.getDefault().isRegistered(this)) {
                EventBus.getDefault().unregister(this);
            }

            setBackgroundBlackAlphaLevel(1F);
            if (onDismissListener != null) {
                onDismissListener.onDismiss();
            }

            if (onWindowResult != null) {
                onWindowResult.onWindowResult(requestCode, resultCode, resultData);
            }
        });

        this.initLayoutView();
        lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
    }

    /**
     * 调用初始化相关
     */
    private void invokeInitMethod() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        if (lifecycleRegistry.getCurrentState() == Lifecycle.State.DESTROYED) {
            lifecycleRegistry = new LifecycleRegistry(this);
            lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        }

        if (!isFirstInit) {
            return;
        }
        isFirstInit = false;

        this.initNavigation();
        this.initData();
        this.bindListener();
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
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
     * 获取Context
     */
    public Context getContext() {
        return context.get();
    }

    /**
     * 启动内容占位图
     */
    protected void setPlaceHolderTarget(View targetView) {
        if (placeHolderView != null) {
            return;
        }
        placeHolderView = new PlaceHolderView(getContext(), targetView);
    }


    /**
     * 获取内容占位图
     */
    public PlaceHolderView getPlaceHolderView() {
        //默认contentView为操纵View
        if (placeHolderView == null) {
            this.setPlaceHolderTarget(findViewById(R.id.basic_content_root));
        }
        return placeHolderView;
    }

    /**
     * 获取消息提示UIView
     */
    public UIView getUIView() {
        return uIView;
    }

    /**
     * 获取传递参数
     */
    public Bundle getArguments() {
        return arguments;
    }

    /**
     * 设置传递参数
     */
    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }

    /**
     * 获取fragment或者Activity中锚点view
     */
    public View getRootAnchor() {
        return rootAnchor != null ? rootAnchor.get() : null;
    }

    /**
     * 设置fragment或者Activity中锚点view
     */
    public void setRootAnchor(View rootAnchor) {
        this.rootAnchor = new WeakReference<>(rootAnchor);
    }

    /**
     * 获取当前PopupWindow显示锚点View
     */
    public View getAnchor() {
        return anchor != null ? anchor.get() : null;
    }

    /**
     * 设置显示windows的回调事件
     */
    public void setOnShowListener(OnShowListener onShowListener) {
        this.onShowListener = onShowListener;
    }

    @Override
    public void setOnDismissListener(PopupWindow.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public BasicPopupWindow setContentView(@LayoutRes int layoutResId) {
        this.setContentView(LayoutInflater.from(getContext()).inflate(layoutResId, null));
        return this;
    }

    @Override
    public void setContentView(View contentView) {
        LinearLayout rootView = null;
        if (contentView != null) {
            rootView = new LinearLayout(getContext());
            rootView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT
            ));
            rootView.setId(R.id.basic_content_root);
            rootView.setOrientation(LinearLayout.VERTICAL);

            navigationbar = new NavigationBar(getContext());
            navigationbar.setVisibility(View.GONE);
            rootView.addView(navigationbar, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT
                    , AppContext.getDimensionPixelSize(R.dimen.navigation_bar_height)
            ));

            //更新子View大小
            ViewGroup.LayoutParams childLayoutParams = contentView.getLayoutParams();
            if (childLayoutParams == null) {
                rootView.addView(contentView, new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT
                        , ViewGroup.LayoutParams.MATCH_PARENT
                ));
            } else {
                childLayoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
                childLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                rootView.addView(contentView);
            }
        }

        super.setContentView(rootView);
    }

    public <T extends View> T findViewById(@IdRes int viewId) {
        if (getContentView() != null) {
            return getContentView().findViewById(viewId);
        }
        return null;
    }

    /**
     * 初始化导航条
     */
    private void initNavigation() {
        if (navigationbar == null) {
            return;
        }
        navigationbar.setVisibility(View.GONE);
//        navigationbar.setBottomLineViewShow(false);
        navigationbar.setRightImageView(R.mipmap.icon_close);
        navigationbar.setRightImageViewShow(true);
        navigationbar.setRightImageViewClickListener((v) -> dismiss());
    }


    @SuppressWarnings("rawtypes")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(EventData event) {
        this.onReceiveMessageEvent(event);
    }

    /**
     * 当接收到EventBus事件时会调用
     */
    @SuppressWarnings("rawtypes")
    protected void onReceiveMessageEvent(EventData event) {
    }

    /**
     * 设置关闭Dialog时携带数据回调
     */
    public BasicPopupWindow setOnWindowResult(OnWindowResult onWindowResult) {
        this.onWindowResult = onWindowResult;
        return this;
    }

    /**
     * 设置最终界面关闭时回调结果码
     */
    public void setResult(int resultCode) {
        this.setResult(resultCode, null);
    }

    /**
     * 设置最终界面关闭时回调结果码数据
     */
    public void setResult(int resultCode, Intent data) {
        this.resultCode = resultCode;
        this.resultData = data;
    }

    /**
     * 带请求码显示window，最终有结果回调
     */
    public void showAsDropDownForResult(View anchor, int xoff, int yoff, int gravity, int requestCode) {
        this.requestCode = requestCode;
        this.showAsDropDown(anchor, xoff, yoff, gravity);
    }

    /**
     * 带请求码显示window，最终有结果回调
     */
    public void showAtLocationForResult(View parent, int gravity, int x, int y, int requestCode) {
        this.requestCode = requestCode;
        this.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        this.anchor = new WeakReference<>(anchor);
        if (rootAnchor == null) {
            rootAnchor = new WeakReference<>(anchor);
        }

        this.requestCode = 0;
        this.resultCode = 0;
        this.resultData = null;

        this.invokeInitMethod();
        if (onShowListener != null) {
            onShowListener.onShow(this);
        }
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        lifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        this.anchor = new WeakReference<>(parent);
        if (rootAnchor == null) {
            rootAnchor = new WeakReference<>(parent);
        }

        this.requestCode = 0;
        this.resultCode = 0;
        this.resultData = null;

        this.invokeInitMethod();
        if (onShowListener != null) {
            onShowListener.onShow(this);
        }
        super.showAtLocation(parent, gravity, x, y);
        lifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
    }

    /**
     * 指定显示方向
     *
     * @param anchor    基于显示的View
     * @param direction 显示方向
     * @param xOffset   X坐标偏移量 px
     * @param yOffset   Y坐标偏移量 px
     */
    public void showAtDirection(View anchor, Direction direction, int xOffset, int yOffset) {
        int[] location = new int[2];//指定控件的坐标，界面左上角是（0，0），记住这一点，具体什么方向显示就easy了
        anchor.getLocationOnScreen(location);
        switch (direction) {
            case LEFT://指定控件左面显示：
                this.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] - this.getWidth() + xOffset, location[1] + yOffset);
                break;
            case RIGHT://指定控件右面显示：
                this.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] + anchor.getMeasuredWidth() + xOffset, location[1] + yOffset);
                break;
            case TOP://指定控件上面显示：
                this.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] + xOffset, location[1] - this.getHeight() + yOffset);
                break;
            case BOTTOM: //指定控件下面显示：
                this.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] + xOffset, location[1] + anchor.getHeight() + yOffset);
                break;
            default:
        }
    }


    /**
     * 设置宽高
     *
     * @param widthDp  宽 dp
     * @param heightDp 高 dp
     */
    public BasicPopupWindow setSize(int widthDp, int heightDp) {
        super.setWidth(widthDp > 0 ? DensityUtilKt.dp2px(widthDp) : widthDp);
        super.setHeight(heightDp > 0 ? DensityUtilKt.dp2px(heightDp) : heightDp);
        return this;
    }

    /**
     * 设置宽
     *
     * @param widthDp 宽 dp
     */
    public BasicPopupWindow setDPWidth(int widthDp) {
        super.setWidth(widthDp > 0 ? DensityUtilKt.dp2px(widthDp) : widthDp);
        return this;
    }

    /**
     * 设置高
     *
     * @param heightDp 高 dp
     */
    public BasicPopupWindow setDPHeight(int heightDp) {
        super.setHeight(heightDp > 0 ? DensityUtilKt.dp2px(heightDp) : heightDp);
        return this;
    }

    /**
     * 设置上下margin
     *
     * @param marginLeftRightDp 左右margin
     * @param marginTopBottomDp 上下margin
     */
    public BasicPopupWindow setMargin(int marginLeftRightDp, int marginTopBottomDp) {
        super.setWidth(AppContext.getScreenWidth() - 2 * DensityUtilKt.dp2px(marginLeftRightDp));
        super.setHeight(AppContext.getScreenHeight() - 2 * DensityUtilKt.dp2px(marginTopBottomDp));
        return this;
    }

    /**
     * 设置左右margin
     *
     * @param marginLeftRightDp 左右边距
     */
    public BasicPopupWindow setMarginLeftRight(int marginLeftRightDp) {
        super.setWidth(AppContext.getScreenWidth() - 2 * DensityUtilKt.dp2px(marginLeftRightDp));
        return this;
    }

    /**
     * 设置上下margin
     *
     * @param marginTopBottomDp 上下边距
     */
    public BasicPopupWindow setMarginTopBottom(int marginTopBottomDp) {
        super.setHeight(AppContext.getScreenHeight() - 2 * DensityUtilKt.dp2px(marginTopBottomDp));
        return this;
    }

    /**
     * 设置动画
     */
    public BasicPopupWindow setXAnimationStyle(int animationStyle) {
        super.setAnimationStyle(animationStyle);
        return this;
    }

    /**
     * 设置背景灰色程度
     *
     * @param level 0.0f-1.0f
     */
    public BasicPopupWindow setBackgroundBlackAlphaLevel(@FloatRange(from = 0, to = 1) float level) {
        if (!(getContext() instanceof Activity)) {
            return this;
        }

        Window mWindow = ((Activity) getContext()).getWindow();
        WindowManager.LayoutParams params = mWindow.getAttributes();
        params.alpha = level;
        mWindow.setAttributes(params);
        return this;
    }


    /**
     * 设置点击外部的时候关闭window
     */
    public BasicPopupWindow setTouchOutDismiss(boolean isEnabled) {
        super.setOutsideTouchable(isEnabled);
        return this;
    }


    /**
     * 添加view点击事件
     *
     * @param viewId        view id
     * @param clickListener 点击事件
     */
    public BasicPopupWindow addViewClickListener(@IdRes int viewId, View.OnClickListener clickListener) {
        getContentView().findViewById(viewId).setOnClickListener(clickListener);
        return this;
    }

    @FunctionalInterface
    public interface OnShowListener {
        void onShow(PopupWindow popupWindow);
    }

    /**
     * DialogFragment 关闭时携带数据回调
     */
    @FunctionalInterface
    public interface OnWindowResult {
        /**
         * PopupWindow 关闭时携带数据返回上一个界面
         *
         * @param requestCode 请求码
         * @param resultCode  结果码
         * @param data        携带数据
         */
        void onWindowResult(int requestCode, int resultCode, @Nullable Intent data);

    }
}
