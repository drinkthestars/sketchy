package com.goofy.goober.sketch.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketch.CanvasRecorder
import com.goofy.goober.sketch.CapturableContainer
import com.goofy.goober.sketch.drawing.drawParallelogram
import com.goofy.goober.sketch.drawing.drawQuad
import com.goofy.goober.sketch.ui.Bg1
import kotlin.math.pow

private val Padding = 16.dp

@Preview(showBackground = true)
@Composable
fun Quads(modifier: Modifier = Modifier) {
    val path = remember { Path() }
    val count = remember { mutableStateOf(5) }
    val colors = remember {
        listOf(
            Color(0xff539962)
        )
    }

    CapturableContainer(
        modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp)
    ) { onGloballyPositioned ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg1)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Padding),
            onDraw = {
                val (width, height) = size

                val shiftFactor = 0.1f
                val verticalShift = height * shiftFactor

                for (i in 0 until count.value) {
                    path.reset()
                    val ratio = i.toFloat() / count.value.toFloat()
                    val x = width * 0.5f
                    val y = i * verticalShift + height * 0.4f

                    val vertex1 = Offset(x, y)
                    val vertex2 = Offset(x + width / 3f , y)
                    val vertex3 = Offset(x + width * 0.7f, y + height)
                    val vertex4 = Offset(x - width * 0.3f, y + height)

                    drawQuad(
                        vertex1 = vertex1,
                        vertex2 = vertex2,
                        vertex3 = vertex3,
                        vertex4 = vertex4,
                        path = path,
                        color = colors[i % colors.size],
                        alpha = ratio * 0.8f,
                    )
                }
            }
        )
    }
}
