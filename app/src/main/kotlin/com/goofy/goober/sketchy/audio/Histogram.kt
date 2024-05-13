package com.goofy.goober.sketchy.audio

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import com.goofy.goober.sketchy.Sketch
import kotlin.math.sqrt

@Composable
fun Histogram(
    modifier: Modifier = Modifier
) {
    val vizState = remember {
        VisualizerState()
    }
    val visualizer = rememberVisualizer(vizState)

    Sketch(
        showControls = true,
        modifier = modifier.fillMaxSize(),
        onDraw = {time ->
//            drawHistogram(vizState.fftState.value)
            drawFrequencyBandsHistogram(vizState.frequencyBands.value)
        }
    )
}


fun DrawScope.drawHistogram(fft: ByteArray) {
    val barWidth = size.width / (fft.size / 2)  // Assuming fft.size is even and data is complex (real + imaginary)
    val barColor = Color.Blue
    val maxBarHeight = size.height / 2  // Maximum height a bar can reach

    for (i in 0 until fft.size / 2 step 2) {
        val real = fft[i].toInt()
        val imaginary = fft[i + 1].toInt()
        val magnitude = sqrt((real * real + imaginary * imaginary).toFloat())
        val normalizedMagnitude = magnitude / Byte.MAX_VALUE  // Normalize magnitude to [0, 1]
        val barHeight = normalizedMagnitude * maxBarHeight  // Scale height to canvas

        // Calculate the top left position and size of each bar
        val topLeft = Offset(x = i / 2 * barWidth, y = size.height - barHeight)
        val barSize = Size(width = barWidth.toFloat(), height = barHeight)

        // Draw the bar
        drawRect(
            color = barColor,
            topLeft = topLeft,
            size = barSize,
            alpha = 1.0f,  // Full opacity
            style = Fill,
            colorFilter = null,
            blendMode = BlendMode.SrcOver
        )
    }
}
fun DrawScope.drawFrequencyBandsHistogram(bands: FloatArray) {
    val barWidth = size.width / bands.size
    val barColor = Color.Blue
    val maxBarHeight = size.height

    for (i in 1 until bands.size) {
        val barHeight = (bands[i] / bands.maxOrNull()!!) * maxBarHeight
        val topLeft = Offset(x = i * barWidth, y = maxBarHeight - barHeight)
        val barSize = Size(width = barWidth, height = barHeight)

        drawRect(
            color = barColor,
            topLeft = topLeft,
            size = barSize,
            alpha = 1.0f,
            style = Fill
        )
    }
}

