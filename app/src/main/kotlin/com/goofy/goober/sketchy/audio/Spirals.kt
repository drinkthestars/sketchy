package com.goofy.goober.sketchy.audio

import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.PI
import com.goofy.goober.sketchy.Sketch
import com.goofy.goober.sketchy.map
import glm_.glm.pow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import android.graphics.Color as AndroidColor

@Composable
fun Spirals(
    modifier: Modifier = Modifier
) {
    val state = rememberVisualizerState(
        smoothingType = SmoothingType.Avg,
        enableFftCapture = true
    )
    val paint = remember {
        Paint().apply {
            strokeCap = Paint.Cap.ROUND
            alpha = 250
        }
    }
    val count = remember { 1300 }
    val baseScalingFactor = remember { 2f }
    val phi = remember { (sqrt(5.0) + 1) / 2 }

    Sketch(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        onDraw = { time ->
            translate(size.width / 2, size.height / 2) {
                val radius = containerDiagonal()
                val scalingFactor = baseScalingFactor * (1 - sin(time / 5 * 2 * PI + state.fftAvg/10) * 0.5f + 0.5f)
                val scale = 1f + (time / 1000f) % 1f // Reset scale smoothly
                scale(scale, scale) {
                    for (i in 0 until count) {
                        val factor = i.toFloat() / count.toFloat()
                        val angle = i.toFloat() / phi.toFloat() + time * 0.2f

                        val pushout = if (state.fftAvg > 3.5f) 30f else 10f
                        val dist = factor * radius * scalingFactor + pushout

                        val x = cos(angle * PI * 2) * dist
                        val y = sin(angle * PI * 2) * dist

                        val push = if (state.fftAvg > 3.5f) 8f else 3f
                        val sig = pow(cosNormalized(factor - time * push), 2f)

                        val hue = map(factor, 0f, 1f, 0f, 360f)
                        val luminosity = if (state.isAmplitudeHigh()) 1f else 0.8f
                        paint.color = AndroidColor.HSVToColor(floatArrayOf(hue, 0.6f, luminosity))
                        paint.strokeWidth = factor * 200f * sig + push + 10f
                        paint.setShadowLayer(pushout + 25f, 0f, 0f, paint.color)
                        drawIntoCanvas {
                            it.nativeCanvas.drawPoint(x, y, paint)
                        }
                    }
                }
            }
        }
    )
}

// normalized cosine function
private fun cosNormalized(x: Float): Float {
    return (cos(x * 2 * PI) * 0.5f + 0.5f)
}

private fun invertedCosNormalized(x: Float): Float {
    return 1 - cosNormalized(x)
}

private fun DrawScope.containerDiagonal(): Float {
    return sqrt(
        pow(size.width / 2, 2f)
                + pow(size.height / 2, 2f)
    )
}