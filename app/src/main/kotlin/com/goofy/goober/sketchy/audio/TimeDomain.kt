package com.goofy.goober.sketchy.audio

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.nativeCanvas
import com.goofy.goober.sketchy.map
import com.goofy.goober.sketchy.mapInt
import com.goofy.goober.sketchy.norm
import glm_.func.common.abs
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

/**
 * Will draw a line for each data point in [state#waveform]. Similar to [drawBasicLines], but will draw
 * the +ve values and a mirrored version of the +ve values below the center to mimic more of what
 * audio waveforms look like in different audio software. This discards the -ve values as a result.
 */
fun DrawScope.drawMirroredWaveform(
    state: VisualizerState,
    useBeatDetection: Boolean
) {
    val screenHeight = size.height
    val waveform = state.waveform
    val brush = Brush.verticalGradient(
        colors = listOf(Color.Green.copy(alpha = 0.4f), Color.Blue.copy(alpha = 0.4f)),
        startY = 0f,
        endY = size.height
    )
    val strokeWidth = if (useBeatDetection && state.isAmplitudeHigh()) 10f else 2f

    val screenWidth = size.width
    val centerY = screenHeight / 2f
    val amplitudeScale =
        (screenHeight * 0.2f) / 128f  // Scale to fit 60% of the screen height

    // Draw lines for each data point
    for (i in waveform.indices) {
        val x = i * (screenWidth / waveform.size.toFloat())
        val y = centerY - (waveform[i] * amplitudeScale)
        val mirroredY =
            centerY + (waveform[i] * amplitudeScale) // Mirroring the waveform below the center

        drawLine(
            brush = brush,
            start = Offset(x, y),
            end = Offset(x, centerY),
            strokeWidth = strokeWidth  // Adjust the stroke width if needed
        )
        drawLine(
            brush = brush,
            start = Offset(x, mirroredY),
            end = Offset(x, centerY),
            strokeWidth = strokeWidth  // Adjust the stroke width if needed
        )
    }
}

fun DrawScope.drawRects(
    state: VisualizerState,
    useBeatDetection: Boolean
) {
    val waveform = state.waveform
    val screenHeight = size.height
    val screenWidth = size.width
    val centerY = screenHeight / 2f

    // Define the width of each rectangle
    val rectWidth = screenWidth / waveform.size.toFloat()

    for (i in waveform.indices) {
        val x = i * rectWidth
        val rectHeight =
            waveform[i].abs * 0.7f  // Use absolute value to ensure all rects are positive
        val topY = centerY - rectHeight  // Start drawing from the center and go up
        val bottomY = centerY + rectHeight  // Start drawing from the center and go down

        val alpha = if (useBeatDetection && state.isAmplitudeHigh()) 1f else 0.5f

        // Draw rectangle extending above the center
        drawRect(
            color = Color.Yellow.copy(alpha),
            topLeft = Offset(x, topY),
            size = Size(width = rectWidth, height = rectHeight),
            alpha = 1.0f
        )

        // Draw rectangle extending below the center (mirrored)
        drawRect(
            color = Color.Yellow.copy(alpha),
            topLeft = Offset(x, centerY),  // Start at the center line
            size = Size(width = rectWidth, height = rectHeight),
            alpha = 1.0f
        )
    }
}

