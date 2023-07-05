package com.goofy.goober.sketchy.screens.images

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import com.goofy.goober.sketchy.R
import com.goofy.goober.sketchy.common.InteractiveContainer
import com.goofy.goober.sketchy.common.createImageBitmap

private const val XSamples = 90f

@Composable
fun ImageRemixing() {
    InteractiveContainer(
        modifier = Modifier.fillMaxSize(),
    ) {onGloballyPositioned ->
        val imageBitmap = createImageBitmap(R.drawable.image3)

        Image(imageBitmap, modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .onGloballyPositioned(onGloballyPositioned)
        )
    }
}

/**
 * Sample showing how to obtain a [PixelMap] to query pixel information
 * from an underlying [ImageBitmap]
 */
@Composable
private fun Image(imageBitmap: ImageBitmap, modifier: Modifier = Modifier) {
    var offsetY by remember { mutableStateOf(XSamples) }

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
        for (x in 0 until imageBitmap.width step dotCount.toInt()) {
            for (y in 0 until imageBitmap.height step dotCount.toInt()) {
                val pixel = pixelMap[x, y]
                drawRectTrueColor(x, y, dotCount, pixel)
            }
        }
    }
}

private fun DrawScope.drawRectTrueColor(
    x: Int,
    y: Int,
    dotCount: Float,
    pixel: Color
) {
    drawRect(
        topLeft = Offset(x.toFloat(), y.toFloat()),
        size = Size(dotCount, dotCount),
        color = pixel
    )
}
