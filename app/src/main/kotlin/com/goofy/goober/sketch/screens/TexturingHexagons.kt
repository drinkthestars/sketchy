package com.goofy.goober.sketch.screens

import android.graphics.RenderEffect
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goofy.goober.shaders.NoiseGrain2
import com.goofy.goober.sketch.CapturableContainer
import com.goofy.goober.sketch.PI
import com.goofy.goober.sketch.TWO_PI
import com.goofy.goober.sketch.drawing.drawQuad
import com.goofy.goober.sketch.ui.Bg1
import com.goofy.goober.sketch.ui.Bg2
import com.goofy.goober.sketch.ui.Silvers
import com.goofy.goober.sketch.ui.Palette2
import com.goofy.goober.sketch.ui.Golds
import com.goofy.goober.style.Slider

private val Padding = 32.dp

@Preview(showBackground = true)
@Composable
fun TexturingHexagons(modifier: Modifier = Modifier) {
    val path = remember { Path() }
    val nbPoints = remember { 6 }
    val numColors = remember { 10 }

    var colors by remember { mutableStateOf(Palette2.shuffled().take(numColors)) }
    var intensity by remember { mutableStateOf(0.15f) }

    CapturableContainer(
        modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp),
        onRefreshClick = {
            colors = Palette2.shuffled().take(numColors)
        }
    ) { onGloballyPositioned ->
        Slider(
            label = "Intensity = $intensity",
            value = intensity,
            onValueChange = {
                intensity = it
            },
            valueRange = 0f..1f
        )
        Spacer(modifier = Modifier.height(24.dp))
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg2)
                .onGloballyPositioned(onGloballyPositioned)
                .onSizeChanged { size ->
                    NoiseGrain2.setFloatUniform(
                        "resolution",
                        size.width.toFloat(),
                        size.height.toFloat()
                    )
                }
                .graphicsLayer {
                    NoiseGrain2.setFloatUniform("intensity", intensity)
                    renderEffect = RenderEffect
                        .createRuntimeShaderEffect(
                            NoiseGrain2,
                            "image"
                        )
                        .asComposeRenderEffect()
                }
                .padding(Padding),
            onDraw = {
                val (width, height) = size

                // ONE
                path.reset()
                drawPolygon(
                    color = colors[0],
                    sides = nbPoints,
                    radius = width * 0.6f,
                    center = this.center,
                    rotation = PI,
                    path = path,
                )
                path.reset()

                // TWO
                drawPolygon(
                    color = colors[1],
                    sides = nbPoints,
                    radius = width * 0.4f,
                    center = this.center,
                    rotation = PI,
                    path = path,
                )

                // THREE
                path.reset()
                drawPolygon(
                    color = colors[2],
                    sides = nbPoints,
                    radius = width * 0.3f,
                    center = this.center,
                    rotation = PI,
                    path = path,
                )
                // Hexes
                path.reset()
                drawPolygon(
                    color = colors[3],
                    sides = nbPoints,
                    radius = width * 0.2f,
                    center = this.center,
                    rotation = PI,
                    path = path,
                )
                path.reset()
                drawPolygon(
                    color = colors[2],
                    sides = 3,
                    radius = width * 0.21f,
                    center = this.center,
                    rotation = PI,
                    path = path,
                    closeHalf = true
                )
//                // FOUR - Triangle
                path.reset()
                drawPolygon(
                    color = colors[8],
                    sides =  nbPoints / 2,
                    radius = width * 0.2f,
                    center = this.center,
                    rotation = TWO_PI,
                    path = path,
                )

                // FIVE - Triangle
                path.reset()
                drawQuad(
                    color = colors[4],
                    sides =  nbPoints / 2,
                    radius = width * 0.15f,
                    center = this.center,
                    rotation = TWO_PI,
                    path = path,
                )

//                // SIX
//                path.reset()
//                drawQuad(
//                    color = colorQuad[5],
//                    sides =  nbPoints,
//                    radius = width * 0.15f/2,
//                    center = this.center,
//                    rotation = glm.PIf/3,
//                    path = path,
//                )

                // LINE
                val topLeft1 = Offset(
                    center.x - 4f,
                    center.y - width * 0.6f
                )
                drawRect(
                    brush = Brush.linearGradient(Golds),
                    topLeft = topLeft1,
                    size = Size(8f, width * 0.3f)
                )

                val topLeft2 = Offset(
                    center.x - 4f,
                    center.y + width * 0.3f
                )
                drawRect(
                    brush = Brush.linearGradient(Silvers),
                    topLeft = topLeft2,
                    size = Size(8f, width * 0.3f)
                )
            }
        )
    }
}
