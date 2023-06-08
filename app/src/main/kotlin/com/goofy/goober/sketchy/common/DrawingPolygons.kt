package com.goofy.goober.sketchy.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import com.goofy.goober.sketchy.HALF_PI
import com.goofy.goober.sketchy.TWO_PI
import kotlin.math.cos
import kotlin.math.sin

fun DrawScope.drawPolygon(
    center: Offset,
    vertices: Int,
    radius: Float,
    path: Path,
    color: Color,
    alpha: Float = 1f,
    rotation: Float = 0f,
    closeHalf: Boolean = false,
    style: DrawStyle = Fill,
    colorFilter: ColorFilter? = null,
    blendMode: BlendMode = DrawScope.DefaultBlendMode
) {
    val angleIncrement = TWO_PI / vertices
    val startAngle = rotation - HALF_PI

    path.reset()

    for (i in 0 until vertices) {
        val angle = startAngle + i * angleIncrement
        val x = radius * cos(angle) + center.x
        val y = radius * sin(angle) + center.y

        if (closeHalf && i == (vertices - 1) / 2) {
            path.close()
        } else {
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
    }
    path.close()

    drawPath(
        path = path,
        color = color,
        style = style,
        colorFilter = colorFilter,
        alpha = alpha,
        blendMode = blendMode
    )
}

fun DrawScope.drawTriangle(
    vertex1: Offset,
    vertex2: Offset,
    vertex3: Offset,
    path: Path,
    color: Color,
    style: DrawStyle = Fill,
    alpha: Float = 1f
) {
    path.reset()
    path.moveTo(vertex1.x, vertex1.y)
    path.lineTo(vertex2.x, vertex2.y)
    path.lineTo(vertex3.x, vertex3.y)
    path.close()

    drawPath(
        path = path, color = color, style = style, alpha = alpha
    )
}

fun DrawScope.drawQuad(
    vertex1: Offset,
    vertex2: Offset,
    vertex3: Offset,
    vertex4: Offset,
    path: Path,
    color: Color,
    style: DrawStyle = Fill,
    alpha: Float = 1f
) {
    path.apply {
        reset()
        moveTo(vertex1.x, vertex1.y)
        lineTo(vertex2.x, vertex2.y)
        lineTo(vertex3.x, vertex3.y)
        lineTo(vertex4.x, vertex4.y)
        close()
    }

    drawPath(
        path = path,
        color = color,
        style = style,
        alpha = alpha
    )
}

fun DrawScope.drawParallelogram(
    center: Offset,
    radius: Float,
    skew: Float,
    color: Color,
    style: DrawStyle = Fill,
    alpha: Float = 1f,
    path: Path
) {
    val halfWidth = radius
    val halfHeight = radius * 0.5f

    val topLeftX = center.x - halfWidth
    val topLeft = Offset(topLeftX, center.y - halfHeight)
    val topRight = Offset(topLeftX + 2 * halfWidth, center.y - halfHeight)
    val bottomRight =
        Offset(topLeftX + 2 * halfWidth - skew * 2 * halfHeight, center.y + halfHeight)
    val bottomLeft = Offset(topLeftX - skew * 2 * halfHeight, center.y + halfHeight)

    path.apply {
        reset()
        moveTo(topLeft.x, topLeft.y)
        lineTo(topRight.x, topRight.y)
        lineTo(bottomRight.x, bottomRight.y)
        lineTo(bottomLeft.x, bottomLeft.y)
        close()
    }

    drawPath(
        path = path,
        color = color,
        style = style,
        alpha = alpha
    )
}

data class Triangle(
    val v1Offset: Float,
    val v2Offset: Float,
    val v3Offset: Float,
    val alpha: Float,
    val color: Color,
)
