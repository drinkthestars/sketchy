package com.goofy.goober.sketchy.audio

import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import com.goofy.goober.sketchy.map
import com.goofy.goober.sketchy.mapInt
import com.goofy.goober.sketchy.norm
import glm_.glm
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * Will draw a line for each data point in [waveform].
 * [waveform] is expected to be a byte array with values ranging from -128 to 127, so the lines
 * will be drawn based on the amplitude of each data point and will be centered vertically, with
 * some lines going above and below the center.
 */
fun DrawScope.drawBasicLines(
    waveform: ByteArray
) {
    if (waveform.isNotEmpty()) {
        val screenHeight = size.height
        val screenWidth = size.width
        val centerY = screenHeight / 2f
        val amplitudeScale = (screenHeight * 0.3f) / 128f  // Scale to fit a % of screen height

        // Draw lines for each data point
        for (i in waveform.indices) {
            val x = i * (screenWidth / waveform.size.toFloat())
            val y = centerY - (waveform[i] * amplitudeScale)
            drawLine(
                color = Color.Green,
                start = Offset(x, centerY),
                end = Offset(x, y),
                strokeWidth = 2f  // Adjust the stroke width if needed
            )
        }
    }
}

fun DrawScope.drawMirrored(
    state: VisualizerState,
    useBeatDetection: Boolean,
    paint: Paint
) {
    val waveform = state.waveform
    val screenHeight = size.height
    val screenWidth = size.width
    val centerY = screenHeight / 2f
    var offset = 0f
    var shadowRad = 10f
    var shadowColor = android.graphics.Color.GREEN + android.graphics.Color.BLUE
    var alpha = 255

    if (useBeatDetection) {
        if (state.isAmplitudeHigh()) {
            alpha = 255
            shadowRad = 50f
            shadowColor = android.graphics.Color.YELLOW
            offset = 0.7f
        } else {
            alpha = 100
            shadowRad = 5f
        }
    }

    paint.setShadowLayer(
        shadowRad,
        0f,
        0f,
        shadowColor
    )
    paint.alpha = alpha
    paint.strokeWidth = 2f
    paint.shader = LinearGradient(
        0f, 0f, // start coordinates (x1, y1) at the top-left corner
        0f, screenHeight, // end coordinates (x2, y2) at the bottom-left corner
        android.graphics.Color.GREEN,
        android.graphics.Color.BLUE,
        Shader.TileMode.CLAMP
    )

    // Normalize and scale the waveform data
    val amplitudeScale = (screenHeight * 0.1f) / 128f + offset

    val points = FloatArray(waveform.size * 4)

    // Collect points for the original and mirrored waveform
    for (i in waveform.indices) {
        val x = i * (screenWidth / waveform.size.toFloat())
        val y = centerY - (waveform[i] * amplitudeScale)
        val mirroredY = centerY + (waveform[i] * amplitudeScale)  // Mirrored point


        // Add point for the upper half
        points[i * 4] = x
        points[i * 4 + 1] = y

        // Add point for the lower half (mirrored)
        points[i * 4 + 2] = x
        points[i * 4 + 3] = mirroredY
    }

    drawIntoCanvas {
        it.nativeCanvas.drawLines(points, paint)
    }
}

