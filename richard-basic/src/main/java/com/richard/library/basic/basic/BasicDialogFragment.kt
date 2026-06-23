package com.richard.library.basic.basic

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.FloatRange
import androidx.annotation.IdRes
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.richard.library.basic.R
import com.richard.library.basic.basic.dict.Direction
import com.richard.library.basic.basic.uiview.UIView
import com.richard.library.basic.basic.uiview.UIViewImpl
import com.richard.library.basic.eventbus.EventData
import com.richard.library.basic.util.FragmentUtil
import com.richard.library.context.AppContext
import com.richard.library.context.immersionbar.ImmersionBar
import com.richard.library.context.immersionbar.SystemBarUtil
import com.richard.library.context.util.HideNavBarUtil
import com.richard.library.context.util.dp2px
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * <pre>
 * Description : BaseDialogFragment基类
 * Author : admin-richard
 * Date : 2019-05-10 17:57
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-10 17:57      admin-richard         new file.
</pre> *
 */
abstract class BasicDialogFragment : AppCompatDialogFragment(), UIInitializer, OnKeyListener {

    //消息提示UIView
    private var uiView: UIView? = null

    //状态栏和导航栏控制
    private var systemBar: ImmersionBar? = null

    //当前fragment是否对用户可见
    var isUserVisible: Boolean = false
        private set

    //内容视图View
    @JvmField
    internal var contentView: View? = null

    //关闭DialogFragment监听事件
    private var onDismissListener: DialogInterface.OnDismissListener? = null

    //是否允许dialog 显示到其他app之上
    private var isAllowCanShowOnOtherApp = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setStyle(STYLE_NO_TITLE, R.style.dialog_round_corner)
        systemBar = SystemBarUtil.withBar(this)
        uiView = UIViewImpl(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (contentView != null) {
            return contentView
        }

        this.initLayoutView()
        return contentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }

