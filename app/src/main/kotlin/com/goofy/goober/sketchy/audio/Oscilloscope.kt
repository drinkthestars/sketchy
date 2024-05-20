package com.goofy.goober.sketchy.audio

import android.graphics.Paint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import com.goofy.goober.sketchy.Sketch
import com.goofy.goober.style.Checkbox
import com.goofy.goober.style.MenuItem
import com.goofy.goober.style.Sizing

@Composable
fun Oscilloscope(
    modifier: Modifier = Modifier
) {
    val vizTypeMenuExpanded = remember { mutableStateOf(false) }
    val vizType = remember { mutableStateOf(VizType.FlashingLines) }

    val smoothingMenuExpanded = remember { mutableStateOf(false) }
    val smoothingType = remember { mutableStateOf(SmoothingType.None) }

    val enableFftCapture = remember { mutableStateOf(false) }
    val useBeatDetection = remember { mutableStateOf(true) }

    val state = rememberVisualizerState(
        smoothingType = smoothingType.value,
        enableFftCapture = enableFftCapture.value
    )

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(Sizing.Four)
        ) {
            Box {
                MenuItem(label = "Type: ", value = vizType.value.name) {
                    vizTypeMenuExpanded.value = true
                }
                DropdownMenu(expanded = vizTypeMenuExpanded.value, onDismissRequest = {
                    vizTypeMenuExpanded.value = false
                }) {
                    VizType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(text = type.name) },
                            onClick = { vizType.value = type })
                    }
                }
            }
            Spacer(modifier = Modifier.width(Sizing.Five))
            Box {
                MenuItem(
                    label = "Smoothing: ",
                    value = smoothingType.value.name
                ) { smoothingMenuExpanded.value = true }
                DropdownMenu(expanded = smoothingMenuExpanded.value, onDismissRequest = {
                    smoothingMenuExpanded.value = false
                }) {
                    SmoothingType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(text = type.name) },
                            onClick = { smoothingType.value = type })
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(Sizing.Four)
        ) {
            Checkbox(
                label = "FFT Capture",
                checked = enableFftCapture.value,
                onCheckedChange = {
                    enableFftCapture.value = it
                }
            )
            Checkbox(
                label = "Beat Detection",
                checked = useBeatDetection.value,
                onCheckedChange = {
                    useBeatDetection.value = it
                }
            )
        }

        Content(
            modifier = modifier,
            vizType = vizType.value,
            state = state,
            useBeatDetection = useBeatDetection.value
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    vizType: VizType,
    state: VisualizerState,
    useBeatDetection: Boolean
) {
    val path = remember { Path() }
    val paint = remember { Paint() }
    Sketch(
        modifier = modifier
            .fillMaxSize(),
        onDraw = { time ->
            val waveform = state.waveform
            if (waveform.isNotEmpty()) {
                when (vizType) {
                    VizType.FlashingLines -> drawFlashingLines(state, useBeatDetection)
                    VizType.MirroredLines -> drawMirrored(state, useBeatDetection, paint)
                    VizType.PolarPoints -> drawPolarPoints(state, useBeatDetection, time)
                    VizType.PolarPath -> drawPolarPath(state, path, useBeatDetection)
                    VizType.RadiateFixedSizePoints -> drawRadiatePoints(
                        state,
                        time,
                        useBeatDetection,
                        paint
                    )

                    VizType.RadiateDynamicSizePoints -> drawRadiatePointsVariableSize(
                        state,
                        time,
                        useBeatDetection,
                        paint
                    )

                    VizType.RadiateSimple -> drawRadiate(state, time, paint, useBeatDetection)
                }
            }

//                drawLinesFlashed(vizState)
//                drawPointsFlashed(vizState)
//                drawLinesFlashed(vizState)
//                drawSpiral2(waveform, time)
//                drawSpiral(waveform, time)
        }
    )
}