private fun DrawScope.drawSpiral(
    state: VisualizerState,
    time: Float
) {
    val waveform = state.waveform
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    val maxRadius = size.width * 0.4f  // Maximum radius of the spiral
    val amplitudeScale = maxRadius / 128f  // Scaling factor for waveform amplitude
    val angleIncrement = 0.05f  // How much the angle increases with each point
    val rotationSpeed = 0.02f  // Speed at which the spiral rotates

    val points = mutableListOf<Offset>()
    for (i in waveform.indices) {
        val amplitude = waveform[i] * amplitudeScale
        val radius =
            (i.toFloat() / waveform.size) * maxRadius + amplitude  // Increase radius gradually
        // Current angle accounts for time-based rotation
        val currentAngle = angleIncrement * i + time * rotationSpeed
        val x = centerX + radius * cos(currentAngle)
        val y = centerY + radius * sin(currentAngle)
        points.add(Offset(x, y))
    }
    val energy = state.lastAmp
    // energy goes from 0 to 128
    // start and end color brightness according to energy
    val startColor = Color.hsl(
        hue = 150f,
        saturation = map(energy, 0f, 128f, 0f, 1f),
        lightness = map(energy, 0f, 128f, 0f, 1f)
    )
    val endColor = Color.hsl(
        hue = 300f,
        saturation = 1f,
        lightness = map(energy, 0f, 128f, 0.5f, 1f)
    )
    drawPoints(
        points = points,
        pointMode = PointMode.Points,
        brush = Brush.verticalGradient(
            colors = listOf(
                startColor,
                endColor
            ),
            startY = 0f,
            endY = size.height
        ),
        strokeWidth = 15f,  // Size of each point
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawSpiral2(
    waveform: ByteArray,
    time: Float
) {
    if (waveform.isNotEmpty()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val maxRadius = minOf(size.width, size.height) / 2f * 0.8f  // Maximum radius of the spiral
        val baseRadius = maxRadius / 128f  // Base scaling factor for waveform amplitude
        val angleIncrement = 0.05f  // Adjust for more gradual spiraling
        val timeFactor = time  // Controls the rate of "unraveling" towards the viewer

        val points = mutableListOf<Offset>()
        for (i in waveform.indices) {
            // Only positive values and scaling them for visibility
            val amplitude = Math.max(0f, waveform[i].toFloat()) * baseRadius
            // Radius calculation includes a dynamic component to simulate movement
            val radius = (i.toFloat() / waveform.size) * maxRadius + amplitude * 0.3f + timeFactor
            val currentAngle =
                angleIncrement * i - timeFactor * 0.005f  // Rotation adjustment with time
            val x = centerX + radius * cos(currentAngle)
            val y = centerY + radius * sin(currentAngle)

            points.add(Offset(x, y))

        }

        // Applying fading based on the distance from the center to enhance depth perception
        val color = Color.Green.copy(alpha = max(0f, 1 - (timeFactor / maxRadius)))

        drawPoints(
            points = points,
            pointMode = PointMode.Polygon,
            pathEffect = PathEffect.cornerPathEffect(22f),
            brush = Brush.verticalGradient(
                colors = listOf(color, Color.Blue.copy(alpha = 0.2f)),
                startY = 0f,
                endY = size.height
            ),
            strokeWidth = 5f,  // Size of each point
            cap = StrokeCap.Round
        )
    }
}

fun DrawScope.drawRadiate(
    state: VisualizerState,
    time: Float,
    paint: Paint,
    useBeatDetection: Boolean
) {
    val waveform = state.waveform
    if (waveform.isNotEmpty()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        var radiusOffset = 3f
        if (useBeatDetection) {
            radiusOffset = if (state.isAmplitudeHigh()) {
                2f
            } else {
                3f
            }
        }

        val maxRadius = minOf(size.width, size.height) / radiusOffset  // Maximum spike length
        val baseRadius = 10f  // Minimum spike length
        val angleIncrement = (2 * PI / waveform.size).toFloat()  // Angle between each spike
        val strokeWidth = 5f

        val points = FloatArray(waveform.size * 4)
        for (i in waveform.indices) {
            val amplitude = waveform[i].toFloat()  // Current sample amplitude
            val normalizedAmplitude =
                (amplitude + 128) / 256  // Normalize amplitude to range [0, 1]
            val lineLength = baseRadius + normalizedAmplitude * maxRadius  // Calculate line length
            val angle = i * angleIncrement  // Calculate the angle for the spike
            val endX = centerX + lineLength * cos(angle)
            val endY = centerY + lineLength * sin(angle)

            points[i * 4] = centerX
            points[i * 4 + 1] = centerY
            points[i * 4 + 2] = endX
            points[i * 4 + 3] = endY
        }

        val startColor = Color(
            red = (sin(time) * 127 + 128).toInt(),
            green = (sin(time + PI / 3) * 127 + 128).toInt(),
            blue = (sin(time + 2 * PI / 3) * 127 + 128).toInt()
        )
        val endColor = Color(
            red = (sin(time + PI) * 127 + 128).toInt(),
            green = (sin(time + PI + PI / 3) * 127 + 128).toInt(),
            blue = (sin(time + PI + 2 * PI / 3) * 127 + 128).toInt()
        )
        drawIntoCanvas {
            paint.apply {
                shader = RadialGradient(
                    centerX,
                    centerY,
                    maxRadius,
                    startColor.toArgb(),
                    endColor.toArgb(),
                    Shader.TileMode.CLAMP
                )
                this.strokeWidth = strokeWidth
                this.strokeCap = android.graphics.Paint.Cap.ROUND
            }
            it.nativeCanvas.drawLines(points, paint)
        }
    }
}

/**
 * THIS ONE IS AMAZING
 */
fun DrawScope.drawRadiatePoints(
    state: VisualizerState,
    time: Float,
    useBeatDetection: Boolean,
    paint: Paint
) {
    val waveform = state.waveform
    if (waveform.isNotEmpty()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        var radiusMultiplier = 3f
        var shadowRadius = 10f

        if (useBeatDetection) {
            if (state.isAmplitudeHigh()) {
                radiusMultiplier = 2f
                shadowRadius = 55f
            } else {
                radiusMultiplier = 3f
                shadowRadius = 5f
            }
        }

        val maxRadius = minOf(size.width, size.height) / radiusMultiplier

        val angleIncrement = (2 * PI / waveform.size).toFloat()  // Angle between each point

        // List to hold all the points
        val points = FloatArray(waveform.size * 2)

        for (i in waveform.indices) {
            val amplitude = waveform[i].toFloat()
            val normalizedAmplitude =
                (amplitude + 128) / 256  // Normalize amplitude to range [0, 1]
            val radius =
                maxRadius * normalizedAmplitude  // Radius varies according to the waveform amplitude

            val angle = i * angleIncrement  // Calculate the angle for this point
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)

            points[i * 2] = x
            points[i * 2 + 1] = y
        }

        val startColor = Color(
            red = (sin(time) * 127 + 128).toInt(),
            green = (sin(time + PI / 3) * 127 + 128).toInt(),
            blue = (sin(time + 2 * PI / 3) * 127 + 128).toInt()
        )
        val endColor = Color(
            red = (sin(time + PI) * 127 + 128).toInt(),
            green = (sin(time + PI + PI / 3) * 127 + 128).toInt(),
            blue = (sin(time + PI + 2 * PI / 3) * 127 + 128).toInt()
        )

        drawIntoCanvas {
            it.nativeCanvas.drawPoints(
                points,
                paint.apply {
                    shader = RadialGradient(
                        centerX,
                        centerY,
                        maxRadius,
                        startColor.toArgb(),
                        endColor.toArgb(),
                        Shader.TileMode.CLAMP
                    )
                    strokeWidth = 10f
                    strokeCap = android.graphics.Paint.Cap.ROUND
                    setShadowLayer(
                        shadowRadius,
                        0f,
                        0f,
                        android.graphics.Color.WHITE
                    )
                }
            )
        }
    }
}

fun DrawScope.drawRadiatePointsVariableSize(
    state: VisualizerState,
    time: Float,
    useBeatDetection: Boolean,
    paint: Paint
) {
    val waveform = state.waveform
    if (waveform.isNotEmpty()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        var pointSizeMultiplier = 2f
        var shadowRadius = 10f

        if (useBeatDetection) {
            if (state.fftAvg > 3.2f) {
                pointSizeMultiplier = 25f
                shadowRadius = 55f
            } else {
                pointSizeMultiplier = 5f
                shadowRadius = 5f
            }
        }

        val maxRadius = minOf(size.width, size.height) / 2.5f
        val angleIncrement = (2 * PI / waveform.size).toFloat()  // Angle between each point
        val startColor = Color(
            red = (sin(time) * 127 + 128).toInt(),
            green = (sin(time + PI / 3) * 127 + 128).toInt(),
            blue = (sin(time + 2 * PI / 3) * 127 + 128).toInt()
        )
        // end color as a function of time
        val endColor = Color(
            red = (sin(time + PI) * 127 + 128).toInt(),
            green = (sin(time + PI + PI / 3) * 127 + 128).toInt(),
            blue = (sin(time + PI + 2 * PI / 3) * 127 + 128).toInt()
        )
        val points = FloatArray(waveform.size * 2)
        for (i in waveform.indices) {
            val amplitude = waveform[i].toFloat()
            // Normalizing the amplitude to a range that fits our visualization
            val normalizedAmplitude = norm(amplitude, -128f, 127f)
            val pointSize =
                pointSizeMultiplier + normalizedAmplitude * 8f
            val radius = map(
                normalizedAmplitude,
                0f,
                1f,
                0f,
                maxRadius - pointSize
            )  // Radius varies according to the waveform amplitude
            val angle = i * angleIncrement  // Calculate the angle for this point
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)
            points[i * 2] = x
            points[i * 2 + 1] = y
        }

        drawIntoCanvas {
            it.nativeCanvas.drawPoints(
                points,
                paint.apply {
                    shader = RadialGradient(
                        centerX,
                        centerY,
                        maxRadius,
                        startColor.toArgb(),
                        endColor.toArgb(),
                        Shader.TileMode.CLAMP
                    )
                    strokeWidth = pointSizeMultiplier
                    strokeCap = android.graphics.Paint.Cap.ROUND
                    setShadowLayer(
                        shadowRadius,
                        0f,
                        0f,
                        android.graphics.Color.WHITE
                    )
                }
            )
        }
    }
}

