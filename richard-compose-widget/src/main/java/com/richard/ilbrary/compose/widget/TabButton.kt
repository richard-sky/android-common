package com.richard.ilbrary.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.richard.library.compose.widget.R

@Preview(
    showBackground = true,
    widthDp = 640,
    heightDp = 360,
)
@Composable
fun PreviewTabButton() {
    Box(contentAlignment = Alignment.Center) {
        TabButton(itemList = listOf("全部", "待付款", "待收货", "待评价"), selectedIndex = 0) { }
    }
}

/**
 * 自定义单选选项按钮组
 * @param itemList 选项文本列表
 * @param selectedIndex 默认选中下标
 * @param height 按钮高度
 * @param fontSize 字体大小
 * @param onSelected 选中回调：返回选中索引
 */
@Composable
fun TabButton(
    modifier: Modifier = Modifier,
    itemList: List<String>,
    selectedIndex: Int = 0,
    height: Dp = dimensionResource(R.dimen.normal_button_height),
    fontSize: TextUnit = dimensionResource(R.dimen.text_view_textSize).value.sp,
    minTextWidth: Dp = 60.dp,
    radius: Dp = dimensionResource(R.dimen.radius_value),
    selectedTextColor: Color = Color.White,
    unSelectedTextColor: Color = colorResource(R.color.text),
    selectedBgColor: Color = colorResource(R.color.blue),
    unSelectedBgColor: Color = colorResource(R.color.content_bg),
    onSelected: (Int) -> Unit
) {
    var tabSelectedIndex by remember { mutableIntStateOf(selectedIndex) }

    LazyRow(
        modifier = modifier
            .height(height)
            .border(0.4.dp, colorResource(R.color.light_line), RoundedCornerShape(radius))

    ) {
        itemsIndexed(itemList) { index, text ->
            val isSelected = index == tabSelectedIndex
            val cornerShape = when (index) {
                0 -> RoundedCornerShape(topStart = radius, bottomStart = radius)
                itemList.size - 1 -> RoundedCornerShape(topEnd = radius, bottomEnd = radius)
                else -> RoundedCornerShape(0.dp)
            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(cornerShape)
                    .background(if (isSelected) selectedBgColor else unSelectedBgColor)
                    .clickable {
                        tabSelectedIndex = index
                        onSelected(index) // 回调选中索引
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 13.dp)
                        .defaultMinSize(minWidth = minTextWidth),
                    textAlign = TextAlign.Center,
                    text = text,
                    fontSize = fontSize,
                    color = if (isSelected) selectedTextColor else unSelectedTextColor
                )
            }
        }
    }
}