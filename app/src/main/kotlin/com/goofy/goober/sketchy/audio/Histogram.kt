package com.goofy.goober.sketchy.audio

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.Sketch

@Composable
fun Histogram(
    modifier: Modifier = Modifier
) {
    val state = rememberVisualizerState(
        smoothingType = SmoothingType.None,
        enableFftCapture = true
    )

    Sketch(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        onDraw = {
            drawHistogram(state.fftBands)
        }
    )
}

fun DrawScope.drawHistogram(magnitude: FloatArray) {
    val barSpacing = 5f
    val barWidth = (size.width / magnitude.size) - barSpacing
    val maxBarHeight = size.height / 2  // Maximum height a bar can reach
    val barHeightOffset = 100f

    for (i in magnitude.indices) {
        // normalize magnitude to [0, 1]
        val normalizedMagnitude = magnitude[i] / Byte.MAX_VALUE
        val barHeight =  normalizedMagnitude * maxBarHeight + barHeightOffset

        // Calculate the top left position and size of each bar
        val offsetX = (i * barWidth) + (i * barSpacing)
        val offsetY = size.height - barHeight
        val topLeft = Offset(x = offsetX, y = offsetY)
        val barSize = Size(width = barWidth, height = barHeight)

        // make hue red to yellow based on x position
        val hue = 100 * (i / magnitude.size.toFloat())
        val color =  Color.hsv(hue, 1f, 1f)

        // Draw the bar
        drawRect(
            color = color,
            topLeft = topLeft,
            size = barSize,
            alpha = 1.0f,  // Full opacity
            style = Fill,
            colorFilter = null,
            blendMode = BlendMode.SrcOver
        )
    }
}


