package com.richard.ilbrary.compose.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.richard.ilbrary.compose.widget.type.Direction
import com.richard.library.compose.widget.R

/**
 * @author: Richard
 * @createDate: 2026/6/9 11:05
 * @version: 1.0
 * @description: Text控件基础上扩展可添加上下左右的图标，颜色字体大小默认读取basic定义
 */
@Preview(
    showBackground = true,
    widthDp = 640,
    heightDp = 360
)
@Composable
fun PreviewFTextUI() {
    Box(contentAlignment = Alignment.Center) {
        FText("名称", iconId = android.R.mipmap.sym_def_app_icon, iconSize = 20.dp)
    }
}

/**
 * @param text  文字
 * @param modifier 布局属性
 * @param color 字体颜色
 * @param autoSize 文字大小自适应
 * @param fontSize 字体大小
 * @param fontStyle 字体样式
 * @param fontWeight 字体粗细
 * @param fontFamily 字体
 * @param letterSpacing 字体间距
 * @param textDecoration  文本修饰
 * @param textAlign 文本对齐
 * @param lineHeight 文本行高
 * @param overflow 文本超出显示
 * @param softWrap 是否自动换行
 * @param maxLines 最大行数
 * @param minLines 最小行数
 * @param onTextLayout 文本布局
 * @param style 文本样式
 * @param iconId 图标资源id
 * @param iconSize 图标大小
 * @param iconPadding 图标间距
 * @param iconDirection 图标位置
 */
@Composable
fun FText(
    /*Text控件原生属性*/
    text: String,
    modifier: Modifier = Modifier,
    color: Color = colorResource(R.color.text),
    autoSize: TextAutoSize? = null,
    fontSize: TextUnit = dimensionResource(R.dimen.text_view_textSize).value.sp,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,

    /*FText控件扩展属性*/
    iconId: Int? = null,//图标资源id
    iconSize: Dp? = null,//图标大小
    iconPadding: Dp = dimensionResource(R.dimen.drawable_padding),//图标间距
    iconDirection: Direction = Direction.LEFT,//图标位置
) {

    if (iconId == null) {
        Text(
            text = text,
            modifier = modifier,
            color = color,
            autoSize = autoSize,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = onTextLayout,
            style = style
        )
        return
    }

    when (iconDirection) {
        Direction.LEFT -> {
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                if (iconSize != null) {
                    Image(
                        painter = painterResource(iconId),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                    )
                } else {
                    Image(
                        painter = painterResource(iconId),
                        contentDescription = null,
                    )
                }

                Spacer(modifier = Modifier.width(iconPadding))

                Text(
                    text = text,
                    modifier = modifier,
                    color = color,
                    autoSize = autoSize,
                    fontSize = fontSize,
                    fontStyle = fontStyle,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily,
                    letterSpacing = letterSpacing,
                    textDecoration = textDecoration,
                    textAlign = textAlign,
                    lineHeight = lineHeight,
                    overflow = overflow,
                )
            }
        }

        Direction.RIGHT -> {
            Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = text,
                    modifier = modifier,
                    color = color,
                    autoSize = autoSize,
                    fontSize = fontSize,
                    fontStyle = fontStyle,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily,
                    letterSpacing = letterSpacing,
                    textDecoration = textDecoration,
                    textAlign = textAlign,
                    lineHeight = lineHeight,
                    overflow = overflow,
                )

                Spacer(modifier = Modifier.width(iconPadding))

                if (iconSize != null) {
                    Image(
                        painter = painterResource(iconId),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                    )
                } else {
                    Image(
                        painter = painterResource(iconId),
                        contentDescription = null,
                    )
                }
            }
        }

        Direction.TOP -> {
            Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                if (iconSize != null) {
                    Image(
                        painter = painterResource(iconId),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                    )
                } else {
                    Image(
                        painter = painterResource(iconId),
                        contentDescription = null,
                    )
                }

                Spacer(modifier = Modifier.height(iconPadding))

                Text(
                    text = text,
                    modifier = modifier,
                    color = color,
                    autoSize = autoSize,
                    fontSize = fontSize,
                    fontStyle = fontStyle,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily,
                    letterSpacing = letterSpacing,
                    textDecoration = textDecoration,
                    textAlign = textAlign,
                    lineHeight = lineHeight,
                    overflow = overflow,
                )
            }
        }

        Direction.BOTTOM -> {
            Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = text,
                    modifier = modifier,
                    color = color,
                    autoSize = autoSize,
                    fontSize = fontSize,
                    fontStyle = fontStyle,
                    fontWeight = fontWeight,
                    fontFamily = fontFamily,
                    letterSpacing = letterSpacing,
                    textDecoration = textDecoration,
                    textAlign = textAlign,
                    lineHeight = lineHeight,
                    overflow = overflow,
                )

                Spacer(modifier = Modifier.height(iconPadding))

                if (iconSize != null) {
                    Image(
                        painter = painterResource(iconId),
                        contentDescription = null,
                        modifier = Modifier.size(iconSize),
                    )
                } else {
                    Image(
                        painter = painterResource(iconId),
                        contentDescription = null,
                    )
                }
            }
        }
    }


}