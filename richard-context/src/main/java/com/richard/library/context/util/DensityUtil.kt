package com.richard.library.context.util

import android.content.Context
import android.util.TypedValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.richard.library.context.AppContext

/**
 * dp转px
 *
 * @param dpVal dp值
 */
fun Float.dp2px(): Int {
    return this.dp2px(AppContext.get())
}

/**
 * sp转px
 *
 * @param spVal sp值
 */
fun Float.sp2px(): Int {
    return this.sp2px(AppContext.get())
}

/**
 * px转dp
 *
 * @param pxVal px值
 */
fun Float.px2dp(): Float {
    return this.px2dp(AppContext.get())
}

/**
 * px转sp
 *
 * @param pxVal px值
 */
fun Float.px2sp(): Float {
    return this.px2sp(AppContext.get())
}

/**
 * dp转px
 *
 * @param dpVal dp值
 */
fun Float.dp2px(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics
    ).toInt()
}

/**
 * sp转px
 *
 * @param spVal sp值
 */
fun Float.sp2px(context: Context): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this,
        context.resources.displayMetrics
    ).toInt()
}

/**
 * px转dp
 *
 * @param pxVal px值
 */
fun Float.px2dp(context: Context): Float {
    val scale = context.resources.displayMetrics.density
    return (this / scale)
}

/**
 * px转sp
 *
 * @param pxVal px值
 */
fun Float.px2sp(context: Context): Float {
    val dm = context.resources.displayMetrics
    val fontScale = context.resources.configuration.fontScale
    return this / (dm.density * fontScale)
}

/**
 * 转换数值关系
 *
 * @param unit  需转换成的单位（比如：TypedValue.COMPLEX_UNIT_MM）
 * @param value 需转换的数值
 */
fun Float.convert(context: Context, unit: Int): Float {
    return TypedValue.applyDimension(unit, this, context.resources.displayMetrics)
}

/**
 * 转换数值关系
 *
 * @param unit  需转换成的单位（比如：TypedValue.COMPLEX_UNIT_MM）
 * @param value 需转换的数值
 */
fun Float.convert(unit: Int): Float {
    return this.convert(AppContext.get(), unit)
}


// ========== Compose 单位转换 ==========

/**
 * px转dp
 *
 * @param pxVal px值
 */
fun Float.px2dpUnit(): Dp {
    return this.px2dp(AppContext.get()).dp
}

/**
 * px转sp
 *
 * @param pxVal px值
 */
fun Float.px2spUnit(): TextUnit {
    return this.px2sp(AppContext.get()).sp
}

/**
 * px转dp
 *
 * @param pxVal px值
 */
fun Float.px2dpUnit(context: Context): Dp {
    return this.px2dp(context).dp
}

/**
 * px转sp
 *
 * @param pxVal px值
 */
fun Float.px2spUnit(context: Context): TextUnit {
    return this.px2sp(context).sp
}