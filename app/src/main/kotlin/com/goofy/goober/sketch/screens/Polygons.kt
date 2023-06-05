package com.goofy.goober.sketch.screens

import android.graphics.RenderEffect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goofy.goober.shaders.MarbledTexture
import com.goofy.goober.sketch.CanvasRecorder
import com.goofy.goober.sketch.CapturableContainer
import com.goofy.goober.sketch.HALF_PI
import com.goofy.goober.sketch.PI
import com.goofy.goober.sketch.TWO_PI
import com.goofy.goober.sketch.ui.Silvers
import com.goofy.goober.sketch.ui.Palette2
import com.goofy.goober.sketch.ui.Golds
import kotlin.math.cos
import kotlin.math.sin

private val Padding = 32.dp
private val Rad360 = 2f * PI / 360f

@Preview(showBackground = true)
@Composable
fun Polygons(modifier: Modifier = Modifier) {
    val path = remember { Path() }
    val nbPoints = remember { 6 }
    val nbForms = remember { 3 }
    val numColors = remember { 6 }

    var colorQuad by remember { mutableStateOf(Palette2.shuffled().take(numColors)) }

    CapturableContainer(
        modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp)
    ) { onGloballyPositioned ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Color.Transparent)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Padding),
            onDraw = {
                val (width, height) = size

                // ONE
                path.reset()
                drawPolygon(
                    color = colorQuad[0],
                    sides = nbPoints,
                    radius = width * 0.6f,
                    center = this.center,
                    rotation = PI,
                    path = path,
                )
                path.reset()

                // TWO
                drawPolygon(
                    color = colorQuad[1],
                    sides = nbPoints,
                    radius = width * 0.4f,
                    center = this.center,
                    rotation = PI,
                    path = path,
                )

                // THREE
                path.reset()
                drawPolygon(
                    color = colorQuad[2],
                    sides = nbPoints,
                    radius = width * 0.3f,
                    center = this.center,
                    rotation = PI,
                    path = path,
                )
                // Hexes
                path.reset()
                drawPolygon(
                    color = colorQuad[3],
                    sides = nbPoints,
                    radius = width * 0.2f,
                    center = this.center,
                    rotation = PI,
                    path = path,
                )
                path.reset()
                drawPolygon(
                    color = colorQuad[2],
                    sides = 3,
                    radius = width * 0.21f,
                    center = this.center,
                    rotation = PI,
                    path = path,
                    closeHalf = true
                )
//                // FOUR - Triangle
//                path.reset()
//                drawQuad(
//                    color = colorQuad[3],
//                    sides =  nbPoints / 2,
//                    radius = width * 0.3f,
//                    center = this.center,
//                    rotation = map(
//                        sin(time), -1f, 1f, 0f, TWO_PI
//                    ),
//                    path = path,
//                )
//
//                // FIVE - Triangle
//                path.reset()
//                drawQuad(
//                    color = colorQuad[4],
//                    sides =  nbPoints / 2,
//                    radius = width * 0.15f,
//                    center = this.center,
//                    rotation = TWO_PI,
//                    path = path,
//                )

//                // SIX
//                path.reset()
//                drawQuad(
//                    color = colorQuad[5],
//                    sides =  nbPoints,
//                    radius = width * 0.15f/2,
//                    center = this.center,
//                    rotation = glm.PIf/3,
//                    path = path,
//                )

                // LINE
                val topLeft1 = Offset(
                    center.x - 4f,
                    center.y - width * 0.6f
                )
                drawRect(
                    brush = Brush.linearGradient(Golds),
                    topLeft = topLeft1,
                    size = Size(8f, width * 0.3f)
                )

                val topLeft2 = Offset(
                    center.x - 4f,
                    center.y + width * 0.3f
                )
                drawRect(
                    brush = Brush.linearGradient(Silvers),
                    topLeft = topLeft2,
                    size = Size(8f, width * 0.3f)
                )
            }
        )
    }
}

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
fun DrawScope.drawPolygon(
    center: Offset,
    sides: Int,
    radius: Float,
    path: Path,
    color: Color,
    rotation: Float = 0f,
    alpha: Float = 1f,
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