fun DrawScope.drawFlashingLines(
    state: VisualizerState,
    useBeatDetection: Boolean
) {
    val waveform = state.waveform
    val points = FloatArray(waveform.size * 4)
    // Middle of the screen
    val midY = size.height / 2
    // 15% of the screen height above and below the midline
    val maxHeight = size.height * 0.15f

    for (i in 0 until waveform.size - 1) {
        val currentDataPoint = waveform[i]
        val nextDataPoint = waveform[i + 1]

        val startX = size.width * i / (waveform.size - 1)
        // Normalize and scale the waveform around midY
        val startY = midY + (currentDataPoint * maxHeight) / 128

        val endX = size.width * (i + 1) / (waveform.size - 1)
        // Normalize and scale the waveform around midY
        val endY = midY + (nextDataPoint * maxHeight) / 128

        points[i * 4] = startX
        points[i * 4 + 1] = startY
        points[i * 4 + 2] = endX
        points[i * 4 + 3] = endY
    }

    val paint = if (useBeatDetection) {
        if (state.isAmplitudeHigh()) brightPaint else dullPaint
    } else {
        brightPaint
    }
    drawIntoCanvas {
        it.nativeCanvas.drawLines(points, paint)
    }
}

fun DrawScope.drawPolarPoints(
    state: VisualizerState,
    useBeatDetection: Boolean,
    time: Float
) {
    translate(size.width / 2f, size.height / 2f) {
        val waveform = state.waveform
        val vertices = 181

        val points = mutableListOf<Offset>()

        val radiusOffset = if (state.fftAvg > 3.2f) 55f else 15f

        drawArcPoints(vertices, waveform, points, invert = false, radiusOffset = radiusOffset)
        drawArcPoints(vertices, waveform, points, invert = true, radiusOffset = radiusOffset)

        if (useBeatDetection) {
            if (state.isAmplitudeHigh()) {
                val color = Color.hsl(
                    hue = 150f + (time * 50f % 210f),
                    saturation = 0.7f,
                    lightness = 1f)
                drawPoints(
                    points = points,
                    pointMode = PointMode.Points,
                    color = color,
                    strokeWidth = 25f,
                    alpha = 0.8f,
                    cap = StrokeCap.Round
                )
            } else {
                val color = Color.hsl(
                    hue = 250f + (time* 50f  % 110f),
                    saturation = 0.7f,
                    lightness = 0.8f)
                drawPoints(
                    points = points,
                    pointMode = PointMode.Points,
                    color = color,
                    strokeWidth = 10f,
                    alpha = 0.8f,
                    cap = StrokeCap.Round
                )
            }
        } else {
            val color = Color.hsl(
                hue = 150f + (time* 50f  % 210f),
                saturation = 0.7f,
                lightness = 0.8f)
            drawPoints(
                points = points,
                pointMode = PointMode.Points,
                color = color,
                strokeWidth = 15f,
                alpha = 0.8f,
                cap = StrokeCap.Round
            )
        }
    }
}

