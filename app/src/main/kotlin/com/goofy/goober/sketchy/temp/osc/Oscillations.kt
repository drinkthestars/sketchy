package com.goofy.goober.sketchy.temp.osc

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import com.goofy.goober.sketchy.HALF_PI
import com.goofy.goober.sketchy.Sketch
import com.goofy.goober.sketchy.StatefulSketch
import com.goofy.goober.sketchy.TWO_PI
import com.goofy.goober.sketchy.common.primarySketchColor
import com.goofy.goober.sketchy.map
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ParametricHarmonic(modifier: Modifier = Modifier) {
    val color = primarySketchColor()
    Box(
        modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        StatefulSketch(
            speed = 1f,
            modifier = Modifier.fillMaxSize(0.9f)
        ) { time ->
            translate(size.width / 2f, size.height / 2f) {
                drawCircle(
                    color = color,
                    center = Offset(
                        sin(time * 40f) * 100f + sin(time * 60f) * 150f,
                        cos(time * 40f) * 100f
                    ),
                    radius = 5f,
                    alpha = 0.4f
                )
            }
        }
    }
}

@Composable
fun Harmonic() {
    val path = remember { Path() }
    val color = primarySketchColor()
    Sketch(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f),
        speed = 4f,
        showControls = true,
        onDraw = { value ->
            val (width, height) = size
            translate((width / 2f), map(sin(value), -1f, 1f, 0f, height)) {
                path.reset()
                for (i in 0 until 360) {
                    val rad = i * PI.toFloat() / 180f
                    val r = map(sin(value), -1f, 1f, 20f, 200f)
                    val x = r * cos(rad)
                    val y = r * sin(rad)
                    when (i) {
                        0 -> path.moveTo(x, y)
                        else -> path.lineTo(x, y)
                    }
                }
                path.close()
                drawPath(path, color = color, style = Stroke(2f), alpha = 0.5f)
            }
        }
    )
}

private fun DrawScope.circle(
    center: Offset,
    nbPoints: Int,
    r: Float,
    radiusMax: Float,
    radiusMin: Float,
    path: Path,
    color: Color
) {
    for (i in 0 until nbPoints) {
        val rad = -HALF_PI + i.toFloat() * TWO_PI / nbPoints.toFloat()
        val x = r * cos(rad) + center.x
        val y = r * sin(rad) + center.y
        when (i) {
            0 -> path.moveTo(x, y)
            else -> path.lineTo(x, y)
        }
    }
    path.close()
    val style = Stroke(
        width = map(
            value = r,
            sourceMin = radiusMin,
            sourceMax = radiusMax,
            destMin = 16f,
            destMax = 5f
        )
    )
    val alpha = map(
        value = r,
        sourceMin = radiusMin,
        sourceMax = radiusMax,
        destMin = 0.7f,
        destMax = 0.4f
    )
    drawPath(
        path, color = color, style = style, alpha = alpha
    )
}
