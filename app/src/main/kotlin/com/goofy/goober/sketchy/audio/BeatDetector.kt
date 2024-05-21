package com.goofy.goober.sketchy.audio

import kotlin.math.pow

/**
 * BeatDetector is a class that performs simple beat detection using sound energy variations.
 * It processes waveform data to detect beats based on energy peaks.
 *
 * Taken from the algorithm described [here](http://archive.gamedev.net/archive/reference/programming/features/beatdetection/).
 *
 * @property sensitivity Sensitivity constant for beat detection.
 */
class BeatDetector(
    /**
     * This constant is a sensitivity threshold that determines how sensitive the algorithm is to
     * detecting beats. This constant is used to compare the instant energy with the local average
     * energy of the audio signal to decide if a beat has occurred.
     *
     * ### Simple Beat Detection Algorithm
     * In the simple sound energy algorithm, it is used to scale the average energy before comparing
     * it to the instant energy. The algorithm detects a beat if the instant energy is significantly
     * higher than the local average energy multiplied by this constant. The value of this constant
     * can be adjusted to control the sensitivity:
     * - Higher values of sensitivity (e.g., 1.4) make the algorithm less sensitive, detecting fewer
     * beats (useful for music with clear and distinct beats like techno or rap).
     * - Lower values of sensitivity (e.g., 1.1) make the algorithm more sensitive, detecting more
     * beats (useful for music with less distinct beats and more noise like rock).
     *
     */
    private val sensitivity: Float = 1.3f
) {
    // Waveform-based algorithm
    private var energyHistory = FloatArray(43)
    private var currentIndex = 0
    private var instantEnergy: Float = 0f
    private var averageEnergy: Float = 0f

    /**
     * Indicates whether a beat is detected based on the current energy values.
     */
    val beatDetected: Boolean
        get() = instantEnergy > sensitivity * averageEnergy

    /**
     * Processes the captured waveform data to detect beats.
     * Calculates the instant energy and compares it to the local average energy to detect beats.
     *
     * @param waveform The waveform data captured by the Visualizer.
     */
    fun processWaveform(waveform: ByteArray) {
        // Calculate the instant energy
        instantEnergy = waveform.sumOf { (it.toInt() / 128.0).pow(2.0) }.toFloat()

        // Calculate the average energy from history
        averageEnergy = energyHistory.average().toFloat()

        // Update energy history
        energyHistory[currentIndex] = instantEnergy
        currentIndex = (currentIndex + 1) % energyHistory.size
    }

    /**
     * Resets the energy history and index.
     */
    fun reset() {
        energyHistory.fill(0f)
        currentIndex = 0
    }
}

/**
 * BeatDetectorFft is a class that performs advanced beat detection using frequency-selected sound energy.
 * It processes FFT data to detect beats based on energy variations in frequency subbands.
 *
 * @property sensitivity Sensitivity constant for beat detection.
 */
class BeatDetectorFft(
    private val sensitivity: Float = 250f
) {
    // FFT-based algorithm
    private var fftEnergyHistory = Array(32) { FloatArray(43) }
    private var currentFftIndex = IntArray(32) { 0 }
    private var subbandEnergy = FloatArray(32)
    private var averageFftEnergy = FloatArray(32)

    /**
     * Indicates whether a beat is detected in any of the frequency subbands based on the current energy values.
     */
    val beatDetected: Boolean
        get() = subbandEnergy
            .anyIndexed { index, energy -> energy > sensitivity * averageFftEnergy[index] }

    /**
     * Processes the captured FFT data to detect beats.
     * Calculates the energy for each frequency subband and compares it to the local average energy to detect beats.
     *
     * @param fft The FFT data captured by the Visualizer.
     */
    fun processFftData(fft: ByteArray) {
        // Convert the FFT data to real-valued power spectrum
        val magnitudes = FloatArray(fft.size / 2)
        for (i in magnitudes.indices) {
            val real = fft[2 * i].toFloat()
            val imaginary = fft[2 * i + 1].toFloat()
            magnitudes[i] = (real * real + imaginary * imaginary).pow(0.5f)
        }

        // Divide the spectrum into 32 subbands
        val subbandSize = magnitudes.size / 32
        for (i in 0 until 32) {
            val start = i * subbandSize
            val end = start + subbandSize
            subbandEnergy[i] = magnitudes.slice(start until end).sum()

            // Calculate the local average energy for the subband
            averageFftEnergy[i] = fftEnergyHistory[i].average().toFloat()

            // Update energy history
            fftEnergyHistory[i][currentFftIndex[i]] = subbandEnergy[i]
            currentFftIndex[i] = (currentFftIndex[i] + 1) % fftEnergyHistory[i].size
        }
    }

    /**
     * Resets the FFT energy history and index.
     */
    fun reset() {
        fftEnergyHistory.forEach { it.fill(0f) }
        currentFftIndex.fill(0)
        subbandEnergy.fill(0f)
        averageFftEnergy.fill(0f)
    }
}

/**
 * Checks if any element in the array satisfies the given predicate with its index.
 *
 * @param predicate The predicate to test elements and their indices.
 * @return `true` if any element matches the predicate, `false` otherwise.
 */
inline fun FloatArray.anyIndexed(predicate: (index: Int, element: Float) -> Boolean): Boolean {
    for (index in indices) {
        if (predicate(index, this[index])) return true
    }
    return false
}