fun DrawScope.drawPolarPath(
    state: VisualizerState,
    path: Path,
    useBeatDetection: Boolean
) {
    translate(size.width / 2f, size.height / 2f) {
        val waveform = state.waveform
        val vertices = 181

        path.reset()

        val radiusOffset = if (state.fftAvg > 3.3f) 25f else 1f

        drawArcPath(vertices, waveform, path, invert = false, radiusOffset = radiusOffset)
        drawArcPath(vertices, waveform, path, invert = true, radiusOffset = radiusOffset)

        val paint = if (useBeatDetection) {
            if (state.fftAvg > 3.3f) {
                brightPaint.apply {
                    setShadowLayer(
                        45f,
                        0f,
                        0f,
                        android.graphics.Color.WHITE
                    )
                    strokeWidth = 10f
                }
            } else {
                dullPaint.apply {
                    setShadowLayer(
                        5f,
                        0f,
                        0f,
                        android.graphics.Color.LTGRAY
                    )
                    strokeWidth = 4f
                }
            }
        } else {
            brightPaint.apply {
                setShadowLayer(
                    25f,
                    0f,
                    0f,
                    android.graphics.Color.WHITE
                )
                strokeWidth = 10f
            }
        }

        paint.strokeCap = android.graphics.Paint.Cap.ROUND
        paint.style = android.graphics.Paint.Style.STROKE

        drawIntoCanvas {
            it.nativeCanvas.drawPath(path.asAndroidPath(), paint)
        }
    }
}

