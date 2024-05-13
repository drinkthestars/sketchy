package com.goofy.goober.sketchy.audio

class BeatDetector(val sampleRate: Int, val bufferSize: Int) {
    private val ringBuffer = CircularFloatBuffer(bufferSize)
    private val energyThreshold = 1.2f..2.0f

    fun isBeat(samples: ShortArray): Boolean {
        val instantEnergy = getInstantEnergy(samples)
        val values = ringBuffer.getValues()
        val localAverage = getAverage(values)
        val variance = getVariance(values, localAverage)
        var constant = (-0.0025714f * variance) + 1.5142857f

        constant = constant.coerceIn(energyThreshold)

        ringBuffer.add(instantEnergy)

        return instantEnergy > constant * localAverage
    }

    private fun getInstantEnergy(samples: ShortArray): Float =
        samples.sumOf { sample -> sample.toDouble().pow(2).toFloat() }

    private fun getAverage(values: FloatArray): Float =
        values.average()

    private fun getVariance(values: FloatArray, avg: Float): Float =
        values.sumOf { value -> (value - avg).toDouble().pow(2).toFloat() } / values.size
}

class CircularFloatBuffer(private val size: Int) {
    private val buffer = FloatArray(size)
    private var index = 0

    fun add(value: Float) {
        buffer[index] = value
        index = (index + 1) % size
    }

    fun getValues(): FloatArray = buffer
}
