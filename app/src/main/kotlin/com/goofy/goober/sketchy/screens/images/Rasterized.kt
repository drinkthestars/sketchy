package com.goofy.goober.sketchy.screens.images

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.R
import com.goofy.goober.sketchy.capture.captureAndShare

private const val XSamples = 90f

@Composable
fun Rasterized() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        val imageBitmap = createImageBitmap()
        var size by remember { mutableStateOf(IntSize.Zero) }
        var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
        val context = LocalContext.current

        Spacer(modifier = Modifier.height(100.dp))
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = {
                captureAndShare(
                    width = size.width,
                    height = size.height,
                    rect = boundsInWindow,
                    context = context
                )
            }) {
                Icon(imageVector = Icons.Outlined.Screenshot, contentDescription = null)
            }
            Spacer(Modifier.width(10.dp))
        }
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
                drawCircleShadow(pixel, dotCount, x, y)
            }
        }
    }
}

private fun DrawScope.drawCircleShadow(
    pixel: Color,
    dotCount: Float,
    x: Int,
    y: Int
) {
    val luminance = pixel.luminance()

    val alpha = 1f - luminance
    drawCircle(
        color = pixel,
        radius = ((1f - luminance) * dotCount/2) + 1f,
        center = Offset(x.toFloat() + 3f, y.toFloat() + 3f),
        alpha = alpha
    )
}

@Composable
private fun createImageBitmap(): ImageBitmap {
    return ImageBitmap.imageResource(id = R.drawable.image5)
}
