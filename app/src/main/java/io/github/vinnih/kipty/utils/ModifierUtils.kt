package io.github.vinnih.kipty.utils

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dashedBorder(
    strokeWidth: Dp = 4.dp,
    color: Color,
    cornerRadius: Dp = 16.dp,
    dashLength: Dp = 8.dp,
    gapLength: Dp = 4.dp
) = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidth.toPx(),
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx()),
            phase = 0f
        )
    )

    drawRoundRect(
        color = color,
        size = Size(width = size.width, height = size.height),
        cornerRadius = CornerRadius(cornerRadius.toPx()),
        style = stroke
    )
}

fun Modifier.drawIfSelected(selected: Boolean) = this.drawBehind {
    if (!selected) return@drawBehind

    val cornerRadius = 16.dp.toPx()
    val strokeWidth = 4.dp.toPx()

    drawRoundRect(
        color = Color(0xFFFF6B35),
        topLeft = Offset(0f, 0f),
        size = Size(strokeWidth, size.height),
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
}