private fun drawArcPath(
    vertices: Int,
    waveform: ByteArray,
    path: Path,
    invert: Boolean,
    radiusOffset: Float
) {
    val invertMultiplier = if (invert) -1 else 1
    for (i in 0 until vertices) {
        val rad = i * glm.PIf / 180f
        val index = mapInt(i, 0, vertices, 0, (waveform.size - 1))
        val radius = mapInt(waveform[index].toInt(), -128, 127, 250, 400) + radiusOffset

        val x = radius * invertMultiplier * sin(rad)
        val y = radius * cos(rad)
        if (i == 0) {
            path.moveTo(x, y)
        } else {
            path.lineTo(x, y)
        }
    }
}

private fun drawArcPoints(
    vertices: Int,
    waveform: ByteArray,
    points: MutableList<Offset>,
    invert: Boolean,
    radiusOffset: Float
) {
    val invertMultiplier = if (invert) -1 else 1
    for (i in 0 until vertices) {
        val rad = i * glm.PIf / 180f
        val index = mapInt(i, 0, vertices, 0, (waveform.size - 1))
        val radius = mapInt(waveform[index].toInt(), -128, 127, 250, 400) + radiusOffset

        val x = radius * invertMultiplier * sin(rad)
        val y = radius * cos(rad)
        points.add(Offset(x, y))
    }
}

private val brightPaint = android.graphics.Paint().apply {
    color = android.graphics.Color.WHITE
    strokeWidth = 6f
}
private val dullPaint = android.graphics.Paint().apply {
    color = android.graphics.Color.GRAY
    strokeWidth = 3f
}

private val DefaultStroke = Stroke(5f)
