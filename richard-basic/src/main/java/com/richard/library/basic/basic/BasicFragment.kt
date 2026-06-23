package com.richard.library.basic.basic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.richard.library.basic.basic.uiview.UIView
import com.richard.library.basic.basic.uiview.UIViewImpl
import com.richard.library.basic.eventbus.EventData
import com.richard.library.basic.util.FragmentUtil
import com.richard.library.context.immersionbar.ImmersionBar
import com.richard.library.context.immersionbar.SystemBarUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * <pre>
 * Description : Fragment基类
 * Author : admin-richard
 * Date : 2019-05-10 17:57
 * Changelog:
 * Version            Date            Author              Detail
 * ----------------------------------------------------------------------
 * 1.0         2019-05-10 17:57      admin-richard         new file.
</pre> *
 */
abstract class BasicFragment : Fragment(), UIInitializer, OnKeyListener {

    //消息提示UIView
    private var uiView: UIView? = null

    //当前fragment是否对用户可见
    var isUserVisible: Boolean = false
        private set

    //布局视图
    @JvmField
    internal var contentView: View? = null

    //状态栏和导航栏控制
    private var systemBar: ImmersionBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        systemBar = SystemBarUtil.withBar(activity)
        uiView = UIViewImpl(this.context)
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
        if (this.uiView != null) {
            uiView!!.dismissLoading()
            uiView!!.dismissMsgDialog()
            this.uiView = null
        }
        contentView = null
        systemBar = null
        super.onDestroyView()
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
     * 设置布局视图
     */
    protected open fun setContentView(contentView: View?) {
        this.contentView = contentView
    }

    /**
     * 设置Compose布局
     */
    protected open fun setContent(content: @Composable () -> Unit) {
        val composeView = ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent(
                {
                    CompositionLocalProvider(
                        LocalViewModelStoreOwner provides this@BasicFragment,
                        LocalLifecycleOwner provides this@BasicFragment,
                        content = content
                    )
                }
            )
        }
        setContentView(composeView)
    }

    /**
     * 结束当前Activity
     */
    fun finish() {
        val activity = getActivity()
        activity?.finish()
    }

    /**
     * 通过id获取布局中的View
     */
    fun <T : View?> findViewById(@IdRes viewId: Int): T? {
        if (view == null) {
            return null
        }
        return requireView().findViewById<T?>(viewId)
    }

    /**
     * 在UI线程中执行业务
     *
     * @param runnable 业务实现
     */
    fun runOnUiThread(runnable: Runnable?) {
        val activity = getActivity()
        activity?.runOnUiThread(runnable)
    }

    /**
     * 获取消息提示UIView
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
     * 获取当前fragment的宽度
     */
    val width: Int
        get() {
            if (view == null) {
                return 0
            }
            return requireView().measuredWidth
        }

    /**
     * 获取当前fragment的高度
     */
    val height: Int
        get() {
            if (view == null) {
                return 0
            }
            return requireView().measuredHeight
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

    /**
     * 当前fragment对于用户可见或不可见时回调
     */
    protected open fun onUserVisible(isVisible: Boolean) {
    }
}