package com.goofy.goober.sketchy.screens.slides

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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goofy.goober.shaders.NoiseGrain1
import com.goofy.goober.sketchy.PI
import com.goofy.goober.sketchy.common.Bg1
import com.goofy.goober.sketchy.common.InteractiveContainer
import com.goofy.goober.sketchy.common.Palette1
import com.goofy.goober.sketchy.common.TextureControls
import com.goofy.goober.sketchy.common.drawPolygon
import com.goofy.goober.style.Sizing.Six
import kotlin.math.pow

@Preview(showBackground = true)
@Composable
fun GeneratePositions(modifier: Modifier = Modifier) {
    val path = remember { Path() }
    var vertices by remember { mutableIntStateOf(3) }
    val count = remember { mutableIntStateOf(10) }
    var colors by remember { mutableStateOf(Palette1.take(2)) }
    var intensity by remember { mutableFloatStateOf(0.15f) }
    var texturingEnabled by remember { mutableStateOf(false) }
    var alpha by remember { mutableFloatStateOf(0.5f) }

    InteractiveContainer(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp),
        onRefreshClick = {
            colors = Palette1.shuffled().take(2)
        },
        onArrowUpClick = {
            alpha = (alpha + 0.1f).coerceAtMost(1f)
        },
        onArrowDownClick = {
            alpha = (alpha - 0.1f).coerceAtLeast(0.05f)
        },
        onAddClick = {
            count.value = (count.value + 1).coerceAtMost(50)
        },
        onRemoveClick = {
            count.value = (count.value - 1).coerceAtLeast(1)
        },
        onShapeChangeClick = {
            vertices = if (vertices == 10) {
                3
            } else {
                (vertices + 1).coerceAtMost(10)
            }
        },
    ) { onGloballyPositioned ->
        TextureControls(
            onTexturingToggled = { texturingEnabled = it },
            onIntensityChanged = { intensity = it }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg1)
                .onGloballyPositioned(onGloballyPositioned)
                .onSizeChanged { size ->
                    NoiseGrain1.setFloatUniform(
                        "resolution",
                        size.width.toFloat(),
                        size.height.toFloat()
                    )
                }
                .graphicsLayer {
                    NoiseGrain1.setFloatUniform("intensity", intensity)
                    if (texturingEnabled) {
                        renderEffect = RenderEffect
                            .createRuntimeShaderEffect(
                                NoiseGrain1,
                                "image"
                            )
                            .asComposeRenderEffect()
                    }
                }
                .padding(Six),
            onDraw = {
                val (width, height) = size
                for (i in 0 until count.value) {
                    path.reset()
                    val ratio = i.toFloat() / count.value.toFloat()
                    val yOffset = height * 0.5f * ratio
                    val centerY = height * 0.6f - yOffset
                    val radius = width * 0.5f * (1f - ratio.pow(2))

                    drawPolygon(
                        color = colors[i % colors.size],
                        vertices = vertices,
                        radius = radius,
                        center = Offset(
                            width / 2f,
                            centerY
                        ),
                        rotation = if (i % 2 == 0) PI else 0f,
                        alpha = alpha,
                        path = path,
                    )
                }
            }
        )
    }

}
