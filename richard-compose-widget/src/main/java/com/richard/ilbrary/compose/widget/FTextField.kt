package com.richard.ilbrary.compose.widget

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.DecorationBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.richard.ilbrary.compose.widget.data.FocusStyle
import com.richard.library.compose.widget.R
import com.richard.library.context.util.isNotEmpty
import com.richard.library.context.util.isNotNull

/**
 * @author: Richard
 * @createDate: 2026/6/12 11:28
 * @version: 1.0
 * @description: TextField控件基础上扩展可添加上下左右的图标，颜色字体大小默认读取basic定义
 */
@Composable
fun FTextField(
    /*自定义属性*/
    isEditTextMode: Boolean = true,
    height: Dp? = null,
    hintText: String = "",
    labelText: String? = null,
    labelTextStyle: TextStyle = TextStyle(
        fontSize = dimensionResource(R.dimen.text_view_textSize).value.sp,
        color = colorResource(R.color.label_text),
        fontWeight = FontWeight.Medium
    ),
    outerFocusStyle: FocusStyle? = null,
    outerModifier: Modifier = Modifier.fillMaxWidth(),
    innerFocusStyle: FocusStyle? = null,
    innerModifier: Modifier? = null,
    onClick: (() -> Unit)? = null,

    /*TextField原生属性*/
    value: String = "",
    onValueChange: ((String) -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = TextFieldDefaults.shape,
    textStyle: TextStyle = TextStyle(
        fontSize = dimensionResource(R.dimen.edit_text_view_textSize).value.sp,
        color = colorResource(R.color.text),
        lineHeight = if (maxLines == 1) {
            (height ?: dimensionResource(R.dimen.edit_text_height)).value.sp
        } else {
            TextUnit.Unspecified
        },
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),
    colors: TextFieldColors = TextFieldDefaults.colors(
        errorContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        focusedTextColor = colorResource(R.color.text),
        disabledTextColor = colorResource(R.color.text),
        unfocusedTextColor = colorResource(R.color.text),
        focusedPlaceholderColor = Color.Transparent,
        unfocusedPlaceholderColor = Color.Transparent,
        disabledPlaceholderColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    ),
) {

    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    // 监听是否聚焦
    val isFocused by interactionSource.collectIsFocusedAsState()

    // 控件整体高度，固定尺寸
    val containerHeight = height ?: dimensionResource(R.dimen.edit_text_height)

    //外部容器焦点样式
    val outerFocusModifier = if (outerFocusStyle.isNotNull()) {
        Modifier
            .border(
                width = outerFocusStyle.borderWidth,
                color = if (isFocused) outerFocusStyle.focusedBorderColor else outerFocusStyle.unfocusBorderColor,
                shape = RoundedCornerShape(outerFocusStyle.radius)
            )
            .background(
                color = if (isFocused) outerFocusStyle.focusedBgColor else outerFocusStyle.unfocusBgColor,
                shape = RoundedCornerShape(outerFocusStyle.radius)
            )
    } else {
        Modifier
    }

    //内部容器焦点样式
    val innerFocusModifier = if (innerFocusStyle.isNotNull()) {
        Modifier
            .border(
                width = innerFocusStyle.borderWidth,
                color = if (isFocused) innerFocusStyle.focusedBorderColor else innerFocusStyle.unfocusBorderColor,
                shape = RoundedCornerShape(innerFocusStyle.radius)
            )
            .background(
                color = if (isFocused) innerFocusStyle.focusedBgColor else innerFocusStyle.unfocusBgColor,
                shape = RoundedCornerShape(innerFocusStyle.radius)
            )
    } else {
        Modifier
    }

    // 外层固定高度，不会撑满屏幕
    val outerFinalModifier = outerFocusModifier
        .height(containerHeight)
        .then(outerModifier)

    // TextField控件样式修饰
    val innerFinalModifier = if (innerModifier.isNotNull()) {
        innerFocusModifier
            .then(innerModifier)
            .focusable(
                enabled = isEditTextMode,
                interactionSource = interactionSource
            )
    } else {
        innerFocusModifier
            .fillMaxWidth()
            .padding(
                start = if (leadingIcon != null) 0.dp else dimensionResource(R.dimen.content_item_padding_left_right),
                end = if (trailingIcon != null) 0.dp else dimensionResource(R.dimen.content_item_padding_left_right)
            )
            .focusable(
                enabled = isEditTextMode,
                interactionSource = interactionSource
            )
    }


    Row(
        modifier = outerFinalModifier
            .clickable(
                enabled = !isEditTextMode,
                indication = null,
                interactionSource = interactionSource,
                onClick = { onClick?.invoke() }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (labelText.isNotEmpty()) {
            FText(text = labelText, style = labelTextStyle)
            Spacer(modifier = Modifier.width(dimensionResource(R.dimen.content_item_padding_left_right)))
        }

        BasicTextField(
            modifier = innerFinalModifier,
            interactionSource = interactionSource,
            enabled = isEditTextMode,
            readOnly = false,
            singleLine = singleLine,
            value = value,
            onValueChange = {
                onValueChange?.invoke(it)
            },
            textStyle = textStyle,
            maxLines = maxLines,
            minLines = minLines,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            decorationBox = @Composable { innerTextField ->
                DecorationBox(
                    value = value,
                    visualTransformation = visualTransformation,
                    // 单行fillMaxHeight实现垂直居中，多行保持原有样式
                    innerTextField = {
                        val boxModifier = if (singleLine) Modifier.fillMaxHeight() else Modifier
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(boxModifier),
                            contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
                        ) {
                            innerTextField()
                        }
                    },
                    // 占位符同步逻辑，单行居中、多行左上
                    placeholder = {
                        if (value.isNotEmpty()) return@DecorationBox

                        val boxModifier = if (singleLine) Modifier.fillMaxHeight() else Modifier
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(boxModifier),
                            contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
                        ) {
                            placeholder?.invoke() ?: run {
                                if (hintText.isNotEmpty()) {
                                    Text(
                                        text = hintText,
                                        style = textStyle.copy(color = colorResource(R.color.text_hint))
                                    )
                                }
                            }
                        }
                    },
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    prefix = prefix,
                    suffix = suffix,
                    supportingText = supportingText,
                    shape = shape,
                    singleLine = singleLine,
                    enabled = !isEditTextMode,
                    isError = isError,
                    interactionSource = interactionSource,
                    colors = colors,
                    contentPadding = PaddingValues(0.dp),
                )
            }
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 360,
    heightDp = 640
)
@Composable
fun PreviewFTextField() {
    Box(contentAlignment = Alignment.Center) {
        FTextField(
            outerModifier = Modifier
                .border(
                    width = 1.dp,
                    color = Color.Black,
                    shape = RoundedCornerShape(dimensionResource(R.dimen.radius_value))
                ),
            height = 45.dp,
            hintText = "请输入名称",
            isEditTextMode = false,
            singleLine = true,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.mipmap.ic_dropdown_menu),
                    contentDescription = null
                )
            },
            onClick = {
                Log.d("testtt", "点击了")
            })
    }
}