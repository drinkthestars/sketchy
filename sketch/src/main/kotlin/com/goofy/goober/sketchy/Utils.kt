package com.goofy.goober.sketchy

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt
import kotlin.random.Random

const val PI = Math.PI.toFloat()
const val TWO_PI = 2f * Math.PI.toFloat()
const val HALF_PI = Math.PI.toFloat() / 2f

fun Random.nextFloat(min: Float, max: Float): Float {
    return nextFloat() * (max - min) + min
}

fun norm(value: Float, min: Float, max: Float): Float {
    return (value - min) / (max - min)
}

fun lerp(norm: Float, min: Float, max: Float): Float {
    return (max - min) * norm + min
}

fun Offset.distanceTo(offset2: Offset): Float {
    getDistanceSquared()
    val deltaX = offset2.x - this.x
    val deltaY = offset2.y - this.y
    return sqrt(deltaX * deltaX + deltaY * deltaY)
}

fun map(
    value: Float,
    sourceMin: Float,
    sourceMax: Float,
    destMin: Float,
    destMax: Float
): Float {
    return lerp(
        norm = norm(
            value = value,
            min = sourceMin,
            max = sourceMax
        ),
        min = destMin,
        max = destMax
    )
}

fun Int.mapTo(sourceMin: Float, sourceMax: Float, destMin: Float, destMax: Float): Float {
    return map(
        value = this.toFloat(),
        sourceMin = sourceMin,
        sourceMax = sourceMax,
        destMin = destMin,
        destMax = destMax
    )
}

fun Float.mapTo(sourceMin: Float, sourceMax: Float, destMin: Float, destMax: Float): Float {
    return map(
        value = this,
        sourceMin = sourceMin,
        sourceMax = sourceMax,
        destMin = destMin,
        destMax = destMax
    )
}
