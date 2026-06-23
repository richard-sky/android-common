package com.richard.ilbrary.compose.widget

import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.richard.ilbrary.compose.widget.data.DialogButton
import com.richard.ilbrary.compose.widget.type.Direction
import com.richard.library.compose.widget.R
import com.richard.library.context.AppContext
import com.richard.library.context.immersionbar.SystemBarUtil
import com.richard.library.context.util.HideNavBarUtil
import com.richard.library.context.util.isNotEmpty
import com.richard.library.context.util.isNull

/**
 * 通用自定义弹窗
 * 仅保留入场弹出动画，移除退场关闭动画，低版本Compose兼容
 * @param modifier modifier
 * @param show 外部控制弹窗总显示开关
 * @param title 标题文本，为空则不显示标题
 * @param titleIconId 标题图标资源Id
 * @param titleAlignment 标题横向对齐
 * @param titleTextStyle 标题文字样式
 * @param buttonList 底部按钮集合
 * @param buttonArrangement 按钮行对齐方式
 * @param dialogCorner 弹窗圆角
 * @param dialogSpace 弹窗整体内边距
 * @param contentSpace 标题与内容间距
 * @param buttonTopSpace 内容与按钮间距
 * @param outsideClickDismiss 点击外部/返回键是否允许关闭弹窗
 * @param showBackMask 是否显示Dialog蒙版背景
 * @param onDismiss 弹窗关闭回调（无退场动画，立即执行）
 * @param content 弹窗自定义内容区域
 */
@Composable
fun ContentDialog(
    modifier: Modifier? = null,
    show: MutableState<Boolean>,
    title: String? = null,
    titleIconId: Int? = null,
    titleAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    titleTextStyle: TextStyle = TextStyle(
        fontSize = dimensionResource(R.dimen.text_view_textSize).value.sp,
        color = colorResource(R.color.text),
        fontWeight = FontWeight.Medium
    ),
    buttonList: List<DialogButton>? = null,
    buttonArrangement: Arrangement.Horizontal = Arrangement.End,
    dialogCorner: Dp = dimensionResource(R.dimen.big_radius_value),
    dialogSpace: Dp = dimensionResource(R.dimen.content_padding),
    contentSpace: Dp = 14.dp,
    buttonTopSpace: Dp = 30.dp,
    outsideClickDismiss: Boolean = true,
    showBackMask: Boolean = true,
    onDismiss: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { AppContext.getActivity(context) }
    var dialogWindow: Window? by remember { mutableStateOf(null) }

    // 动画可见状态：仅控制入场动画
    var animVisible by remember { mutableStateOf(false) }

    LaunchedEffect(show.value) {
        animVisible = show.value
        if (show.value) {
            //--隐藏状态栏和导航栏业务
            if (!SystemBarUtil.isHideBar(activity)) {
                return@LaunchedEffect
            }
            dialogWindow?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            )
        } else {
            onDismiss?.invoke()
        }
    }

    // 生命周期兜底：组件销毁重置状态，避免残留
    DisposableEffect(Unit) {
        onDispose {
            animVisible = false
        }
    }

    // 外部关闭直接不渲染Dialog，无退场动画
    if (!show.value) return

    Dialog(
        onDismissRequest = {
            if (outsideClickDismiss) {
                show.value = false
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = outsideClickDismiss,
            dismissOnClickOutside = outsideClickDismiss,
            usePlatformDefaultWidth = modifier.isNull(),
        )
    ) {

        //--隐藏状态栏和导航栏业务
        val rootView = LocalView.current
        DisposableEffect(rootView) {
            val win = rootView.findDialogWindow()
            dialogWindow = win
            win?.let { window ->
                if (SystemBarUtil.isHideBar(activity)) {
                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    )
                    HideNavBarUtil.hideBar(window, SystemBarUtil.getBarHide(activity))
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
                }
            }

            onDispose {
            }
        }

        if (!showBackMask) {
            val windowProvider = LocalView.current.parent as? DialogWindowProvider
            SideEffect {
                windowProvider?.window?.setDimAmount(0f)
            }
        }

        AnimatedVisibility(
            visible = animVisible,
            enter = fadeIn(tween(220, easing = FastOutSlowInEasing)) +
                    scaleIn(
                        initialScale = 0.95F,
                        animationSpec = tween(220, easing = FastOutSlowInEasing)
                    )
        ) {
            Card(
                modifier = if (modifier.isNull()) Modifier else modifier,
                shape = RoundedCornerShape(dialogCorner),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.bg))
            ) {
                Column(modifier = Modifier.padding(dialogSpace)) {
                    // 标题区域
                    if (title.isNotEmpty()) {

                        FText(
                            modifier = Modifier.align(titleAlignment),
                            text = title,
                            style = titleTextStyle,
                            iconId = titleIconId,
                            iconPadding = dimensionResource(R.dimen.drawable_padding),
                            iconDirection = Direction.LEFT
                        )

                        Spacer(modifier = Modifier.height(contentSpace))
                    }

                    // 自定义内容区域
                    content.invoke(this)

                    Spacer(modifier = Modifier.height(buttonTopSpace))

                    // 底部按钮区域
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = buttonArrangement
                    ) {
                        buttonList?.forEachIndexed { index, button ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.content_item_margin)))
                            }

                            FButton(
                                text = button.text,
                                textSize = button.textSize,
                                textColor = button.textColor,
                                isOutlinedButton = button.isOutline,
                                width = button.width,
                                height = button.height
                                    ?: dimensionResource(R.dimen.middle_button_height),
                                horizontalPadding = button.horizontalPadding,
                                onClick = {
                                    if (button.onClick == null) {
                                        show.value = false
                                    } else {
                                        button.onClick.invoke()
                                    }
                                },
                                shape = button.shape,
                            )
                        }
                    }
                }
            }
        }
    }
}

// 从View向上查找Dialog Window
private fun View.findDialogWindow(): Window? {
    var parentView: View? = this
    while (parentView != null) {
        val provider = parentView as? androidx.compose.ui.window.DialogWindowProvider
        if (provider != null) return provider.window
        parentView = parentView.parent as? View
    }
    return null
}