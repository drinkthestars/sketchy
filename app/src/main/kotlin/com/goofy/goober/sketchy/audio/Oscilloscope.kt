package com.goofy.goober.sketchy.audio

import android.graphics.Point
import android.media.audiofx.Visualizer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.goofy.goober.sketchy.Sketch
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun Oscilloscope(
    modifier: Modifier = Modifier
) {
    val vizState = remember {
        VisualizerState()
    }
    val visualizer = rememberVisualizer(vizState)

    Sketch(
        modifier = modifier.fillMaxSize().aspectRatio(1f),
        onDraw = {time ->
            drawBezierWaveform(vizState.waveformState.value)
        }
    )
}

fun DrawScope.drawWaveform(waveform: ByteArray) {
    val middleY = size.height / 2
    val pointStep = size.width / (waveform.size - 1)
    for (i in waveform.indices) {
        val x1 = i * pointStep
        val y1 = middleY + ((waveform[i].toInt() + 128) - 128) * (middleY / 128f)
        if (i < waveform.size - 1) {
            val x2 = (i + 1) * pointStep
            val y2 = middleY + ((waveform[i + 1].toInt() + 128) - 128) * (middleY / 128f)
            drawLine(
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                color = Color.Green,
                strokeWidth = 5f
            )
        }
    }
}

fun DrawScope.drawBezierWaveform(waveform: ByteArray) {
    val paint = Paint().apply {
        color = Color.Cyan
        strokeWidth = 3f
        style = PaintingStyle.Stroke
    }
    val middleY = size.height / 2
    val pointStep = size.width / (waveform.size - 1)

    val path = Path().apply {
        moveTo(0f, middleY)
        for (i in waveform.indices step 3) {
            if (i >= 0) {
                val x1 = i * pointStep
                val y1 = middleY + ((waveform[i].toInt() + 128) - 128) * (middleY / 128f)
                if (i + 2 < waveform.size) {
                    val x2 = (i + 2) * pointStep
                    val y2 = middleY + ((waveform[i + 2].toInt() + 128) - 128) * (middleY / 128f)
                    cubicTo(x1, y1, (x1 + x2) / 2, (y1 + y2) / 2, x2, y2)
                }
            }
        }
    }
    drawPath(
        path = path,
        color = Color.Cyan,
        style = Stroke(3f)
    )
}

fun DrawScope.drawCircularWaveform(waveform: ByteArray) {
    val radius = min(size.width, size.height) / 3
    val centerX = size.width / 2
    val centerY = size.height / 2
    val path = Path()
    waveform.indices.forEach { i ->
        val angle = i * (360f / waveform.size)
        val adjustedMagnitude = ((waveform[i].toInt() + 128) - 128) * (radius / 128f)
        val x = centerX + cos(Math.toRadians(angle.toDouble())) * (radius + adjustedMagnitude)
        val y = centerY + sin(Math.toRadians(angle.toDouble())) * (radius + adjustedMagnitude)
        if (i == 0) {
            path.moveTo(x.toFloat(), y.toFloat())
        } else {
            path.lineTo(x.toFloat(), y.toFloat())
        }
    }
    path.close()
    drawPath(
        path = path,
        color = Color.Magenta,
        style = Stroke(2f)
    )
}

fun DrawScope.drawWaveformPoints(waveform: ByteArray) {
    val paint = Paint().apply {
        color = Color.Yellow
        strokeWidth = 10f
        strokeCap = StrokeCap.Round  // This makes the points round
    }
    val middleY = size.height / 2
    val pointStep = size.width / (waveform.size - 1)
    waveform.indices.map { i ->
        val x = i * pointStep
        val y = middleY + ((waveform[i].toInt() + 128) - 128) * (middleY / 128f)
        Offset(x, y)
    }.apply {
        drawPoints(
            points = this,
            pointMode = PointMode.Points,
            color = Color.Gray,
            strokeWidth = 10f,
            cap = StrokeCap.Round
        )
    }
}
