package com.goofy.goober.sketchy.audio

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.Sketch
import glm_.func.common.abs

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
//            drawFftAvg(state)
            drawHistogram(state)
        }
    )
}

fun DrawScope.drawFftAvg(
    state: VisualizerState
) {
    println("state.fftAvg: ${state.fftAvg}")
    drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.White),
            start = Offset(size.width/2f, size.height/2f),
            end = Offset(size.width/2f, size.height/2f + 100)
        ),
        alpha = if (state.fftAvg > 2.5f) 0.1f else 0.4f,
        style = Fill,
        colorFilter = null,
        blendMode = BlendMode.SrcOver
    )
}

fun DrawScope.drawHistogram(state: VisualizerState) {
    val fftBands = state.fftBands
    val barSpacing = 5f
    val barWidth = (size.width / fftBands.size) - barSpacing
    val maxBarHeight = size.height / 2  // Maximum height a bar can reach
    val barHeightOffset = 100f
    // Create a fixed vertical gradient brush
    val brush = Brush.verticalGradient(
        colors = listOf(Color.Red, Color(0xFFFFA500), Color.Yellow, Color.Green),
        startY = size.height - maxBarHeight + 150, // Start from the bottom of the maximum bar height
        endY = size.height // End at the bottom of the canvas
    )

    for (i in fftBands.indices) {
        // normalize magnitude to [0, 1]
        val normalizedMagnitude = fftBands[i].abs / 179.6f

        val heightOffset = if (state.fftAvg > 2.5f) 10f else 15f
        val barHeight =  normalizedMagnitude * maxBarHeight + barHeightOffset + heightOffset

        // Calculate the top left position and size of each bar
        val offsetX = (i * barWidth) + (i * barSpacing)
        val offsetY = size.height - barHeight
        val topLeft = Offset(x = offsetX, y = offsetY)
        val barSize = Size(width = barWidth, height = barHeight)

        // Draw the bar
        drawRect(
            brush = brush,
            topLeft = topLeft,
            size = barSize,
            alpha = if (state.fftAvg > 2.5f) 1f else 0.5f,
            style = Fill,
            colorFilter = null,
            blendMode = BlendMode.SrcOver
        )
    }
}


