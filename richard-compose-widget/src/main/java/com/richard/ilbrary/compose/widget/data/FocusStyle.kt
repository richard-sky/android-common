package com.richard.ilbrary.compose.widget.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.richard.library.context.AppContext
import com.richard.library.context.R
import com.richard.library.context.util.px2dpUnit

/**
 * 输入框焦点样式
 * @param borderWidth 边框宽度
 * @param radius 圆角
 * @param focusedBorderColor 聚焦边框颜色
 * @param unfocusBorderColor 失焦边框颜色
 * @param focusedBgColor 聚焦背景色
 * @param unfocusBgColor 失焦背景色
 */
data class FocusStyle(
    val borderWidth: Dp = AppContext.getDimension(R.dimen.line_size).px2dpUnit(),
    val radius: Dp = AppContext.getDimension(R.dimen.radius_value).px2dpUnit(),
    val focusedBorderColor: Color = Color.Transparent,
    val unfocusBorderColor: Color = Color.Transparent,
    val focusedBgColor: Color = Color.Transparent,
    val unfocusBgColor: Color = Color.Transparent,
) {
    companion object {
        val Main = FocusStyle(
            borderWidth = AppContext.getDimension(R.dimen.line_size).px2dpUnit(),
            radius = AppContext.getDimension(R.dimen.radius_value).px2dpUnit(),
            focusedBorderColor = Color(AppContext.getColor(R.color.main)),
            unfocusBorderColor = Color(AppContext.getColor(R.color.main)).copy(alpha = 0.2f),
            focusedBgColor = Color(AppContext.getColor(R.color.main)).copy(alpha = 0.05f),
        )

        val Border = FocusStyle(
            borderWidth = AppContext.getDimension(R.dimen.line_size).px2dpUnit(),
            radius = AppContext.getDimension(R.dimen.radius_value).px2dpUnit(),
            focusedBorderColor = Color(AppContext.getColor(R.color.dark_line)),
            unfocusBorderColor = Color(AppContext.getColor(R.color.light_line)),
        )
    }
}
