package com.goofy.goober.sketch.drawing

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import com.goofy.goober.sketch.HALF_PI
import com.goofy.goober.sketch.TWO_PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws a polygon with the specified parameters.
 *
 * @param center The center point of the polygon.
 * @param rotation The rotation of the polygon in degrees.
 * @param sides The number of sides of the polygon.
 * @param radius The radius of the polygon.
 * @param path The Path object to draw the polygon on.
 * @param color The color of the polygon.
 * @param closeHalf Whether to close half of the polygon.
 * @param style The drawing style of the polygon.
 */
fun DrawScope.drawQuad(
    center: Offset,
    sides: Int,
    radius: Float,
    path: Path,
    color: Color,
    rotation: Float = 0f,
    closeHalf: Boolean = false,
    style: DrawStyle = Fill
) {
    val angleIncrement = TWO_PI / sides
    val startAngle = rotation - HALF_PI

    path.reset()

    for (i in 0 until sides) {
        val angle = startAngle + i * angleIncrement
        val x = radius * cos(angle) + center.x
        val y = radius * sin(angle) + center.y

        if (closeHalf && i == (sides - 1) / 2) {
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
        path = path, color = color, style = style
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


/**
 * Draws a quadrilateral shape on the canvas.
 *
 * @param vertex1 The first vertex of the quadrilateral.
 * @param vertex2 The second vertex of the quadrilateral.
 * @param vertex3 The third vertex of the quadrilateral.
 * @param vertex4 The fourth vertex of the quadrilateral.
 * @param path The path object used to construct the quadrilateral.
 * @param color The color of the quadrilateral.
 * @param style The drawing style, which is optional and defaults to `Fill`.
 * @param alpha The transparency of the quadrilateral, ranging from 0.0 (fully transparent) to 1.0 (fully opaque).
 */
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

/**
 * Draws a parallelogram shape on the canvas.
 *
 * @param topLeft The top-left offset of the parallelogram.
 * @param size The size of the parallelogram (width and height).
 * @param skew The skew factor to introduce the skew effect to the parallelogram.
 * @param color The color of the parallelogram.
 * @param style The drawing style, which is optional and defaults to `Fill`.
 */
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
    val bottomRight = Offset(topLeftX + 2 * halfWidth - skew * 2 * halfHeight, center.y + halfHeight)
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
