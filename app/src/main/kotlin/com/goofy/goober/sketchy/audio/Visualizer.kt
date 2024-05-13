package com.goofy.goober.sketchy.audio

import android.media.audiofx.Visualizer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.math.absoluteValue
import kotlin.math.sqrt


class VisualizerState {
    val waveformState = mutableStateOf(ByteArray(0))
    val fftState = mutableStateOf(ByteArray(0))
    val maxNoise = mutableStateOf(0f)
    val frequencyBands = mutableStateOf(FloatArray(0))
    val numBands = 10
    val samplingRate = 44100  // Example: CD quality audio
    val beats = mutableStateOf(emptyList<Int>())
    val beatCount = mutableStateOf(Int)
}

@Composable
internal fun rememberVisualizer(
    visualizerState: VisualizerState
): Visualizer {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    return remember(lifecycle) {
        Visualizer(0).apply {
            setDataCaptureListener(
                /* listener = */ object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        visualizer: Visualizer?,
                        waveform: ByteArray?,
                        samplingRate: Int
                    ) {
                        // Update the waveform state
                        waveform?.let {
                            visualizerState.waveformState.value = it
                            /**
                             * Step 3: Integrate into Visualizer
                             * You can then integrate this into your visualizer processing, calling
                             * these functions when new waveform data is captured:
                             */
                            val envelope = calculateEnvelope(waveform)
                            val onsets = detectOnsets(envelope, 10f)  // Threshold is a tuned parameter
                            visualizerState.beats.value = onsets


                            /**
                             * Integrate Beat Detection
                             * In your data capture listener, process the audio buffers, calculate
                             * the energy, update history, and detect beats:
                             */
                            val energy = calculateEnergy(it)
                            val historyAverage = energyHistory.average()
                            energyHistory.add(energy)

                            if (detectBeat(energy, historyAverage)) {
                                visualizerState.beatCount.value++
                            }

                            // Example condition to reset
                            if (visualizerState.beatCount.value > 1000) {
                                visualizerState.beatCount.value = 0
                            }
                        }
                    }

                    override fun onFftDataCapture(
                        visualizer: Visualizer,
                        fft: ByteArray,
                        samplingRate: Int
                    ) {
                        visualizerState.fftState.value = fft
                        visualizerState.maxNoise.value = fft.average().toFloat().absoluteValue * 30f

                        // Process FFT data into bands and update state
                        visualizerState.frequencyBands.value = processFftToBands(
                            fft = fft,
                            numBands = visualizerState.numBands,
                            samplingRate = visualizerState.samplingRate
                        )
                    }
                },
                /* rate = */ Visualizer.getMaxCaptureRate() / 2,
                /* waveform = */ true,
                /* fft = */ true
            )
            captureSize = 256
            enabled = true
            measurementMode = Visualizer.MEASUREMENT_MODE_PEAK_RMS
            lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    this@apply.enabled = false
                    this@apply.setDataCaptureListener(
                        /* listener = */ null,
                        /* rate = */ 0,
                        /* waveform = */ false,
                        /* fft = */ false
                    )
                    this@apply.release()
                }
            })
        }
    }
}

//fun processFftToBands(fft: ByteArray, numBands: Int, samplingRate: Int): FloatArray {
//    // Calculate magnitudes from FFT data
//    val magnitudes = FloatArray(fft.size / 2)
//    for (i in 0 until fft.size / 2 step 2) {
//        val real = fft[i].toInt()
//        val imaginary = fft[i + 1].toInt()
//        magnitudes[i / 2] = sqrt((real * real + imaginary * imaginary).toFloat())
//    }
//
//    // Initialize bands
//    val bands = FloatArray(numBands)
//    val binsPerBand = magnitudes.size / numBands
//
//    // Sum magnitudes into bands
//    for (band in 0 until numBands) {
//        var sum = 0f
//        for (bin in 0 until binsPerBand) {
//            sum += magnitudes[band * binsPerBand + bin]
//        }
//        bands[band] = sum / binsPerBand  // Average magnitude for this band
//    }
//
//    return bands
//}
fun processFftToBands(fft: ByteArray, numBands: Int, samplingRate: Int): FloatArray {
    // Convert FFT bytes to magnitudes (skipping the first bin if DC offset is problematic)
    val magnitudes = FloatArray((fft.size / 2) - 1)
    for (i in 2 until fft.size step 2) {  // Start from 2 to skip the first bin
        val real = fft[i].toInt()
        val imaginary = fft[i + 1].toInt()
        magnitudes[(i / 2) - 1] = sqrt((real * real + imaginary * imaginary).toFloat())
    }

    // Group magnitudes into bands
    val bands = FloatArray(numBands)
    val binsPerBand = magnitudes.size / numBands
    for (band in 0 until numBands) {
        var sum = 0f
        for (bin in 0 until binsPerBand) {
            sum += magnitudes[band * binsPerBand + bin]
        }
        bands[band] = sum / binsPerBand
    }

    return bands
}


// Beat detection

/**
 * Step 1: Amplitude Envelope Extraction
 * We first need a function to calculate the amplitude envelope from the waveform data.
 * Since you're using the Visualizer, which provides both FFT and waveform data, we can calculate
 * the envelope from the waveform:
 */
fun calculateEnvelope(waveform: ByteArray): FloatArray {
    val envelope = FloatArray(waveform.size / 2)
    for (i in 0 until waveform.size step 2) {
        val firstSample = waveform[i].toInt()
        val secondSample = waveform[i + 1].toInt()
        envelope[i / 2] = sqrt((firstSample * firstSample + secondSample * secondSample).toFloat())
    }
    return envelope
}

/**
 * Step 2: Onset Detection
 * To detect onsets, look for points where the amplitude envelope increases sharply:
 */
fun detectOnsets(envelope: FloatArray, threshold: Float): List<Int> {
    val onsets = mutableListOf<Int>()
    for (i in 1 until envelope.size) {
        if (envelope[i] > envelope[i - 1] + threshold) {
            onsets.add(i)
        }
    }
    return onsets
}

// https://archive.gamedev.net/archive/reference/programming/features/beatdetection/index.html

/**
 * Calculate Energy
 * For each buffer, calculate the energy (sum of squares of the samples):
 */
fun calculateEnergy(buffer: ByteArray): Double {
    return buffer.map { it.toInt() * it.toInt() }.average()
}

/**
 * Detect Beats
 * Implement the beat detection logic:
 */
fun detectBeat(currentEnergy: Double, historyAverage: Double): Boolean {
    return currentEnergy > 1.3 * historyAverage
}

/**
 * Historical Energy Comparison
 * Maintain a rolling history of energy values and compare the current energy to the average:
 */
class EnergyHistory(size: Int) {
    private val energies = ArrayDeque<Double>()
    private val maxSize = size

    fun add(energy: Double) {
        if (energies.size >= maxSize) energies.removeFirst()
        energies.addLast(energy)
    }

    fun average() = energies.average()
}

val energyHistory = EnergyHistory(43) // Assuming 43 buffers per second
