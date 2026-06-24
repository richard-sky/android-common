package com.richard.ilbrary.compose.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.richard.library.compose.widget.R

/**
 *  加载框 dialog
 *  @param show Dialog是否显示
 *  @param text 提示文本
 *  @param showBackMask 是否显示Dialog蒙版背景
 *  @param modifier Modifier
 */
@Composable
fun LoadingDialog(
    show: MutableState<Boolean>,
    text: String? = "请稍等...",
    bgColor: Color = colorResource(R.color.content_bg),
    autoSize: TextAutoSize? = TextAutoSize.StepBased(
        minFontSize = 9.sp,
        maxFontSize = 12.sp,
        stepSize = 0.2.sp
    ),
    showBackMask: Boolean = false,
    modifier: Modifier = Modifier
        .width(100.dp)
        .height(80.dp)
) {
    ContentDialog(
        show = show,
        modifier = modifier,
        outsideClickDismiss = false,
        showBackMask = showBackMask,
        dialogBgColor = bgColor
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(30.dp),
                color = colorResource(R.color.blue),
                strokeWidth = 2.5.dp,
                trackColor = Color.LightGray,
                strokeCap = StrokeCap.Butt,
                gapSize = 1.dp
            )

            text?.let {
                FText(
                    modifier = Modifier.padding(top = 10.dp),
                    text = text,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    autoSize = autoSize
                )
            }
        }
    }
}