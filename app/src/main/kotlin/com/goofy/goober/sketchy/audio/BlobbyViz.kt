package com.goofy.goober.sketchy.audio

import android.media.audiofx.Visualizer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.tooling.preview.Preview
import com.goofy.goober.sketchy.Sketch
import com.goofy.goober.sketchy.lerp
import com.goofy.goober.sketchy.map
import glm_.glm
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.sin

data class MaxNoise(var value: Float = 0f)

/**
 * Perfectly looped Perlin noise needs to be 2D perlin noise
 * but we only have 1D perlin noise in [Blobby]
 **/
@Preview(showBackground = true)
@Composable
fun BlobbyViz() {
    val path = remember { Path() }
    val stroke = remember { Stroke(1f) }
    val toRad = remember { 2 * PI.toFloat() / 360f }
    val peak = remember { Visualizer.MeasurementPeakRms() }
    val state = rememberVisualizerState(
        smoothingType = SmoothingType.None,
        enableFftCapture = true
    )
    val color = MaterialTheme.colorScheme.primary

    LaunchedEffect(peak) {
        while (isActive) {
            delay(100)
            state.visualizer.getMeasurementPeakRms(peak)
        }
    }

    Sketch(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f),
        onDraw = { time ->
            path.reset()
            val (width, height) = size
            translate((width / 2f), (height / 2f)) {
                var xOffset: Float
                var yOffset: Float
                val maxNoise = state.rawFFt.average().absoluteValue.toFloat() * 30f
                for (i in 0 until 360) {
                    val rad = i * toRad + time * 0.005f
//                    xOffset = map(cos(rad + time * 10f), -1f, 1f, 0f, 5f + (time * 10f))
//                    yOffset = map(sin(rad + time * 3f), -1f, 1f, 0f, 5f + (time * 10f))
                    xOffset = map(cos(rad + time * 10f), -1f, 1f, 0f, maxNoise)
                    yOffset = map(sin(rad + time * 3f), -1f, 1f, 0f, maxNoise)
                    val noise = glm.perlin(Vec3(x = xOffset, y = yOffset, z = time * 0.7f))
                    val r = map(noise, 0f, 1f, -30f, 30f) + (peak.mRms * -1f / 10f) + 100f
                    val x = r * cos(rad)
                    val y = r * sin(rad)
                    when (i) {
                        0 -> path.moveTo(x, y)
                        else -> path.lineTo(x, y)
                    }
                }
                path.close()
                drawPath(path, color = color, style = stroke)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun Blobby() {
    Sketch(
        modifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f),
        speed = 4f,
        showControls = true,
        onDraw = { value ->
            val (width, height) = size
            translate((width / 2f), (height / 2f)) {
                val path = Path()
                var xOffset = 0f
                var startOffset = Offset.Zero
                var startingR = 0f
                for (i in 0 until 360) {
                    val rad = i * 2 * PI.toFloat() / 360f
                    val r = if ((2 * PI.toFloat() - rad) < 0.1) {
                        // this leads to some funky behavior - like a jellyfish
                        startingR
                    } else {
                        // modulating with noise
                        val noise = glm.perlin(Vec2(x = xOffset, y = value))
                        lerp(noise, -25f, 25f) + 400f

                        // can also modulate with a sine wave
                    }
                    // doesn't matter if this is negative, it just means
                    // that it will be drawn in the other direction
                    val x = r * cos(rad)
                    val y = r * sin(rad)
                    when (i) {
                        0 -> {
                            path.moveTo(x, y)
                            startOffset = Offset(x, y)
                            startingR = r
                        }
//                        2 * PI.toFloat() -> { // not sure if this does anything
//                            path.lineTo(startOffset.x, startOffset.y)
//                        }
                        else -> {
                            path.lineTo(x, y)
                        }
                    }

                    // get a full loop
//                    i = (i + 0.3f) // makes it hexagonal/more "pixelated"
                    xOffset = (xOffset + 0.05f) // makes it spikier, noisier
//                    drawCircle(
//                        color = Color.White,
//                        radius = 10f,
//                        center = Offset(x, y)
//                    )
                    // could be cool to see this as a ring of dots
                }
                path.close()
                drawPath(path, color = Color.Black)
            }
        }
    )
}
