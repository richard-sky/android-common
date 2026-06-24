package com.richard.ilbrary.compose.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.richard.library.compose.widget.R

/**
 * 泛型标签下拉选择控件
 * @param modifier 控件修饰
 * @param buttonHeight 按钮高度(含按钮，输入框，下拉菜单项的高度)
 * @param isEditTextMode true=输入框模式(仅点箭头展开) false=按钮模式(整区域展开)
 * @param labelText 左侧标签文字
 * @param hintText 输入框提示文字
 * @param options 泛型选项列表
 * @param selectedIndex 默认选择的选项索引
 * @param cornerRadius 整体圆角
 * @param borderColor 边框颜色
 * @param dropdownWidth 下拉列表宽度控制：null = 默认与上方控件同宽；传入Dp值 = 下拉使用指定固定宽度
 * @param dropdownMenuMaxHeight 下拉列表最大高度
 * @param onValueChange 输入框内容改变回调，返回输入框内容
 * @param displayText 泛型对象转为展示文本
 * @param onOptionSelect 选中选项回调，返回泛型对象
 */
@Composable
fun <T> DropdownMenu(
    modifier: Modifier = Modifier.fillMaxWidth(),
    buttonHeight: Dp = dimensionResource(R.dimen.normal_button_height),
    isEditTextMode: Boolean = false,
    labelText: String? = null,
    hintText: String = if (isEditTextMode) "请输入或选择" else "请选择",
    options: List<T>,
    selectedIndex: Int? = null,
    cornerRadius: Dp = dimensionResource(R.dimen.radius_value),
    borderColor: Color = colorResource(R.color.light_line),
    dropdownWidth: Dp? = null,
    dropdownMenuMaxHeight: Dp = 200.dp,
    onValueChange: ((String) -> Unit) = {},
    displayText: ((T) -> String) = { it.toString() },
    onOptionSelect: (T) -> Unit,
) {
    var selectedText by remember {
        mutableStateOf(
            if (selectedIndex != null) {
                displayText(options[selectedIndex])
            } else {
                ""
            }
        )
    }
    var expanded by remember { mutableStateOf(false) }
    var controlPxWidth by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()


    Row(
        modifier = Modifier
            .height(buttonHeight)
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (!labelText.isNullOrEmpty()) {
            FText(text = labelText)
        }

        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.content_item_padding_left_right)))

        Row(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1F)
                .border(
                    border = BorderStroke(
                        width = dimensionResource(R.dimen.line_size),
                        color = borderColor
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .onGloballyPositioned { layoutInfo ->
                    controlPxWidth = layoutInfo.size.width.toFloat()
                }
        ) {


            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(horizontal = dimensionResource(R.dimen.content_item_padding_left_right))
                    .clickable(
                        enabled = !isEditTextMode,
                        indication = null,// 去掉水波纹
                        interactionSource = null
                    ) {
                        expanded = !expanded
                    },
            ) {
                BasicTextField(
                    enabled = isEditTextMode,
                    readOnly = false,
                    singleLine = true,
                    value = selectedText,
                    onValueChange = {
                        selectedText = it
                        onValueChange.invoke(it)
                    },
                    textStyle = TextStyle(
                        fontSize = dimensionResource(R.dimen.edit_text_view_textSize).value.sp,
                        color = colorResource(R.color.text),
                        lineHeight = buttonHeight.value.sp,
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterStart)
                        .focusable(
                            enabled = isEditTextMode,
                            interactionSource = if (isEditTextMode) interactionSource else null
                        ),
                    decorationBox = { innerTextField ->
                        if (selectedText.isEmpty() && !isFocused) {
                            FText(
                                text = hintText,
                                color = colorResource(R.color.text_hint),
                                style = TextStyle(lineHeight = buttonHeight.value.sp)
                            )
                        }
                        innerTextField()
                    }
                )
            }

            Image(
                painter = painterResource(if (expanded) R.mipmap.ic_dropdown_menu_up else R.mipmap.ic_dropdown_menu_down),
                contentDescription = null,
                contentScale = ContentScale.Inside,
                modifier = Modifier
                    .width(buttonHeight)
                    .fillMaxHeight()
                    .clickable { expanded = !expanded }
            )

            // 计算最终下拉宽度
            val finalDropdownWidth = dropdownWidth ?: with(density) { controlPxWidth.toDp() }

            // 下拉菜单
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                offset = DpOffset(0.dp, 2.dp),
                modifier = Modifier
                    .background(color = colorResource(R.color.content_bg))
                    .width(finalDropdownWidth)
                    .verticalScroll(rememberScrollState())
                    .heightIn(max = dropdownMenuMaxHeight)
            ) {
                options.forEach { item ->
                    DropdownMenuItem(
                        modifier = Modifier.height(buttonHeight),
                        text = { FText(text = displayText(item)) },
                        onClick = {
                            expanded = false
                            selectedText = displayText(item)
                            onOptionSelect(item)
                        }
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 640,
    heightDp = 360
)
@Composable
fun PreviewDropdownMenu() {
    Box(contentAlignment = Alignment.Center) {
        DropdownMenu(
            labelText = "请选择",
            options = listOf(
                "选项1",
                "选项2",
                "选项3",
                "选项4",
                "选项5",
                "选项6",
                "选项7",
                "选项8",
                "选项9",
                "选项10"
            ),
            selectedIndex = null,
            onValueChange = {},
            onOptionSelect = {},
            isEditTextMode = false,
            cornerRadius = 6.dp,
            borderColor = Color(0xFFDDDDDD),
            displayText = { it },
        )
    }
}