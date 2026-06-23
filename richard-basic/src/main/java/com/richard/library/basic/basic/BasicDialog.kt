package com.richard.library.basic.basic

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import androidx.annotation.FloatRange
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.richard.library.basic.R
import com.richard.library.basic.basic.dict.Direction
import com.richard.library.basic.basic.uiview.UIView
import com.richard.library.basic.basic.uiview.UIViewImpl
import com.richard.library.basic.eventbus.EventData
import com.richard.library.basic.immersionbar.SystemBarUtil
import com.richard.library.basic.util.HideNavBarUtil
import com.richard.library.context.AppContext
import com.richard.library.context.util.dp2px
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * <pre>
 * Description : dialog基类
 * Author : admin-richard
 * Date : 2021/4/14 17:28
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2021/4/14 17:28      admin-richard         new file.
</pre> *
 */
abstract class BasicDialog @JvmOverloads constructor(
    context: Context,
    themeResId: Int = R.style.dialog_round_corner
) : AppCompatDialog(context, themeResId), UIInitializer, ViewModelStoreOwner {

    private var _viewModelStore: ViewModelStore? = null
    private var dimAmount = 0.5f //背景昏暗度
    private var direction: Direction? = null //dialog显示位置
    private var marginLeftRightDp = 30 //左右边距
    private var marginTopBottomDp = 30 //上下边距
    private var animStyle = 0 //进入退出动画
    private var widthDp = WindowManager.LayoutParams.WRAP_CONTENT
    private var heightDp = WindowManager.LayoutParams.WRAP_CONTENT
    private var isFirstInit = true //是否为首次初始化
    var fragmentManager: FragmentManager? = null
    private val uiView: UIView

    init {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        val activity = AppContext.getActivity(getContext())
        if (activity is FragmentActivity) {
            this.fragmentManager = activity.supportFragmentManager
        }
        this.uiView = UIViewImpl(this.uiContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        this.initLayoutView()
        this.initData()
        this.bindListener()
        super.onCreate(savedInstanceState)
    }

    /**
     * 设置Compose布局
     */
    fun setContent(content: @Composable () -> Unit) {
        val composeView = ComposeView(context).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnLifecycleDestroyed(this@BasicDialog))
            setContent(
                {
                    CompositionLocalProvider(
                        LocalViewModelStoreOwner provides this@BasicDialog,
                        LocalLifecycleOwner provides this@BasicDialog,
                        content = content
                    )
                }
            )
        }
        setContentView(composeView)
    }

    override fun onAttachedToWindow() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        if (isFirstInit) {
            isFirstInit = false
            this.initParams()
        }
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        viewModelStore.clear()
        super.onDetachedFromWindow()
    }

    override fun onStop() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        uiView.dismissLoading()
        uiView.dismissMsgDialog()
        super.onStop()
    }

    override fun show() {
        val activity = AppContext.getActivity(context)
        if (!SystemBarUtil.isHideBar(activity)) {
            super.show()
            return
        }

        val window = getWindow()
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )

        super.show()

        if (window != null) {
            HideNavBarUtil.hideBar(getWindow(), SystemBarUtil.getBarHide(activity))
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        }
    }

    /**
     * 获取ViewModelStore
     */
    override val viewModelStore: ViewModelStore
        get() {
            if (_viewModelStore == null) {
                _viewModelStore = ViewModelStore()
            }
            return _viewModelStore!!
        }

    /**
     * 获取UIView
     */
    fun getUIView(): UIView {
        return uiView
    }

    /**
     * 获取UI Context
     */
    val uiContext: Context?
        get() {
            if (context is ContextWrapper) {
                return (context as ContextWrapper).baseContext
            }
            return super.getContext()
        }

    /**
     * 接收到EventBus事件
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: EventData<*>?) {
        this.onReceiveMessageEvent(event)
    }

    /**
     * 当接收到EventBus事件时会调用
     */
    protected fun onReceiveMessageEvent(event: EventData<*>?) {
    }

    /**
     * 初始化属性参数
     */
    private fun initParams() {
        val window = getWindow()
        if (window != null) {
            val params = window.attributes
            params.dimAmount = dimAmount

            //设置dialog显示位置
            if (direction != null) {
                when (direction) {
                    Direction.TOP -> params.gravity = Gravity.TOP
                    Direction.BOTTOM -> params.gravity = Gravity.BOTTOM
                    Direction.LEFT -> params.gravity = Gravity.START
                    Direction.RIGHT -> params.gravity = Gravity.END
                    else -> {}
                }
            }

            //设置dialog宽度
            if (widthDp == 0) {
                params.width =
                    AppContext.getScreenWidth() - 2 * marginLeftRightDp.toFloat().dp2px()
            } else if (widthDp == WindowManager.LayoutParams.WRAP_CONTENT
                || widthDp == WindowManager.LayoutParams.MATCH_PARENT
            ) {
                params.width = widthDp
            } else {
                params.width = widthDp.toFloat().dp2px()
            }

            //设置dialog高度
            if (heightDp == 0) {
                params.height =
                    AppContext.getScreenHeight() - 2 * marginTopBottomDp.toFloat().dp2px()
            } else if (heightDp == WindowManager.LayoutParams.WRAP_CONTENT
                || heightDp == WindowManager.LayoutParams.MATCH_PARENT
            ) {
                params.height = heightDp
            } else {
                params.height = heightDp.toFloat().dp2px()
            }

            //设置dialog动画
            if (animStyle != 0) {
                window.setWindowAnimations(animStyle)
            }

            window.setAttributes(params)
        }
    }

    /**
     * 设置背景昏暗度
     *
     * @param level 黑色透明级别
     */
    fun setBackgroundBlackAlphaLevel(
        @FloatRange(
            from = 0.0,
            to = 1.0
        ) level: Float
    ): BasicDialog {
        dimAmount = level
        return this
    }

    /**
     * 显示方向位置
     */
    fun setShowDirection(direction: Direction?): BasicDialog {
        this.direction = direction
        return this
    }

    /**
     * 设置宽高
     *
     * @param widthDp  以dp为单位的宽
     * @param heightDp 以dp为单位的高
     */
    fun setSize(widthDp: Int, heightDp: Int): BasicDialog {
        this.widthDp = widthDp
        this.heightDp = heightDp
        return this
    }

    /**
     * 设置宽
     *
     * @param widthDp 以dp为单位的宽
     */
    fun setWidth(widthDp: Int): BasicDialog {
        this.widthDp = widthDp
        return this
    }

    /**
     * 设置高
     *
     * @param heightDp 以dp为单位的高
     */
    fun setHeight(heightDp: Int): BasicDialog {
        this.heightDp = heightDp
        return this
    }

    /**
     * 设置上下margin
     *
     * @param marginLeftRightDp 左右margin
     * @param marginTopBottomDp 上下margin
     */
    fun setMargin(marginLeftRightDp: Int, marginTopBottomDp: Int): BasicDialog {
        this.marginLeftRightDp = marginLeftRightDp
        this.marginTopBottomDp = marginTopBottomDp
        this.widthDp = 0
        this.heightDp = 0
        return this
    }

    /**
     * 设置左右margin
     *
     * @param marginLeftRightDp 左右边距
     */
    fun setMarginLeftRight(marginLeftRightDp: Int): BasicDialog {
        this.marginLeftRightDp = marginLeftRightDp
        this.widthDp = 0
        return this
    }

    /**
     * 设置上下margin
     *
     * @param marginTopBottomDp 上下边距
     */
    fun setMarginTopBottom(marginTopBottomDp: Int): BasicDialog {
        this.marginTopBottomDp = marginTopBottomDp
        this.heightDp = 0
        return this
    }

    /**
     * 设置进入退出动画
     *
     * @param animStyle 动画样式id
     */
    fun setAnimStyle(@StyleRes animStyle: Int): BasicDialog {
        this.animStyle = animStyle
        return this
    }

    /**
     * 设置是否点击外部取消
     *
     * @param outCancel 点击外部是否可以关闭dialog
     */
    fun setOutCancel(outCancel: Boolean): BasicDialog {
        super.setCancelable(outCancel)
        return this
    }

    /**
     * 设置允许出现在其它应用上（必须先在系统设置中开启"出现在其它应用上"的权限）
     * 并且在清单文件中添加：
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"></uses-permission>
     * <uses-permission android:name="android.permission.SYSTEM_OVERLAY_WINDOW"></uses-permission>
     */
    fun allowCanShowOnOtherApp(): BasicDialog {
        val window = window ?: return this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            window.setType(WindowManager.LayoutParams.TYPE_PHONE)
        } else {
            window.setType(WindowManager.LayoutParams.TYPE_TOAST)
        }
        return this
    }
}