package com.goofy.goober.sketchy.screens

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
import com.goofy.goober.sketchy.common.Bg1
import com.goofy.goober.sketchy.common.InteractiveContainer
import com.goofy.goober.sketchy.common.drawParallelogram

import com.goofy.goober.style.Sizing.Six

//val vertex1 = Offset(50f, 50f)
//val vertex2 = Offset(150f, 50f)
//val vertex3 = Offset(100f, 100f)
//val vertex4 = Offset(0f, 100f)

@Preview(showBackground = true)
@Composable
fun Parallelograms(modifier: Modifier = Modifier) {
    val path = remember { Path() }
    val vertices = remember { 3 }
    val count = remember { mutableStateOf(5) }
    val colors = remember {
        listOf(
            Color(0xff7f9b9c)
        )
    }

    InteractiveContainer(
        modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp)
    ) { onGloballyPositioned ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg1)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Six),
            onDraw = {
                val (width, height) = size

                val shiftFactor = 0.1f
                val verticalShift = height * shiftFactor
                val skew = 0.7f

                for (i in 0 until count.value) {
                    path.reset()
                    val ratio = i.toFloat() / count.value.toFloat()
                    val y = i * verticalShift + height * 0.4f

//                    val vertex1 = Offset(x, y)
//                    val vertex2 = Offset(x + width / 3f , y)
//                    val vertex3 = Offset(x + width * 0.7f, y + height)
//                    val vertex4 = Offset(x - width * 0.3f, y + height)
//
//                    val sizeX = width * 0.5f * (1f - ratio.pow(2))
                    val sizeX = width * 0.5f
                    val x = (width / 2f) + sizeX / 5f
                    val center = Offset(x, y)

                    drawParallelogram(
                        center = center,
                        radius = sizeX / 2f,
                        skew = skew,
                        color = colors[i % colors.size],
                        alpha = (ratio * 0.8f),
                        path = path
                    )

//                    drawQuad(
//                        vertex1 = vertex1,
//                        vertex2 = vertex2,
//                        vertex3 = vertex3,
//                        vertex4 = vertex4,
//                        path = path,
//                        color = colors[i % colors.size],
//                        alpha = ratio * 0.8f,
//                    )
                }
            }
        )
    }
}
