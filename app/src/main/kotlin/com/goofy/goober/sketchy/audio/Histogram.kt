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

fun DrawScope.drawHistogram(state: VisualizerState) {

    println("WARP - average fft: ${state.fftAvg}")

    val fftBands = state.fftBands
    val barSpacing = 5f
    val segmentHeight = 20f  // Height of each segment
    val maxBarHeight = size.height * 0.75  // Maximum height a bar can reach
    val numSegments = (maxBarHeight / (segmentHeight + barSpacing)).toInt()

    // Calculate the width of each segment
    val segmentWidth = (size.width - (fftBands.size - 1) * barSpacing) / fftBands.size
    val barSize = Size(width = segmentWidth, height = segmentHeight)

    val colors = listOf(Color.Green, Color.Yellow, Color(0xFFFFA500), Color.Red)

    for (i in fftBands.indices) {
        val normalizedMagnitude = fftBands[i].abs/ 100f
        val activeSegments = (normalizedMagnitude * numSegments).toInt()

        val offsetX = i * (segmentWidth + barSpacing)
        var offsetY = size.height - segmentHeight

        for (segment in 0 until numSegments) {
            val topLeft = Offset(x = offsetX, y = offsetY - (segment * (segmentHeight + barSpacing)))

            // Determine the color based on the segment position
            val colorIndex = (segment * colors.size) / numSegments
            val color = colors.getOrElse(colorIndex) { Color.Red }

            // Determine the alpha value
            val alpha = if (segment < activeSegments) 1f else 0.1f

            // Draw the segment
            drawRect(
                color = color.copy(alpha = alpha),
                topLeft = topLeft,
                size = barSize,
                style = Fill
            )
        }
    }

}



