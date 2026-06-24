package com.richard.ilbrary.compose.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import com.richard.ilbrary.compose.widget.data.DialogButton
import com.richard.library.compose.widget.R


/**
 * 通用自定义提示弹窗
 * @param modifier modifier
 * @param show 控制弹窗显示/隐藏
 * @param title 标题文本，为空则不显示标题
 * @param titleIconId 标题图标资源Id
 * @param titleAlignment 标题对齐方式
 * @param titleTextStyle 标题样式（字号、颜色）
 * @param message 内容消息
 * @param messageAlignment 内容对齐方式
 * @param messageTextStyle 内容样式（字号、颜色）
 * @param buttonList 按钮集合，支持任意数量
 * @param buttonArrangement 按钮行对齐方式（左右/居中/均分等）
 * @param dialogCorner 弹窗圆角
 * @param dialogSpace 弹窗内边距
 * @param contentSpace 标题与内容之间间距
 * @param buttonTopSpace 内容与按钮区域间距
 * @param outsideClickDismiss 点击弹窗外部是否关闭
 * @param dialogAlignment Dialog显示位置
 * @param onShow 弹窗显示回调
 * @param onDismiss 弹窗关闭回调
 */
@Composable
fun TipDialog(
    modifier: Modifier? = null,
    show: MutableState<Boolean>,
    title: String = stringResource(R.string.cw_dialog_title),
    titleIconId: Int? = null,
    titleAlignment: Alignment.Horizontal = Alignment.Start,
    titleTextStyle: TextStyle = TextStyle(
        fontSize = dimensionResource(R.dimen.text_view_textSize).value.sp,
        color = colorResource(R.color.text),
        fontWeight = FontWeight.Medium
    ),
    message: String,
    messageAlignment: Alignment.Horizontal = Alignment.Start,
    messageTextStyle: TextStyle = TextStyle(
        fontSize = dimensionResource(R.dimen.text_view_textSize).value.sp,
        color = colorResource(R.color.text)
    ),
    buttonList: List<DialogButton>,
    buttonArrangement: Arrangement.Horizontal = Arrangement.End,
    dialogBgColor: Color = colorResource(R.color.bg),
    dialogCorner: Dp = dimensionResource(R.dimen.big_radius_value),
    dialogSpace: Dp = dimensionResource(R.dimen.content_padding),
    contentSpace: Dp = dimensionResource(R.dimen.content_padding),
    buttonTopSpace: Dp = dimensionResource(R.dimen.button_margin_top),
    outsideClickDismiss: Boolean = true,
    dialogAlignment: Alignment = Alignment.Center,
    onShow: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    ContentDialog(
        modifier = modifier,
        show = show,
        title = title,
        titleIconId = titleIconId,
        titleAlignment = titleAlignment,
        titleTextStyle = titleTextStyle,
        buttonList = buttonList,
        buttonArrangement = buttonArrangement,
        dialogBgColor = dialogBgColor,
        dialogCorner = dialogCorner,
        dialogSpace = dialogSpace,
        contentSpace = contentSpace,
        buttonTopSpace = buttonTopSpace,
        outsideClickDismiss = outsideClickDismiss,
        dialogAlignment = dialogAlignment,
        onShow = onShow,
        onDismiss = onDismiss,
    ) {
        Text(
            text = message,
            modifier = Modifier.align(messageAlignment),
            style = messageTextStyle
        )
    }
}