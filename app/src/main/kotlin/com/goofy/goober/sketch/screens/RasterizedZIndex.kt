package com.goofy.goober.sketch.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import com.goofy.goober.sketch.R
import com.goofy.goober.sketch.map

private const val XSamples = 90f

@Composable
fun RasterizedZIndex() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        val imageBitmap = createImageBitmap()
        var size by remember { mutableStateOf(IntSize.Zero) }
        var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(imageBitmap, modifier = Modifier
                .onGloballyPositioned {
                    size = it.size
                    boundsInWindow = it.boundsInWindow()
                }
            )
        }
    }
}

/**
 * Sample showing how to obtain a [PixelMap] to query pixel information
 * from an underlying [ImageBitmap]
 */
@Composable
private fun Image(imageBitmap: ImageBitmap, modifier: Modifier = Modifier) {
    var offsetY by remember { mutableStateOf(0f) }

    val pixelMap = imageBitmap.toPixelMap(
        width = imageBitmap.width, height = imageBitmap.height
    )
    val canvasSize = with(LocalDensity.current) {
        DpSize(imageBitmap.width.toDp(), imageBitmap.height.toDp())
    }
    val dotCount: Float = imageBitmap.width.toFloat() / XSamples

    Canvas(modifier = modifier
        .size(canvasSize)
        .pointerInput(Unit) {
            detectDragGestures { _, dragAmount ->
                val originalY = offsetY
                val summedY = originalY + dragAmount.y
                val newValueY = summedY.coerceIn(0f, imageBitmap.height.toFloat())
                offsetY = newValueY

            }
        }
    ) {
        translate(imageBitmap.width / 2f, imageBitmap.height / 2f) {
            for (x in 0 until imageBitmap.width step dotCount.toInt()) {
                for (y in 0 until imageBitmap.height step dotCount.toInt()) {
                    val pixel = pixelMap[x, y]
                    drawZIndexOffset(
                        pixel = pixel,
                        y = y,
                        imageBitmap = imageBitmap,
                        offsetY = offsetY,
                        x = x,
                        dotCount = dotCount
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawZIndexOffset(
    imageBitmap: ImageBitmap,
    pixel: Color,
    y: Int,
    x: Int,
    dotCount: Float,
    offsetY: Float,
) {
    val luminance = (1f - pixel.luminance())

    val trueX = x - imageBitmap.width / 2f
    val trueY = y - imageBitmap.height / 2f

    val zx = map(
        value = offsetY * luminance,
        sourceMin = 0f,
        sourceMax = imageBitmap.height.toFloat(),
        destMin = imageBitmap.width.toFloat(),
        destMax = imageBitmap.width.toFloat() / 2f
    )

    val zy = map(
        value = offsetY * luminance,
        sourceMin = 0f,
        sourceMax = imageBitmap.height.toFloat(),
        destMin = imageBitmap.height.toFloat(),
        destMax = imageBitmap.height.toFloat() / 2f
    )

    val sx = map(trueX / zx, 0f, 1f, 0f, imageBitmap.width.toFloat())
    val sy = map(trueY / zy, 0f, 1f, 0f, imageBitmap.height.toFloat())

    // HUE CIRCLES
    drawCircle(
        color = pixel,
        radius = (luminance * dotCount / 2) + 4f,
        center = Offset(sx, sy)
    )
//    drawRect(
//        topLeft = Offset(sx, sy),
//        size = Size(dotCount, dotCount),
//        color = pixel
//    )
}

@Composable
private fun createImageBitmap(): ImageBitmap {
    return ImageBitmap.imageResource(id = R.drawable.image3)
}
