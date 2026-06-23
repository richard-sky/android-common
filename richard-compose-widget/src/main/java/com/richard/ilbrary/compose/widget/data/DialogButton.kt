package com.richard.ilbrary.compose.widget.data

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.richard.library.compose.widget.R
import com.richard.library.context.AppContext
import com.richard.library.context.util.px2spUnit

/**
 * 弹窗按钮数据模型
 * @param text 按钮文字
 * @param onClick 点击事件
 * @param textColor 文字颜色
 * @param textSize 文字字号(sp)
 * @param bgColor 按钮背景色
 * @param shape 按钮形状
 * @param border 边框（outline 按钮使用）
 * @param horizontalPadding 按钮左右内边距
 * @param isOutline 是否为描边按钮
 */
data class DialogButton(
    val text: String,
    val textColor: Color = Color(AppContext.getColor(R.color.button_text)),
    val textSize: TextUnit = AppContext.getDimension(R.dimen.button_text_size).px2spUnit(),
    val bgColor: Color = Color(AppContext.getColor(R.color.button_un_press_bg)),
    val shape: RoundedCornerShape = RoundedCornerShape(AppContext.getDimension(R.dimen.radius_value)),
    val border: BorderStroke? = null,
    val isOutline: Boolean = false,
    val horizontalPadding: Dp = 24.dp,
    val width: Dp? = null,
    val height: Dp? = null,
    val onClick: (() -> Unit)? = null,
)