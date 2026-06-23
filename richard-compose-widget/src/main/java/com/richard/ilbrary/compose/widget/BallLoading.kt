package com.richard.ilbrary.compose.widget

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun BallLoading(modifier: Modifier = Modifier.size(100.dp)) {
    val infiniteTransition = rememberInfiniteTransition(label = "balloon_bounce")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "progress"
    )

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val centerX = canvasWidth / 2f
        val baseRadius = minOf(canvasWidth, canvasHeight) / 7f
        val bounceHeight = baseRadius * 2.8f
        val groundY = canvasHeight * 0.78f

        // 弹跳相位 0 → π
        val angle = progress * PI.toFloat()
        val normalizedHeight = sin(angle)  // 0→1→0
        val ballCenterY = groundY - bounceHeight * normalizedHeight

        // 形变因子：cos(angle)：最高点=1，触地=0，再最高点=-1
        val squashFactor = cos(angle)
        val scaleX = 1f + 0.38f * (1f - abs(squashFactor))
        val scaleY = 1f - 0.42f * (1f - abs(squashFactor))

        val radiusX = baseRadius * scaleX
        val radiusY = baseRadius * scaleY

        // 地面阴影
        val shadowAlpha = 0.22f * (1f - normalizedHeight)
        drawOval(
            color = Color.Black.copy(alpha = shadowAlpha),
            topLeft = Offset(centerX - baseRadius * 0.80f, groundY + 4f),
            size = Size(baseRadius * 1.62f, baseRadius * 0.27f)
        )

        // 绘制气球
        drawBalloon(
            center = Offset(centerX, ballCenterY),
            radiusX = radiusX,
            radiusY = radiusY
        )
    }
}

private fun DrawScope.drawBalloon(
    center: Offset,
    radiusX: Float,
    radiusY: Float
) {
    val balloonColor = Color(0xFFFF4D4D)
    val highlightColor = Color.White.copy(alpha = 0.48f)

    // 1. 气球主体
    drawOval(
        color = balloonColor,
        topLeft = Offset(center.x - radiusX, center.y - radiusY),
        size = Size(radiusX * 2, radiusY * 2)
    )

    // 2. 高光
    val highlightW = radiusX * 0.34f
    val highlightH = radiusY * 0.26f
    drawOval(
        color = highlightColor,
        topLeft = Offset(center.x - radiusX * 0.58f, center.y - radiusY * 0.67f),
        size = Size(highlightW, highlightH)
    )

    // 3. 底部小尾巴（三角形）
    val tailPath = Path().apply {
        moveTo(center.x - radiusX * 0.16f, center.y + radiusY * 0.94f)
        lineTo(center.x + radiusX * 0.13f, center.y + radiusY * 0.96f)
        lineTo(center.x, center.y + radiusY * 1.17f)
        close()
    }
    drawPath(tailPath, color = balloonColor)

    // 4. 绳子（缩短70% → 原长的30%）
    val ropeStart = Offset(center.x, center.y + radiusY * 1.03f)
    // 原终点偏移: (radiusX*0.23, radiusY*0.49)，缩短后偏移乘0.3
    val ropeEnd = Offset(
        center.x + radiusX * 0.069f,      // 0.23 * 0.3 ≈ 0.069
        center.y + radiusY * 1.177f       // 1.03 + 0.49 * 0.3 = 1.03 + 0.147 = 1.177
    )
    // 原控制点偏移: (radiusX*0.09, radiusY*0.30)，缩短后偏移乘0.3
    val controlPoint = Offset(
        center.x + radiusX * 0.027f,      // 0.09 * 0.3 ≈ 0.027
        center.y + radiusY * 1.123f       // 1.03 + 0.093 = 1.123
    )

    val ropePath = Path().apply {
        moveTo(ropeStart.x, ropeStart.y)
        quadraticTo(
            controlPoint.x, controlPoint.y,
            ropeEnd.x, ropeEnd.y
        )
    }
    drawPath(
        path = ropePath,
        color = Color(0xFF555555),
        style = Stroke(width = radiusX * 0.065f) // 绳子粗细也略微调细以适应缩短
    )

    // 绳结（位置跟随绳子末端）
    drawCircle(
        color = Color(0xFF444444),
        radius = radiusX * 0.035f,
        center = ropeEnd
    )
}