package com.goofy.goober.sketchy.audio

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.Sketch

@Composable
fun VisualizerBasics(
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
            drawHistogram(state)
        }
    )
}