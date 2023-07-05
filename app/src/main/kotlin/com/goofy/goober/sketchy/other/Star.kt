package com.goofy.goober.sketchy.other

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot.Companion.withMutableSnapshot
import androidx.compose.ui.geometry.Offset
import com.goofy.goober.sketchy.mapTo
import kotlin.random.Random

private const val SizeMax = 30f
private const val SizeMin = 12f

class Star(
    private val screenWidthPx: Float,
    private val screenHeightPx: Float
) {
    private var x = random(-screenWidthPx, screenWidthPx)
    private var y = random(-screenHeightPx, screenHeightPx)
    private var z = random(max = screenWidthPx)
    private var prevZ = z
    private val randMaxSize = random(SizeMin, SizeMax)

    var fromOffset by mutableStateOf(Offset.Zero)
    var toOffset by mutableStateOf(Offset.Zero)
    var radius by mutableFloatStateOf(0f)
    val color = StarColors.random()

    fun draw(speed: Float) {
        withMutableSnapshot {
            z -= speed
            if (z < 1) {
                z = screenWidthPx
                x = random(-screenWidthPx, screenWidthPx)
                y = random(-screenHeightPx, screenHeightPx)
                prevZ = z
            }
            radius = z.mapTo(
                sourceMin = 0f,
                sourceMax = screenWidthPx,
                destMin = randMaxSize,
                destMax = 0f
            )
            fromOffset = Offset(
                x = (x / prevZ).mapTo(
                    sourceMin = 0f,
                    sourceMax = 1f,
                    destMin = 0f,
                    destMax = screenWidthPx
                ),
                y = (y / prevZ).mapTo(
                    sourceMin = 0f,
                    sourceMax = 1f,
                    destMin = 0f,
                    destMax = screenHeightPx
                )
            )
            toOffset = Offset(
                x = (x / z).mapTo(
                    sourceMin = 0f,
                    sourceMax = 1f,
                    destMin = 0f,
                    destMax = screenWidthPx
                ),
                y = (y / z).mapTo(
                    sourceMin = 0f,
                    sourceMax = 1f,
                    destMin = 0f,
                    destMax = screenHeightPx
                )
            )
            prevZ = z
        }
    }

    private fun random(min: Float = 0f, max: Float) = min + Random.nextFloat() * (max - min)
}