package com.richard.ilbrary.compose.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.richard.library.context.R

/**
 * @author: Richard
 * @createDate: 2026/6/9 10:26
 * @version: 1.0
 * @description: 按钮
 */
@Preview(
    showBackground = true,
    widthDp = 640,
    heightDp = 360
)
@Composable
fun PreviewFButtonViewUI() {
    Box(contentAlignment = Alignment.Center) {
        FButton(text = "登录", onClick = {})
    }
}

/**
 * 全自定义状态按钮
 * 支持：正常 / 按下 / 禁用 背景色
 * 支持：文字、文字颜色、文字大小
 * 支持：宽高、形状
 * 自带：按下缩放 + 颜色渐变动画
 *
 * @param text 按钮文字
 * @param enabled 是否可用
 * @param width 宽
 * @param height 高
 * @param horizontalPadding 按钮左右内边距
 * @param shape 形状
 * @param normalBgColor 正常背景色
 * @param pressedBgColor 按下背景色
 * @param disabledBgColor 禁用背景色
 * @param textSize 文字大小
 * @param textColor 文字颜色
 * @param isOutlinedButton 是否属于外边框样式按钮
 * @param onClick 点击事件
 */
@Composable
fun FButton(
    modifier: Modifier? = null,
    text: String,
    enabled: Boolean = true,
    width: Dp? = null,
    height: Dp = dimensionResource(R.dimen.normal_button_height),
    horizontalPadding: Dp = 24.dp,
    shape: Shape = RoundedCornerShape(dimensionResource(R.dimen.radius_value)),
    normalBgColor: Color = colorResource(R.color.button_un_press_bg),
    pressedBgColor: Color = colorResource(R.color.button_pressed_bg),
    disabledBgColor: Color = colorResource(R.color.button_disabled_bg),
    textSize: TextUnit = dimensionResource(R.dimen.button_text_size).value.sp,
    textColor: Color = colorResource(R.color.button_text),
    isOutlinedButton: Boolean = false,
    onClick: () -> Unit
) {
    GradientButton(
        modifier = modifier,
        text = text,
        enabled = enabled,
        width = width,
        height = height,
        horizontalPadding = horizontalPadding,
        shape = shape,
        normalGradient = listOf(normalBgColor, normalBgColor),
        pressedGradient = listOf(pressedBgColor, pressedBgColor),
        disabledGradient = listOf(disabledBgColor, disabledBgColor),
        textSize = textSize,
        textColor = textColor,
        isOutlinedButton = isOutlinedButton,
        onClick = onClick
    )
}