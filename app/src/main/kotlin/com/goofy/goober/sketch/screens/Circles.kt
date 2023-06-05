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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketch.CanvasRecorder
import com.goofy.goober.sketch.CapturableContainer
import com.goofy.goober.sketch.PI
import com.goofy.goober.sketch.ui.Bg2
import com.goofy.goober.sketch.ui.PaletteBright3
import com.goofy.goober.sketch.ui.PaletteGreens
import com.goofy.goober.sketch.ui.Silver
import kotlin.math.pow

private val Padding = 32.dp
private val Rad360 = 2f * PI / 360f

@Preview(showBackground = true)
@Composable
fun Circles(modifier: Modifier = Modifier) {
    val count = remember { mutableStateOf(35) }
    val colors = remember {
        listOf(
            Silver,
            Color(0xff262623),
        )
    }

    CapturableContainer(
        modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp)
    ) { onGloballyPositioned ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg2)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Padding),
            onDraw = {
                val (width, height) = size

                // ONE
                // drawQuad count times
                for (i in 0 until count.value) {
                    val ratio = i.toFloat() / count.value.toFloat()
                    val radius = width * 0.5f * (1f - ratio.pow(2))
                    drawCircle(
                        color = colors[i % colors.size],
                        radius = radius,
                        alpha = ratio.pow(2)
                    )
                }

//                // LINE
//                val topLeft1 = Offset(
//                    center.x - 4f,
//                    center.y - width * 0.6f
//                )
//                drawRect(
//                    brush = Brush.linearGradient(Golds),
//                    topLeft = topLeft1,
//                    size = Size(8f, width * 0.3f)
//                )
//
//                val topLeft2 = Offset(
//                    center.x - 4f,
//                    center.y + width * 0.3f
//                )
//                drawRect(
//                    brush = Brush.linearGradient(Silvers),
//                    topLeft = topLeft2,
//                    size = Size(8f, width * 0.3f)
//                )
            }
        )
    }
}