fun DrawScope.drawPoints(
    state: VisualizerState,
    useBeatDetection: Boolean
) {
    val waveform = state.waveform
    val screenHeight = size.height
    val screenWidth = size.width
    val centerY = screenHeight / 2f
    val amplitudeScale = (screenHeight * 0.1f) / 128f  // Normalize and scale the waveform data

    // Collect points for the original and mirrored waveform
    val points = mutableListOf<Offset>()
    for (i in waveform.indices) {
        val x = i * (screenWidth / waveform.size.toFloat())
        val y = centerY - (waveform[i] * amplitudeScale)
        val mirroredY = centerY + (waveform[i] * amplitudeScale)  // Mirrored point

        // Add point for the upper half
        points.add(Offset(x, y))

        // Add point for the lower half (mirrored)
        points.add(Offset(x, mirroredY))
    }

    val alpha = if (useBeatDetection) {
        if (state.isAmplitudeHigh()) 1f else 0.5f
    } else {
        1f
    }

    // Draw points
    drawPoints(
        points = points,
        pointMode = PointMode.Polygon,
        pathEffect = PathEffect.cornerPathEffect(22f),
        brush = Brush.verticalGradient(
            colors = listOf(Color.Green.copy(alpha), Color.Blue.copy(alpha)),
            startY = 0f,
            endY = size.height
        ),
        strokeWidth = 5f,  // Size of each point
        cap = StrokeCap.Round
    )
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
    useBeatDetection: Boolean
) {
    val waveform = state.waveform
    if (waveform.isNotEmpty()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radiusOffset = if (useBeatDetection) {
            if (state.isAmplitudeHigh()) 2f else 3f
        } else {
            3f
        }
        val maxRadius = minOf(size.width, size.height) / radiusOffset  // Maximum spike length
        val baseRadius = 10f  // Minimum spike length
        val angleIncrement = (2 * PI / waveform.size).toFloat()  // Angle between each spike
        val strokeWidth = 5f

        for (i in waveform.indices) {
            val amplitude = waveform[i].toFloat()  // Current sample amplitude
            val normalizedAmplitude =
                (amplitude + 128) / 256  // Normalize amplitude to range [0, 1]
            val lineLength = baseRadius + normalizedAmplitude * maxRadius  // Calculate line length
            val angle = i * angleIncrement  // Calculate the angle for the spike
            val endX = centerX + lineLength * cos(angle)
            val endY = centerY + lineLength * sin(angle)

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

            drawLine(
                brush = Brush.radialGradient(
                    colors = listOf(startColor, endColor),
                    center = Offset(centerX, centerY),
                    radius = maxRadius
                ),
                alpha = 0.6f,
                start = Offset(centerX, centerY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }
    }
}

/**
 * THIS ONE IS AMAZING
 */
fun DrawScope.drawRadiatePoints(
    state: VisualizerState,
    time: Float,
    useBeatDetection: Boolean
) {
    val waveform = state.waveform
    if (waveform.isNotEmpty()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val radiusMultiplier = if (useBeatDetection) {
            if (state.isAmplitudeHigh()) 2f else 3f
        } else {
            3f
        }
        val maxRadius = minOf(size.width, size.height) / radiusMultiplier

        val angleIncrement = (2 * PI / waveform.size).toFloat()  // Angle between each point

        // List to hold all the points
        val points = mutableListOf<Offset>()

        for (i in waveform.indices) {
            val amplitude = waveform[i].toFloat()
            val normalizedAmplitude =
                (amplitude + 128) / 256  // Normalize amplitude to range [0, 1]
            val radius =
                maxRadius * normalizedAmplitude  // Radius varies according to the waveform amplitude

            val angle = i * angleIncrement  // Calculate the angle for this point
            val x = centerX + radius * cos(angle)
            val y = centerY + radius * sin(angle)

            points.add(Offset(x, y))  // Add the calculated point to the list
        }

        // start color as a function of time
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

        // Draw all points with varying sizes based on their amplitude for added effect
        drawPoints(
            points = points,
            pointMode = PointMode.Points,
            brush = Brush.radialGradient(
                colors = listOf(startColor, endColor),
                center = Offset(centerX, centerY),
                radius = maxRadius
            ),
            strokeWidth = 8f,
            cap = StrokeCap.Round
        )
    }
}

fun DrawScope.drawRadiatePointsVariableSize(
    state: VisualizerState,
    time: Float,
    useBeatDetection: Boolean
) {
    val waveform = state.waveform
    if (waveform.isNotEmpty()) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val pointSizeMultiplier = if (useBeatDetection) {
            if (state.isAmplitudeHigh()) 15f else 2f
        } else {
            2f
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
        val brush = Brush.radialGradient(
            colors = listOf(startColor, endColor),
            center = Offset(centerX, centerY),
            radius = maxRadius
        )
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

            // start color as a function of time
            // Draw each point individually with its size
            drawPoints(
                points = listOf(Offset(x, y)),
                pointMode = PointMode.Points,
                brush = brush,
                strokeWidth = pointSize,
                cap = StrokeCap.Round
            )
        }
    }
}

private fun DrawScope.drawPointsFlashed(state: VisualizerState) {
    val waveform = state.waveform

    // Calculate spacing between points
    val pointSpacing = size.width / waveform.size.toFloat()
    val midY = size.height / 2
    val maxHeight = size.height * 0.15f  // This sets the amplitude to 30% of the screen height

    for (i in waveform.indices) {
        val x = i * pointSpacing
        val oppositeDataPoint = waveform[i].toFloat() * -1
        val y =
            midY + (oppositeDataPoint * maxHeight) / 128f  // Correct mapping of signed byte to screen coordinates

        // Calculate amplitude for this waveform
        var accumulator = 0f
        for (byte in waveform) {
            accumulator += abs(byte.toFloat())  // Calculate the absolute sum of the waveform samples
        }
        val amp: Float = accumulator / (128 * waveform.size)  // Normalize the amplitude calculation

        drawIntoCanvas {
            val paint = if (amp > state.lastAmp) {
                // Amplitude is bigger than normal, use a prominent color
                state.lastAmp = amp
                brightPaint  // Assume paint1 is set to a more prominent color
            } else {
                // Amplitude is nothing special, reduce the amplitude
                state.lastAmp *= 0.99f
                dullPaint  // Assume paint2 is a less prominent color
            }
            it.nativeCanvas.drawPoint(x, y, paint)
        }
    }
}

private fun DrawScope.drawLinesFlashed(state: VisualizerState) {
    val waveform = state.waveform

    // Calculate points for line
    val points = FloatArray(waveform.size * 4)
    val midY = size.height / 2
    val maxHeight = size.height * 0.15f  // This sets the amplitude to 30% of the screen height

    for (i in 0 until waveform.size - 1) {

        // Calculations from [VisualizerView]
        val startX = (size.width * i / (waveform.size - 1)).toFloat()
        val startY = (size.height / 2
                + (waveform[i] + 128).toByte() * (size.height / 2) / 128).toFloat()

        val endX = (size.width * (i + 1) / (waveform.size - 1)).toFloat()
        val endY = (size.height / 2
                + (waveform[i + 1] + 128).toByte() * (size.height / 2) / 128).toFloat()

        points[i * 4] = startX
        points[i * 4 + 1] = startY
        points[i * 4 + 2] = endX
        points[i * 4 + 3] = endY


        // Old calculations

//        points[i * 4] = size.width * i / (waveform.size - 1)
//        points[i * 4 + 1] = midY + (waveform[i] * maxHeight / 128)  // Normalize and scale the waveform around midY
//        points[i * 4 + 2] = size.width * (i + 1) / (waveform.size - 1)
//        points[i * 4 + 3] = midY + (waveform[i + 1] * maxHeight / 128)
    }

    // Calculate amplitude for this waveform
    var accumulator = 0f
    for (byte in waveform) {
        accumulator += abs(byte.toFloat())  // Calculate the absolute sum of the waveform samples
    }
    val amp: Float = accumulator / (128 * waveform.size)  // Normalize the amplitude calculation

    drawIntoCanvas {
        if (amp > state.lastAmp) {
            // Amplitude is bigger than normal, make a prominent line
            state.lastAmp = amp
            it.nativeCanvas.drawLines(points, brightPaint)
        } else {
            // Amplitude is nothing special, reduce the amplitude
            state.lastAmp *= 0.99f
            it.nativeCanvas.drawLines(points, dullPaint)
        }
    }
}

fun DrawScope.drawLinesFlashed2(
    state: VisualizerState,
    useBeatDetection: Boolean
) {
    val waveform = state.waveform
    val points = FloatArray(waveform.size * 4)
    val midY = size.height / 2  // Middle of the screen
    val maxHeight = size.height * 0.15f  // 15% of the screen height above and below the midline

    drawLine(
        color = Color.Red,
        start = Offset(0f, midY),
        end = Offset(size.width, midY),
        strokeWidth = 2f
    )

    for (i in 0 until waveform.size - 1) {
        val currentDataPoint = waveform[i]
        val nextDataPoint = waveform[i + 1]
        val startX = size.width * i / (waveform.size - 1)
        val startY =
            midY + (currentDataPoint * maxHeight) / 128 // Normalize and scale the waveform around midY
        val endX = size.width * (i + 1) / (waveform.size - 1)
        val endY = midY + (nextDataPoint * maxHeight) / 128
        points[i * 4] = startX
        points[i * 4 + 1] = startY
        points[i * 4 + 2] = endX  // x coordinate for the end of the line segment
        points[i * 4 + 3] = endY  // y coordinate for the end, scaled

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

fun DrawScope.drawCirclePoints(
    state: VisualizerState,
    useBeatDetection: Boolean
) {
    translate(size.width / 2f, size.height / 2f) {
        val waveform = state.waveform
        val vertices = 181

        val points = mutableListOf<Offset>()

        val radiusOffset = if (state.isBeat) 20f else 0f

        drawArcPoints(vertices, waveform, points, invert = false, radiusOffset = radiusOffset)
        drawArcPoints(vertices, waveform, points, invert = true, radiusOffset = radiusOffset)

        if (useBeatDetection) {
            if (state.isAmplitudeHigh()) {
                val color = Color(red = 255, green = 255, blue = 255, alpha = 255)
                drawPoints(
                    points = points,
                    pointMode = PointMode.Points,
                    color = color,
                    strokeWidth = 15f,
                    cap = StrokeCap.Round
                )
            } else {
                val color = Color(red = 128, green = 128, blue = 128, alpha = 255)
                drawPoints(
                    points = points,
                    pointMode = PointMode.Points,
                    color = color,
                    strokeWidth = 2f,
                    cap = StrokeCap.Round
                )
            }
        } else {
            Color.White
            drawPoints(
                points = points,
                pointMode = PointMode.Points,
                color = Color.White,
                strokeWidth = 5f,
                cap = StrokeCap.Round
            )
        }
    }
}

fun DrawScope.drawCirclePath(
    state: VisualizerState,
    path: Path,
    useBeatDetection: Boolean
) {
    translate(size.width / 2f, size.height / 2f) {
        val waveform = state.waveform
        val vertices = 181

        path.reset()

        val radiusOffset = if (state.isBeat) 20f else 0f

        drawArcPath(vertices, waveform, path, invert = false, radiusOffset = radiusOffset)
        drawArcPath(vertices, waveform, path, invert = true, radiusOffset = radiusOffset)

        val color = if (useBeatDetection) {
            if (state.isAmplitudeHigh()) {
                Color.White
            } else {
                Color.Gray
            }
        } else {
            Color.White
        }

        drawPath(
            path = path,
            color = color,
            style = DefaultStroke,
            alpha = 1f
        )
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
        val radius = mapInt(waveform[index].toInt(), -128, 127, 250, 350) + radiusOffset

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
        val radius = mapInt(waveform[index].toInt(), -128, 127, 250, 350) + radiusOffset

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

private val DefaultStroke = Stroke(2f)
