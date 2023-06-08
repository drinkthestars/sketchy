package com.goofy.goober.sketchy.common

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.goofy.goober.sketchy.TWO_PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Immutable
class RandomBlobs(
    val color: Color = Color.Transparent,
    val count: Int = 1,
)

fun DrawScope.splatter(center: Offset, color: Color, maxRadius: Float, droplets: Int) {
    // Draw the main blob
    drawCircle(color = color, center = center, radius = maxRadius)

    // Random number generator
    val rand = Random(seed = droplets)

    // The distance factor determines how far the droplets will spread
    val distanceFactor = 2.0f

    for (i in 1 until droplets) {
        // Vary the radius of the droplet as we move further away from the center.
        // The random component adds some variation to the radius
        val radius = maxRadius * (1 - i.toFloat() / droplets) * rand.nextFloat()

        // Vary the distance of the droplet from the center as we move further away from the center
        val angle = rand.nextFloat() * TWO_PI
        val distance = maxRadius * (i.toFloat() / droplets) * distanceFactor

        // Calculate the position of the droplet
        val x = center.x + cos(angle) * distance
        val y = center.y + sin(angle) * distance

        // Draw the droplet
        drawCircle(color = color, center = Offset(x, y), radius = radius)
    }
}
