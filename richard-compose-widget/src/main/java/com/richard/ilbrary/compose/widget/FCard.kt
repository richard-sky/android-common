package com.richard.ilbrary.compose.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import com.richard.library.context.R

/**
 * 具有根据默认自定义配置的样式的卡片
 */
@Composable
fun FCard(
    modifier: Modifier = Modifier,
    colors: CardColors = CardDefaults.cardColors(containerColor = colorResource(R.color.content_bg)),
    elevation: CardElevation = CardDefaults.cardElevation(
        defaultElevation = dimensionResource(R.dimen.big_radius_value),
        pressedElevation = dimensionResource(R.dimen.big_radius_value)
    ),
    border: BorderStroke? = null,
    shape: Shape = RoundedCornerShape(dimensionResource(R.dimen.big_radius_value)),
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier,
        elevation = elevation,
        shape = shape,
        colors = colors,
        border = border,
        content = content
    )
}