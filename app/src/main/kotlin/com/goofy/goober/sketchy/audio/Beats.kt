package com.goofy.goober.sketchy.audio

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.Sketch
import kotlinx.coroutines.delay
import kotlin.math.abs

/**
 * Step 2: Create an Animated Beat Indicator
 * To make the visualization more dynamic, we can animate a background or elements that react to
 * the beat. Using the animateColorAsState and animateDpAsState from Compose's animation package,
 * you can create smooth transitions:
 */
@Composable
fun AnimatedBeatIndicator(beats: List<Int>, currentTimeIndex: Int) {
    val pulseTrigger = beats.any { currentTimeIndex in it - 20..it + 20 }
    val color = animateColorAsState(
        targetValue = if (pulseTrigger) Color(0xFFE91E63) else Color(0xFF607D8B),
        animationSpec = tween(durationMillis = 500), label = ""
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color.value)
    )
}

/**
 * Step 3: Integrate and Manage Time
 * Manage the current time index that simulates the real-time passing of audio frames. This can be
 * done using a state and periodically incrementing it:
 */
@Composable
fun BeatVisualizationScreen() {
    var currentTimeIndex = remember { mutableStateOf(0) }
    val visualizerState = remember { VisualizerState() }
    val visualizer = rememberVisualizer(visualizerState)

    Sketch(
        modifier = Modifier.fillMaxSize(),
        onDraw = { time ->
            drawCircle(
        }
    )
}
