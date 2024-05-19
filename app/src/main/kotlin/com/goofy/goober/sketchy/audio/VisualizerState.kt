package com.goofy.goober.sketchy.audio

import android.media.audiofx.Visualizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.hypot

enum class VizType {
    FlashingLines,
    HorizPoints,
    PolarPoints,
    RadiateSimple,
    RadiateFixedSizePoints,
    RadiateDynamicSizePoints,
    Polarlines,
    Rects,
    Mirrored
}

enum class SmoothingType {
    None,
    Avg,
    EMA,
}

class VisualizerState {

    val visualizer: Visualizer = createVisualizer()

    // Waveform smoothing
    var lastWaveform: ByteArray? = null
    var waveform = ByteArray(0)
    var rawWaveform = ByteArray(0)

    // FFT
    var rawFFt = ByteArray(0)
    var lastBandMagnitudes: FloatArray? = null
    var fftBands = FloatArray(0)
    var maxNoise = 0f

    // Amplitude
    val isBeat get() = beatAmp > 50f
    var lastAmp = 0f
    var beatAmp = 0f

    fun setCaptureListener(
        smoothingType: SmoothingType,
        enableFftCapture: Boolean
    ) {
        visualizer.enabled = false
        visualizer.captureListener(this, smoothingType, enableFftCapture)
        visualizer.enabled = true
    }

    fun isAmplitudeHigh(): Boolean {
        val absSum = this.waveform.sumOf { abs(it.toInt()) }
        val currentAmp = absSum / (128f * this.waveform.size)
        return if (currentAmp > this.lastAmp) {
            this.lastAmp = currentAmp
            true
        } else {
            this.lastAmp *= 0.99f
            false
        }
    }

    fun dispose() {
        visualizer.apply {
            this@apply.enabled = false
            this@apply.setDataCaptureListener(
                /* listener = */ null,
                /* rate = */ 0,
                /* waveform = */ false,
                /* fft = */ false
            )
            this@apply.release()
        }
    }
}

@Composable
fun rememberVisualizerState(
    smoothingType: SmoothingType = SmoothingType.None,
    enableFftCapture: Boolean = true
): VisualizerState {
    val state = remember { VisualizerState() }
    LaunchedEffect(smoothingType, enableFftCapture) {
        state.setCaptureListener(smoothingType, enableFftCapture)
    }
    DisposableEffect(state) {
        onDispose {
            state.dispose()
        }
    }
    return state
}

private fun createVisualizer() = Visualizer(/* audioSession = */ 0).apply {
    scalingMode = Visualizer.SCALING_MODE_NORMALIZED
    this.captureSize = Visualizer.getCaptureSizeRange()[1]
    enabled = true
    measurementMode = Visualizer.MEASUREMENT_MODE_PEAK_RMS
}

private fun Visualizer.captureListener(
    state: VisualizerState,
    smoothingType: SmoothingType,
    enableFftCapture: Boolean
) {
    setDataCaptureListener(
        /* listener = */ object : Visualizer.OnDataCaptureListener {
            override fun onWaveFormDataCapture(
                visualizer: Visualizer?,
                waveform: ByteArray?,
                samplingRate: Int
            ) {
                waveform?.let {
                    when (smoothingType) {
                        SmoothingType.Avg -> waveformAverageSmoothing(state, it)
                        SmoothingType.EMA -> {
                            val smoothedWaveform = exponentialMovingAverageSmoothing(
                                newWaveform = it,
                                oldWaveform = state.lastWaveform
                            )
                            state.waveform = smoothedWaveform
                            state.lastWaveform = smoothedWaveform
                        }

                        SmoothingType.None -> {
                            state.waveform = it
                        }
                    }
                    state.rawWaveform = it
                }
            }

            override fun onFftDataCapture(
                visualizer: Visualizer,
                fft: ByteArray,
                samplingRate: Int
            ) {
                state.rawFFt = fft
                state.maxNoise = fft.average().toFloat().absoluteValue * 30f

                val bandMagnitudes = calculateFftMagnitudes(samplingRate, fft)

                when (smoothingType) {
                    SmoothingType.None -> {
                        state.fftBands = bandMagnitudes
                    }

                    SmoothingType.Avg -> {
                        fftAverageSmoothing(
                            visualizerState = state,
                            newBandMagnitudes = bandMagnitudes
                        )
                    }

                    SmoothingType.EMA -> {
                        val smoothedBandMagnitudes = exponentialMovingAverageSmoothing(
                            newWaveform = bandMagnitudes,
                            oldWaveform = state.lastBandMagnitudes
                        )
                        state.fftBands = smoothedBandMagnitudes
                        state.lastBandMagnitudes = smoothedBandMagnitudes
                    }
                }

                state.beatAmp = bandMagnitudes[0] + bandMagnitudes[1] + bandMagnitudes[2]
            }
        },
        /* rate = */ Visualizer.getMaxCaptureRate(),
        /* waveform = */ true,
        /* fft = */ enableFftCapture
    )
}

