package com.richard.ilbrary.compose.widget.util

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.richard.library.context.AppContext
import com.richard.library.context.util.px2dpUnit

/**
 * @author: Richard
 * @createDate: 2026/6/16 13:47
 * @version: 1.0
 * @description: compose 大小工具类
 */

fun Modifier.defaultDialogWidth(): Modifier {
    return defaultDialogWidth(AppContext.get())
}

fun Modifier.defaultDialogWidth(context: Context): Modifier {
    return if (AppContext.isScreenPortrait(context)) {
        this.padding(horizontal = 30.dp)
    } else {
        this.width(400.dp)
    }
}

fun getScreenWidthDp(): Dp {
    return getScreenWidthDp(AppContext.get())
}

fun getScreenWidthDp(context: Context): Dp {
    return AppContext.getScreenWidth(context).toFloat().px2dpUnit()
}

fun getScreenHeightDp(): Dp {
    return getScreenHeightDp(AppContext.get())
}

fun getScreenHeightDp(context: Context): Dp {
    return AppContext.getScreenHeight(context).toFloat().px2dpUnit()
}