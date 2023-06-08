package com.goofy.goober.sketchy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.StatefulCanvas
import com.goofy.goober.sketchy.PI
import com.goofy.goober.sketchy.common.Bg1
import com.goofy.goober.sketchy.common.ColorPaletteViewer
import com.goofy.goober.sketchy.common.InteractiveContainer
import com.goofy.goober.sketchy.common.Palette3
import com.goofy.goober.sketchy.common.PaletteBlues
import com.goofy.goober.sketchy.common.drawPolygon
import com.goofy.goober.sketchy.distanceTo
import com.goofy.goober.style.Sizing

@Preview(showBackground = true)
@Composable
fun PolygonsTouchInteractive(modifier: Modifier = Modifier) {
    val path = remember { Path() }
    var colors by remember { mutableStateOf(PaletteBlues.shuffled().take(2)) }
    var center by remember { mutableStateOf<Offset?>(null) }

    val centerDiff = remember { 25f }

    InteractiveContainer(
        modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp),
        onAddClick = {
        },
        onRefreshClick = {
            colors = Palette3.shuffled().take(2)
        },
        onShapeChangeClick = {
        },
    ) { onGloballyPositioned ->
        ColorPaletteViewer(colors.take(2))
        StatefulCanvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg1)
                .onGloballyPositioned {
                    onGloballyPositioned(it)
                }
                .padding(Sizing.Eight)
                .pointerInput(Unit) {
                    detectDragGestures { change, _ ->
                        center?.apply {
                            if (this.distanceTo(change.position) > centerDiff) {
                                center = change.position
                            }
                        } ?: run {
                            center = change.position
                        }
                    }
                },
            onDraw = {
                val (width, height) = size

                path.reset()

//                    val vertex4 = Offset(x - width * vertex4Offset, y + height * vertex3Offset)

//                    drawQuad(
//                        vertex1 = vertex1,
//                        vertex2 = vertex2,
//                        vertex3 = vertex3,
//                        vertex4 = vertex4,
//                        path = path,
//                        color = color,
//                        alpha = alpha,
//                    )

                center?.apply {
                    // rotation based on offset.y
                    val rotation = this.y / height * 2 * PI
                    // radius based on offset.y
                    val radius = this.y / height * width * 0.5f
                    // center based on offset
                    // alpha based on offset.y
                    val alpha = (this.y / height).coerceIn(0f, 0.8f)


                    /**
                     * Mirrored X-coordinate: mirrorX = canvasWidth - x
                     * Mirrored Y-coordinate: mirrorY = canvasHeight - y
                     */


                    /**
                     * Mirrored X-coordinate: mirrorX = canvasWidth - x
                     * Mirrored Y-coordinate: mirrorY = canvasHeight - y
                     */
                    /**
                     * Mirrored X-coordinate: mirrorX = canvasWidth - x
                     * Mirrored Y-coordinate: mirrorY = canvasHeight - y
                     */
                    /**
                     * Mirrored X-coordinate: mirrorX = canvasWidth - x
                     * Mirrored Y-coordinate: mirrorY = canvasHeight - y
                     */
                    val mirrorX = width - this.x
                    val mirrorY = height - this.y

                    println("WARP mirrorX: $mirrorX, mirrorY: $mirrorY")

                    val mirrorCenter = Offset(mirrorX, mirrorY)
                    val mirrorRadius = (height - mirrorY) / height * width * 0.5f
                    val mirrorAlpha = ((height - mirrorY) / height).coerceIn(0f, 0.8f)
                    val mirrorRotation = -rotation

                    drawPolygon(
                        color = colors[0],
                        vertices = 3,
                        radius = radius,
                        center = this,
                        rotation = rotation,
                        path = path,
                        alpha = alpha,
                    )
                    drawPolygon(
                        color = colors[1],
                        vertices = 3,
                        radius = mirrorRadius,
                        center = mirrorCenter,
                        rotation = mirrorRotation,
                        path = path,
                        alpha = mirrorAlpha,
                    )
                }
//
//                // ONE
//                path.reset()
//                drawPolygon(
//                    color = colorQuad[0],
//                    vertices = vertices,
//                    radius = width * 0.6f,
//                    center = this.center,
//                    rotation = PI,
//                    path = path,
//                )
//
//
//                path.reset()
//
//                // TWO
//                drawPolygon(
//                    color = colorQuad[1],
//                    vertices = vertices,
//                    radius = width * 0.4f,
//                    center = this.center,
//                    rotation = PI,
//                    path = path,
//                )
//
//                // THREE
//                path.reset()
//                drawPolygon(
//                    color = colorQuad[2],
//                    vertices = vertices,
//                    radius = width * 0.3f,
//                    center = this.center,
//                    rotation = PI,
//                    path = path,
//                )
//                // Hexes
//                path.reset()
//                drawPolygon(
//                    color = colorQuad[3],
//                    vertices = vertices,
//                    radius = width * 0.2f,
//                    center = this.center,
//                    rotation = PI,
//                    path = path,
//                )
//                path.reset()
//                drawPolygon(
//                    color = colorQuad[2],
//                    vertices = 3,
//                    radius = width * 0.21f,
//                    center = this.center,
//                    rotation = PI,
//                    path = path,
//                    closeHalf = true
//                )
//                // FOUR - Triangle
//                path.reset()
//                drawQuad(
//                    color = colorQuad[3],
//                    vertices =  vertices / 2,
//                    radius = width * 0.3f,
//                    center = this.center,
//                    rotation = map(
//                        sin(time), -1f, 1f, 0f, TWO_PI
//                    ),
//                    path = path,
//                )
//
//                // FIVE - Triangle
//                path.reset()
//                drawQuad(
//                    color = colorQuad[4],
//                    vertices =  vertices / 2,
//                    radius = width * 0.15f,
//                    center = this.center,
//                    rotation = TWO_PI,
//                    path = path,
//                )

//                // SIX
//                path.reset()
//                drawQuad(
//                    color = colorQuad[5],
//                    vertices =  vertices,
//                    radius = width * 0.15f/2,
//                    center = this.center,
//                    rotation = glm.PIf/3,
//                    path = path,
//                )

                // LINE
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
