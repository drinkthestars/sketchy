package com.goofy.goober.sketchy.audio

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.PI
import com.goofy.goober.sketchy.Sketch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun Fun(
    modifier: Modifier = Modifier
) {
    val state = rememberVisualizerState(
        smoothingType = SmoothingType.None,
        enableFftCapture = true
    )
    val paint = remember { Paint().apply {
        color = Color.WHITE
        strokeWidth = 15f
        strokeCap = Paint.Cap.ROUND

    } }
    val count = remember     { 1000 }
    val scalingFactor = remember { 5.0f }
    val phi = remember { (sqrt(5.0) + 1) / 2 }

    Sketch(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        onDraw = { time ->
            translate(size.width / 2, size.height / 2) {
                val radius = size.width / 2
                for (i in 0 until count) {
                    val factor = i.toFloat()/count.toFloat()
                    val angle = i.toFloat()/ phi.toFloat() + time * 0.3f
                    val dist = factor * radius * scalingFactor
                    val x =  cos(angle * PI * 2) * dist
                    val y = sin(angle * PI * 2)* dist
                    paint.strokeWidth = factor * 200f
                    drawIntoCanvas {
                        it.nativeCanvas.drawPoint(x, y, paint)
                    }
                }
            }
        }
    )
}