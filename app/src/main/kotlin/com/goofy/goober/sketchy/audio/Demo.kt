package com.goofy.goober.sketchy.audio

import android.media.audiofx.Visualizer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun Demo() {
    val waveformState = remember { mutableStateOf(ByteArray(0)) }
    val visualizer = remember {
        Visualizer(0).apply {
            scalingMode = Visualizer.SCALING_MODE_NORMALIZED
            this.captureSize = Visualizer.getCaptureSizeRange()[1]
            measurementMode = Visualizer.MEASUREMENT_MODE_PEAK_RMS
            setDataCaptureListener(
                /* listener = */ object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        visualizer: Visualizer,
                        waveform: ByteArray,
                        samplingRate: Int
                    ) {
//                        waveformState.value = waveform.clone()
                        println("VIZ: $waveform")
                    }

                    override fun onFftDataCapture(
                        visualizer: Visualizer,
                        fft: ByteArray,
                        samplingRate: Int
                    ) {
                        // Do nothing
                    }
                },
                /* rate = */ Visualizer.getMaxCaptureRate()/2,
                /* waveform = */ true,
                /* fft = */ false
            )
            enabled = true
        }
    }

    DisposableEffect(visualizer) {
        onDispose {
            visualizer.enabled = false
            visualizer.release()
        }
    }

    Canvas(modifier = Modifier.fillMaxSize().clickable {  }) {
        drawWave(waveformState.value)
    }
}

private fun DrawScope.drawWave(waveform: ByteArray) {
    val maxHeight = size.height * 0.15f

    val points = FloatArray(waveform.size * 4)
    for (i in 0 until waveform.size - 1) {
        val x1 = i * size.width / waveform.size
        val x2 = (i + 1) * size.width / waveform.size
        // The waveform values are between -128 and 127, so we need to scale
        // them to fit the canvas
        val y1 = size.height / 2 + waveform[i] / 128f * maxHeight
        val y2 = size.height / 2 + waveform[i + 1] / 128f * maxHeight
        points[i * 4] = x1
        points[i * 4 + 1] = y1
        points[i * 4 + 2] = x2
        points[i * 4 + 3] = y2
    }
    drawIntoCanvas {
        it.nativeCanvas.drawLines(
            points,
            paint
        )
    }
}

private val paint = android.graphics.Paint().apply {
    color = android.graphics.Color.WHITE
    strokeWidth = 2f
}