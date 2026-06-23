package com.richard.ilbrary.compose.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.richard.ilbrary.compose.widget.type.HolderState
import com.richard.library.compose.widget.R

/**
 * @author: Richard
 * @createDate: 2026/6/9 9:20
 * @version: 1.0
 * @description: 带加载中、空数据、错误页面的占位UI
 */
@Preview(
    showBackground = true,
    widthDp = 640,
    heightDp = 360
)
@Composable
fun PreviewPlaceholderUI() {
    Placeholder(
        state = HolderState.Empty,
        onRetry = {
            println("点击了重试")
        },
        content = {
            Text(text = "成功状态展示的内容")
        }
    )
}

/**
 * 通用占位UI组件
 * @param modifier 占位UI的修饰符
 * @param state 当前页面状态
 * @param emptyIconId 空数据图标
 * @param emptyText 空数据显示文本
 * @param errorIconId 错误图标
 * @param errorText 错误提示文字
 * @param loadingText 加载中时显示的文字
 * @param loadingSize loading动画视图大小
 * @param iconSize 图标大小
 * @param fontSize 文字大小
 * @param fontColor 文字颜色
 * @param onRetry 错误重试点击事件
 * @param content 成功状态展示的内容
 */
@Composable
fun Placeholder(
    modifier: Modifier = Modifier,
    state: HolderState = HolderState.Loading,
    emptyIconId: Int = R.mipmap.ic_placeholder_empty,
    emptyText: String = stringResource(R.string.cw_no_data),
    errorIconId: Int = R.mipmap.ic_placeholder_error,
    errorText: String = stringResource(R.string.cw_load_fail_retry),
    loadingText: String = stringResource(R.string.cw_loading),
    loadingSize: Dp = 33.dp,
    iconSize: Dp = 90.dp,
    fontSize: TextUnit = dimensionResource(R.dimen.text_view_textSize).value.sp,
    fontColor: Color = colorResource(R.color.text),
    onRetry: (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 根据状态渲染不同UI
        when (state) {
            // 加载中
            HolderState.Loading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(loadingSize),
                        color = colorResource(R.color.blue),
                        strokeWidth = 2.5.dp,
                        trackColor = Color.LightGray,
                        strokeCap = StrokeCap.Butt,
                        gapSize = 1.dp
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_padding)))

                    // 提示文字
                    Text(
                        text = loadingText,
                        fontSize = fontSize,
                        textAlign = TextAlign.Center,
                        color = fontColor
                    )
                }
            }

            // 空数据
            HolderState.Empty -> {
                PlaceholderItem(
                    iconId = emptyIconId,
                    iconSize = iconSize,
                    tipText = emptyText,
                    fontSize = fontSize,
                    fontColor = fontColor
                )
            }

            // 加载错误（带重试按钮）
            HolderState.Error -> {
                PlaceholderItem(
                    iconId = errorIconId,
                    iconSize = iconSize,
                    tipText = errorText,
                    fontSize = fontSize,
                    fontColor = fontColor,
                    showButton = onRetry != null,
                    buttonText = stringResource(R.string.cw_reload),
                    onButtonClick = { onRetry?.invoke() }
                )
            }

            // 加载成功，展示业务内容
            HolderState.Success -> content?.invoke()
        }
    }
}

/**
 * 占位UI内部子组件（图标 + 文字 + 可选按钮）
 */
@Composable
private fun PlaceholderItem(
    iconId: Int,
    iconSize: Dp,
    tipText: String,
    fontSize: TextUnit,
    fontColor: Color,
    showButton: Boolean = false,
    buttonText: String = "",
    onButtonClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 占位图标
        Image(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_padding)))

        // 提示文字
        Text(
            text = tipText,
            fontSize = fontSize,
            textAlign = TextAlign.Center,
            color = fontColor
        )

        // 错误状态显示重试按钮
        if (showButton) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_padding)))

            Button(
                onClick = onButtonClick,
                modifier = Modifier.size(120.dp, dimensionResource(R.dimen.normal_button_height)),
            ) {
                Text(
                    text = buttonText,
                    fontSize = dimensionResource(R.dimen.text_view_textSize).value.sp,
                    color = colorResource(R.color.button_text)
                )
            }
        }
    }
}