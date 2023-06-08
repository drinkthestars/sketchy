package com.goofy.goober.sketchy

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.roundToInt

@Composable
fun StatefulCanvas(
    modifier: Modifier = Modifier,
    drawState: DrawState = remember { DrawState() },
    onDraw: DrawScope.() -> Unit
) {
    Box(
        modifier = modifier
            .drawWithCache {
                val drawContext = drawState.createDrawContext(size)
                drawContext.drawScope.draw(
                    density = this,
                    layoutDirection = layoutDirection,
                    canvas = drawContext.canvas,
                    size = size
                ) {
                    onDraw()
                }

                onDrawBehind {
                    drawImage(drawContext.imageBitmap)
                }
            }
    )
}

class DrawState {
    val state = mutableStateOf<DrawContext?>(null)

    fun createDrawContext(size: Size): DrawContext {
        var drawContext = state.value
        if (drawContext == null) {
            val imageBitmap = ImageBitmap(size.width.roundToInt(), size.height.roundToInt())
            val canvas = Canvas(imageBitmap)
            val drawScope = CanvasDrawScope()
            drawContext = DrawContext(imageBitmap, canvas, drawScope)
            state.value = drawContext
        }
        return drawContext
    }
}

data class DrawContext(
    val imageBitmap: ImageBitmap,
    val canvas: Canvas,
    val drawScope: CanvasDrawScope
)