        this.initData()
        this.bindListener()
        systemBar!!.init()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = MyDialog(requireContext(), theme, systemBar!!)
        if (isAllowCanShowOnOtherApp) {
            val window = dialog.window
            if (window != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                    window.setType(WindowManager.LayoutParams.TYPE_PHONE)
                } else {
                    window.setType(WindowManager.LayoutParams.TYPE_TOAST)
                }
            }
        }

        return dialog
    }

    override fun onResume() {
        super.onResume()
        this.callOnUserVisible(validateUserVisible())
    }

    override fun onPause() {
        super.onPause()
        this.callOnUserVisible(validateUserVisible())
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        this.callOnUserVisible(validateUserVisible())
        this.handleUserVisible()
    }

    override fun onDestroyView() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        if (uiView != null) {
            uiView!!.dismissLoading()
            uiView!!.dismissMsgDialog()
            uiView = null
        }
        contentView = null
        onDismissListener = null
        systemBar = null
        super.onDestroyView()
    }

    /**
     * 自定义Dialog
     */
    private class MyDialog(context: Context, theme: Int, systemBar: ImmersionBar) :
        AppCompatDialog(context, theme) {

        //Dialog 显示的监听事件
        private var onShowListener: DialogInterface.OnShowListener? = null

        init {
            this.init(systemBar)
        }

        fun init(systemBar: ImmersionBar) {
            if (!systemBar.isHideBar) {
                super.setOnShowListener { dialog: DialogInterface? ->
                    if (onShowListener != null) {
                        onShowListener!!.onShow(dialog)
                    }
                }
                return
            }

            if (window != null) {
                window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                )
            }

            // 设置窗口显示监听器
            super.setOnShowListener { dialogInterface: DialogInterface? ->
                if (window != null) {
                    HideNavBarUtil.hideBar(window, systemBar.barParams.barHide)
                    window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                }
                if (onShowListener != null) {
                    onShowListener!!.onShow(dialogInterface)
                }
            }
        }

        override fun setOnShowListener(onShowListener: DialogInterface.OnShowListener?) {
            this.onShowListener = onShowListener
        }
    }

    /**
     * 处理对用户的可见性
     */
    fun handleUserVisible() {
        val fragment = FragmentUtil.getFragments(getChildFragmentManager())
        for (item in fragment) {
            if (item is BasicScaffoldFragment) {
                item.callOnUserVisible(validateUserVisible())
                item.handleUserVisible()
            } else if (item is BasicDialogFragment) {
                item.callOnUserVisible(validateUserVisible())
                item.handleUserVisible()
            }
        }
    }

    /**
     * 调用onUserVisible()
     */
    fun callOnUserVisible(isVisible: Boolean) {
        this.isUserVisible = isVisible
        this.onUserVisible(isVisible)
    }

    /**
     * 验证当前fragment对用户是否可见
     */
    private fun validateUserVisible(): Boolean {
        if (!isResumed) {
            return false
        }

        var fragment = parentFragment ?: return !isHidden

        while (true) {
            val f = fragment.parentFragment ?: return !fragment.isHidden
            fragment = f
        }
    }

    /**
     * 设置内容视图
     */
    protected open fun setContentView(contentView: View?) {
        this.contentView = contentView
    }

    /**
     * 设置Compose布局
     */
    protected open fun setContent(content: @Composable () -> Unit) {
        val composeView = ComposeView(requireContext()).apply {
            if (dialog != null) {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnLifecycleDestroyed(
                        viewLifecycleOwner
                    )
                )
            } else {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            }

            setContent(
                {
                    CompositionLocalProvider(
                        LocalViewModelStoreOwner provides this@BasicDialogFragment,
                        LocalLifecycleOwner provides this@BasicDialogFragment,
                        content = content
                    )
                }
            )
        }
        setContentView(composeView)
    }

    /**
     * 通过viewId获取View
     */
    fun <T : View?> findViewById(@IdRes viewId: Int): T? {
        if (view == null) {
            return null
        }
        return requireView().findViewById<T?>(viewId)
    }

    /**
     * 在UI线程中执行
     */
    fun runOnUiThread(runnable: Runnable?) {
        val activity = getActivity()
        activity?.runOnUiThread(runnable)
    }

    /**
     * 获取UIView
     */
    fun getUIView(): UIView {
        return uiView!!
    }

    /**
     * 获取控制系统状态栏和导航栏控制
     */
    fun getSystemBar(): ImmersionBar {
        return systemBar!!
    }

    /**
     * 获取当前Dialog View的宽
     */
    val width: Int
        get() {
            if (view == null) {
                return 0
            }
            return requireView().measuredWidth
        }

    /**
     * 获取当前Dialog View的高
     */
    val height: Int
        get() {
            if (view == null) {
                return 0
            }
            return requireView().measuredHeight
        }

    /**
     * 设置允许出现在其它应用上（必须先在系统设置中开启"出现在其它应用上"的权限）
     * 并且在清单文件中添加：
     * <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"></uses-permission>
     * <uses-permission android:name="android.permission.SYSTEM_OVERLAY_WINDOW"></uses-permission>
     */
    fun allowCanShowOnOtherApp(): BasicDialogFragment {
        this.isAllowCanShowOnOtherApp = true
        return this
    }

    /**
     * 接收到EventBus事件时会调用
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


    //---------------------------------
    private var mDimAmount = 0.5f //背景昏暗度
    private var direction: Direction? = null //dialog显示方向位置
    private var marginLeftRightDp = 30 //左右边距
    private var marginTopBottomDp = 30 //上下边距
    private var animStyle = 0 //进入退出动画
    private var widthDp = WindowManager.LayoutParams.WRAP_CONTENT
    private var heightDp = WindowManager.LayoutParams.WRAP_CONTENT

    override fun onStart() {
        super.onStart()
        this.initParams()
    }

    private fun initParams() {
        if (dialog == null) {
            return
        }
        val window = dialog!!.window
        if (window != null) {
            val params = window.attributes
            params.dimAmount = mDimAmount

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
    ): BasicDialogFragment {
        mDimAmount = level
        return this
    }

    /**
     * 显示方向位置
     */
    fun setShowDirection(direction: Direction?): BasicDialogFragment {
        this.direction = direction
        return this
    }

    /**
     * 设置宽高
     *
     * @param widthDp  以dp为单位的宽
     * @param heightDp 以dp为单位的高
     */
    fun setSize(widthDp: Int, heightDp: Int): BasicDialogFragment {
        this.widthDp = widthDp
        this.heightDp = heightDp
        return this
    }

    /**
     * 设置宽
     *
     * @param widthDp 以dp为单位的宽
     */
    fun setWidth(widthDp: Int): BasicDialogFragment {
        this.widthDp = widthDp
        return this
    }

    /**
     * 设置高
     *
     * @param heightDp 以dp为单位的高
     */
    fun setHeight(heightDp: Int): BasicDialogFragment {
        this.heightDp = heightDp
        return this
    }

    /**
     * 设置上下margin
     *
     * @param marginLeftRightDp 左右margin
     * @param marginTopBottomDp 上下margin
     */
    fun setMargin(marginLeftRightDp: Int, marginTopBottomDp: Int): BasicDialogFragment {
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
    fun setMarginLeftRight(marginLeftRightDp: Int): BasicDialogFragment {
        this.marginLeftRightDp = marginLeftRightDp
        this.widthDp = 0
        return this
    }

    /**
     * 设置上下margin
     *
     * @param marginTopBottomDp 上下边距
     */
    fun setMarginTopBottom(marginTopBottomDp: Int): BasicDialogFragment {
        this.marginTopBottomDp = marginTopBottomDp
        this.heightDp = 0
        return this
    }

    /**
     * 设置进入退出动画
     *
     * @param animStyle 动画样式资源id
     */
    fun setAnimStyle(@StyleRes animStyle: Int): BasicDialogFragment {
        this@BasicDialogFragment.animStyle = animStyle
        return this
    }

    /**
     * 设置是否点击外部取消
     *
     * @param outCancel 是否点击外部取消
     */
    fun setOutCancel(outCancel: Boolean): BasicDialogFragment {
        super.setCancelable(outCancel)
        return this
    }

    /**
     * 设置关闭时的回调监听
     */
    fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?): BasicDialogFragment {
        this.onDismissListener = onDismissListener
        return this
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if (parentFragment != null) {
            requireParentFragment().onHiddenChanged(false)
        }

        if (onDismissListener != null) {
            onDismissListener!!.onDismiss(dialog)
        }
    }

    fun show(manager: FragmentManager) {
        this.show(manager, System.currentTimeMillis().toString())
    }

    fun showSingle(manager: FragmentManager) {
        val tag = javaClass.getName()
        if (manager.isDestroyed || manager.findFragmentByTag(tag) != null) {
            return
        }
        super.showNow(manager, tag)
    }

    fun showSingle(manager: FragmentManager, tag: String?) {
        if (manager.isDestroyed || manager.findFragmentByTag(tag) != null) {
            return
        }
        super.showNow(manager, tag)
    }

    override fun show(transaction: FragmentTransaction, tag: String?): Int {
        return super.show(transaction, tag)
    }

    override fun show(manager: FragmentManager, tag: String?) {
        if (manager.isDestroyed) {
            return
        }
        super.show(manager, tag)
    }

    override fun showNow(manager: FragmentManager, tag: String?) {
        if (manager.isDestroyed) {
            return
        }
        super.showNow(manager, tag)
    }

    /**
     * 当前fragment对于用户可见或不可见时回调
     */
    protected fun onUserVisible(isVisible: Boolean) {
    }
}