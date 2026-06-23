package com.richard.ilbrary.compose.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.richard.ilbrary.compose.widget.type.Direction
import com.richard.library.compose.widget.R
import com.richard.library.context.util.isNull

/**
 * @author: Richard
 * @createDate: 2026/6/9 11:02
 * @version: 1.0
 * @description: 渐变色按钮
 */
@Preview(
    showBackground = true,
    widthDp = 640,
    heightDp = 360
)
@Composable
fun PreviewGradientButtonUI() {
    Box(contentAlignment = Alignment.Center) {
        GradientButton(
            text = "登录",
            iconId = android.R.drawable.star_on,
            iconDirection = Direction.TOP,
            onClick = {})
    }
}

/**
 * 渐变色按钮
 *
 * 支持：正常 / 按下 / 禁用 渐变
 * @param modifier 修改按钮样式
 * @param text 按钮文字
 * @param enabled 启用按钮
 * @param width 按钮宽度
 * @param height 按钮高度
 * @param horizontalPadding 按钮左右内边距
 * @param shape 按钮形状
 * @param normalGradient 正常状态渐变
 * @param pressedGradient 按下状态渐变
 * @param textSize 按钮文字大小
 * @param textColor 按钮文字颜色
 * @param iconId 按钮图标
 * @param iconDirection 图标方向
 * @param iconPadding 图标间距
 * @param iconDirection 图标方向
 * @param isOutlinedButton 是否属于外边框样式按钮
 * @param onClick 按钮点击事件
 */
@Composable
fun GradientButton(
    modifier: Modifier? = null,
    text: String,
    enabled: Boolean = true,
    width: Dp? = null,
    height: Dp = dimensionResource(R.dimen.normal_button_height),
    horizontalPadding: Dp = 24.dp,
    shape: Shape = RoundedCornerShape(dimensionResource(R.dimen.radius_value)),
    normalGradient: List<Color> = listOf(
        colorResource(R.color.button_un_press_bg),
        colorResource(R.color.button_pressed_bg)
    ),
    pressedGradient: List<Color> = listOf(
        colorResource(R.color.button_un_press_bg),
        colorResource(R.color.button_pressed_bg)
    ),
    disabledGradient: List<Color> = listOf(
        colorResource(R.color.button_disabled_bg),
        colorResource(R.color.button_disabled_bg)
    ),
    textSize: TextUnit = dimensionResource(R.dimen.button_text_size).value.sp,
    textColor: Color = colorResource(R.color.button_text),
    iconId: Int? = null,
    iconSize: Dp? = null,
    iconPadding: Dp = dimensionResource(R.dimen.drawable_padding),
    iconDirection: Direction = Direction.LEFT,
    isOutlinedButton: Boolean = false,
    onClick: () -> Unit
) {
    // 监听按压状态
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 按下缩放动画
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.98f else 1f,
        animationSpec = spring(stiffness = 800f),
        label = "scale"
    )

    // 选择当前状态渐变
    val currentGradient = when {
        !enabled -> disabledGradient
        isPressed -> pressedGradient
        else -> normalGradient
    }

    val textColor by animateColorAsState(
        targetValue = textColor,
        animationSpec = spring(stiffness = 800f),
        label = "textColor"
    )

    var finalModifier = (modifier?: Modifier)
        .height(height = height)
        .graphicsLayer(scaleX = scale, scaleY = scale)

    if (width != null) {
        finalModifier = finalModifier.width(width)
    }

    if (!isOutlinedButton) {
        finalModifier = finalModifier
            .background(
                brush = Brush.verticalGradient(currentGradient),
                shape = shape
            )
    }

    val buttonContent: @Composable RowScope.() -> Unit = {
        if (iconId == null) {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = textSize,
                    color = textColor,
                    fontWeight = FontWeight.Medium
                )
            )
        } else {
            when (iconDirection) {
                Direction.LEFT -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                            style = TextStyle(
                                fontSize = textSize,
                                color = textColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Direction.RIGHT -> {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = text,
                            style = TextStyle(
                                fontSize = textSize,
                                color = textColor,
                                fontWeight = FontWeight.Medium
                            )
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                            style = TextStyle(
                                fontSize = textSize,
                                color = textColor,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                Direction.BOTTOM -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = text,
                            style = TextStyle(
                                fontSize = textSize,
                                color = textColor,
                                fontWeight = FontWeight.Medium
                            )
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
    }

    Box(modifier = finalModifier) {
        if (isOutlinedButton) {
            OutlinedButton(
                onClick = onClick,
                enabled = enabled,
                modifier = if (width.isNull() && modifier.isNull()) Modifier.fillMaxHeight() else Modifier.fillMaxSize(),
                interactionSource = interactionSource,
                shape = shape,
                // 把 Button 默认背景设为透明
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                border = BorderStroke(
                    width = dimensionResource(R.dimen.line_size),
                    color = currentGradient[0]
                ),
                contentPadding = PaddingValues(
                    start = horizontalPadding,
                    top = 0.dp,
                    end = horizontalPadding,
                    bottom = 0.dp,
                ),
                content = buttonContent
            )
        } else {
            Button(
                onClick = onClick,
                enabled = enabled,
                modifier = if (width.isNull() && modifier.isNull()) Modifier.fillMaxHeight() else Modifier.fillMaxSize(),
                interactionSource = interactionSource,
                shape = shape,
                // 把 Button 默认背景设为透明
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(
                    start = horizontalPadding,
                    top = 0.dp,
                    end = horizontalPadding,
                    bottom = 0.dp,
                ),
                content = buttonContent
            )
        }
    }
}