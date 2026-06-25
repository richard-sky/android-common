package com.richard.ilbrary.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.booleanResource
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.richard.library.compose.widget.R
import com.richard.library.context.util.isNull

/**
 * 通用ComposePage页面脚手架
 * @param modifier 脚手架样式修饰
 * @param titleText 标题文本(titleText 和 title 二选一，优先title)
 * @param title 标题组件(titleText 和 title 二选一，优先title)
 * @param navLeft 导航条左边组件
 * @param navRight 导航条右边组件
 * @param backEvent 返回页面时事件
 * @param navController NavController
 * @param content 主内容
 */
@Composable
fun PageScaffold(
    modifier: Modifier = Modifier,
    titleText: String? = null,
    title: (@Composable BoxScope.() -> Unit)? = null,
    navLeft: (@Composable RowScope.() -> Unit)? = null,
    navRight: (@Composable RowScope.() -> Unit)? = null,
    backEvent: (() -> Unit)? = null,
    navController: NavController? = null,
    content: @Composable ColumnScope.() -> Unit
) {

    var finalNavLeft = navLeft
    if (finalNavLeft.isNull()) {
        finalNavLeft = {
            val iconTint = colorResource(R.color.navigation_left_image_tint)
            FText(
                iconId = R.mipmap.icon_page_scaffold_nav_back,
                iconTint = if (iconTint.value == Color.Transparent.value) null else iconTint,
                text = "",
                color = colorResource(R.color.navigation_text_color),
            )
        }
    }

    var finalTitle = title
    if (finalTitle.isNull()) {
        finalTitle = {
            FText(
                text = titleText ?: "",
                fontSize = dimensionResource(R.dimen.navigation_bar_title_text_size).value.sp,
                color = colorResource(R.color.navigation_text_color),
                fontWeight = if (booleanResource(R.bool.navigation_bar_title_bold)) FontWeight.Bold else FontWeight.Normal
            )
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.navigation_bar_height))
                .background(color = colorResource(R.color.navigation_bar_backcolor)),
        ) {

            //左边
            Row(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = {
                            backEvent?.invoke()
                            navController?.popBackStack()
                        }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.content_padding)))
                finalNavLeft.invoke(this)
            }

            //中间标题
            Box(
                modifier = Modifier
                    .weight(2.5F)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                finalTitle.invoke(this)
            }

            //右边
            Row(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                navRight?.invoke(this)
            }
        }

        if (integerResource(R.integer.navigation_bar_bottom_line_show) == 0) {
            Spacer(
                modifier = Modifier
                    .height(dimensionResource(R.dimen.navigation_bottom_line_size))
                    .background(color = colorResource(R.color.navigation_bottom_line_color))
            )
        }

        content.invoke(this)
    }

}