package com.goofy.goober.sketchy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.StatefulCanvas
import com.goofy.goober.sketchy.common.Bg1
import com.goofy.goober.sketchy.common.ColorPaletteViewer
import com.goofy.goober.sketchy.common.InteractiveContainer
import com.goofy.goober.sketchy.common.Palette3
import com.goofy.goober.sketchy.common.PaletteBright2
import com.goofy.goober.sketchy.common.Triangle
import com.goofy.goober.sketchy.common.drawTriangle
import com.goofy.goober.style.Sizing
import kotlin.random.Random

@Preview(showBackground = true)
@Composable
fun PolygonsInteractive(modifier: Modifier = Modifier) {
    val path = remember { Path() }
    var triangle by remember {
        mutableStateOf(
            Triangle(
                v1Offset = 0f,
                v2Offset = 0f,
                v3Offset = 0f,
                alpha = 0f,
                color = Color.Transparent,
            )
        )
    }
    var colors by remember { mutableStateOf(PaletteBright2.shuffled().take(2)) }

    InteractiveContainer(
        modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp),
        onAddClick = {
            val vertex2Offset = Random.nextFloat()
            val vertex3Offset = Random.nextFloat()
            val vertex4Offset = Random.nextFloat()

            triangle = Triangle(
                vertex2Offset,
                vertex3Offset,
                vertex4Offset,
                alpha = 0.23f,
                color = colors[Random.nextInt(colors.size)],
            )
        },
        onRefreshClick = {
            colors = Palette3.shuffled().take(2)
        },
    ) { onGloballyPositioned ->
        ColorPaletteViewer(colors.take(2))
        StatefulCanvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg1)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Sizing.Eight),
            onDraw = {
                val (width, height) = size

                with(triangle) {
                    path.reset()

                    val vertex1 = Offset(
                        width / 2 - (v1Offset * width / 2),
                        height / 2 - (v2Offset * height / 2)
                    )
                    val vertex2 = Offset(
                        width / 2 + (v1Offset * width / 2),
                        height / 2 + (v3Offset * height / 2)
                    )
                    val vertex3 = Offset(
                        width / 2 - (v3Offset * width / 2),
                        height / 2 + (v1Offset * height / 2)
                    )

                    drawTriangle(
                        vertex1 = vertex1,
                        vertex2 = vertex2,
                        vertex3 = vertex3,
                        path = path,
                        color = color,
                        alpha = alpha,
//                        style = Stroke(width = 2.dp.toPx()),
                    )
                }

            }
        )
    }
}