private fun calculateFftMagnitudes(samplingRate: Int, fft: ByteArray): FloatArray {
    // Converting it to hertz
    val samplingRateInHz = samplingRate / 1000
    // Considering half because the FFT contains real and imaginary parts interlaced
    val n = fft.size / 2
    val magnitudes = FloatArray(n + 1)

    // Calculate magnitudes for each FFT bin
    magnitudes[0] = abs(fft[0].toFloat())  // DC component
    magnitudes[n] = abs(fft[1].toFloat())  // Nyquist frequency
    for (k in 1 until n) {
        val i = 2 * k
        if (i + 1 < fft.size) {
            magnitudes[k] = hypot(fft[i].toFloat(), fft[i + 1].toFloat())
        }
    }

    val bandMagnitudes = FloatArray(FreqBands.size) { 0f }

    // Map magnitudes to frequency bands
    FreqBands.forEachIndexed { index, (low, high) ->
        val lowIndex = ceil(low * n / (samplingRateInHz / 2.0)).toInt()
            .coerceAtLeast(1) // Ensure start is beyond DC component
        val highIndex = floor(high * n / (samplingRateInHz / 2.0)).toInt()
            .coerceAtMost(n - 1) // Ensure end is before Nyquist component
        var sum = 0f
        var count = 0

        if (lowIndex <= highIndex) {
            for (i in lowIndex..highIndex) {
                if (i < n) {
                    sum += magnitudes[i]
                    count++
                }
            }
        }

        // Calculate average magnitude for each band
        bandMagnitudes[index] = if (count > 0) sum / count else 0f
    }
    return bandMagnitudes
}

fun exponentialMovingAverageSmoothing(
    newWaveform: ByteArray,
    oldWaveform: ByteArray?,
    alpha: Float = 0.3f
): ByteArray {
    if (oldWaveform == null || oldWaveform.size != newWaveform.size) {
        return newWaveform  // No previous data to smooth with, return current
    }

    val smoothedWaveform = ByteArray(newWaveform.size)
    for (i in newWaveform.indices) {
        smoothedWaveform[i] =
            (alpha * newWaveform[i] + (1 - alpha) * oldWaveform[i]).toInt().toByte()
    }
    return smoothedWaveform
}

fun exponentialMovingAverageSmoothing(
    newWaveform: FloatArray,
    oldWaveform: FloatArray?,
    alpha: Float = 0.3f
): FloatArray {
    if (oldWaveform == null || oldWaveform.size != newWaveform.size) {
        return newWaveform  // No previous data to smooth with, return current
    }

    val smoothedWaveform = FloatArray(newWaveform.size)
    for (i in newWaveform.indices) {
        smoothedWaveform[i] =
            (alpha * newWaveform[i] + (1 - alpha) * oldWaveform[i])
    }
    return smoothedWaveform
}

private fun waveformAverageSmoothing(visualizerState: VisualizerState, newWaveform: ByteArray) {
    if (visualizerState.lastWaveform == null || visualizerState.lastWaveform?.size != newWaveform.size) {
        visualizerState.lastWaveform = newWaveform
    } else {
        // Blend the current waveform with the last one
        val smoothedWaveform = ByteArray(newWaveform.size)

        for (i in newWaveform.indices) {
            smoothedWaveform[i] =
                ((visualizerState.lastWaveform!![i].toInt() + newWaveform[i].toInt()) / 2).toByte()
        }
        visualizerState.waveform = smoothedWaveform
        visualizerState.lastWaveform = smoothedWaveform
    }
}

private fun fftAverageSmoothing(visualizerState: VisualizerState, newBandMagnitudes: FloatArray) {
    if (visualizerState.lastBandMagnitudes == null || visualizerState.lastBandMagnitudes!!.size != newBandMagnitudes.size) {
        visualizerState.lastBandMagnitudes = newBandMagnitudes
    } else {
        // Blend the current waveform with the last one
        for (i in newBandMagnitudes.indices) {
            newBandMagnitudes[i] =
                ((visualizerState.lastBandMagnitudes!![i].toInt() + newBandMagnitudes[i]) / 2)
        }
    }
    visualizerState.fftBands = newBandMagnitudes
    visualizerState.lastBandMagnitudes = newBandMagnitudes
}

//fun windowedSmoothing(currentWaveform: ByteArray, pastWaveforms: List<ByteArray>, windowSize: Int = 5): ByteArray {
//    val waveformCount = pastWaveforms.size
//    val finalWaveform = ByteArray(currentWaveform.size) { 0 }
//
//    // Accumulate waveforms
//    for (i in 0 until waveformCount) {
//        for (j in currentWaveform.indices) {
//            val pastWaveform: Byte = pastWaveforms[i][j]
//            val wfCountByte = waveformCount.toByte()
//            val oldFinalWf = finalWaveform[j]
//            finalWaveform[j] = (oldFinalWf + (pastWaveform/wfCountByte).toByte()).toByte()
//        }
//    }
//
//    // Add the current waveform to the mix
//    for (j in currentWaveform.indices) {
//        finalWaveform[j] = ((finalWaveform[j] * (windowSize - 1) + currentWaveform[j]) / windowSize).toByte()
//    }
//
//    return finalWaveform
//}
//

private val FreqBands = arrayOf(
    0 to 63,
    63 to 160,
    160 to 400,
    400 to 1000,
    1000 to 2500,
    2500 to 6250,
    6250 to 16000
)
