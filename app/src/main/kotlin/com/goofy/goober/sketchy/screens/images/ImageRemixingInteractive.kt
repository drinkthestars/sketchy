package com.goofy.goober.sketchy.screens.images

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PixelMap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.R
import com.goofy.goober.sketchy.capture.captureAndShare
import com.goofy.goober.sketchy.common.Bg1
import com.goofy.goober.style.IntSlider
import com.goofy.goober.style.Sizing
import kotlin.random.Random

private const val XSamples = 3f

@Composable
fun ImageRemixingInteractive() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        val imageBitmap = createImageBitmap()
        var size by remember { mutableStateOf(IntSize.Zero) }
        var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
        val context = LocalContext.current

        var xSamples = remember { mutableStateOf(XSamples) }

        Row(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
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
            IntSlider(
                label = "X Sample Rate",
                value = xSamples.value.toInt(),
                onValueChange = { xSamples.value = it.toFloat() },
                valueRange = 4..30
            )
        }
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                imageBitmap = imageBitmap,
                modifier = Modifier
                    .onGloballyPositioned {
                        size = it.size
                        boundsInWindow = it.boundsInWindow()
                    },
                xSamplesState = xSamples
            )
        }
    }
}

/**
 * Sample showing how to obtain a [PixelMap] to query pixel information
 * from an underlying [ImageBitmap]
 */
@Composable
private fun Image(
    imageBitmap: ImageBitmap,
    modifier: Modifier = Modifier,
    xSamplesState: State<Float>
) {
    val canvasSize = with(LocalDensity.current) {
        DpSize(imageBitmap.width.toDp(), imageBitmap.height.toDp())
    }
    val xSamples = xSamplesState.value
    Canvas(
        modifier = modifier
            .size(canvasSize)
            .background(Bg1)
            .padding(Sizing.Six)
    ) {
        val aspectRatio = 0.67f
        val ySamples = xSamples / aspectRatio

        val padding = Sizing.Six.value
        val sampleWidth = ((size.width - ((xSamples - 1f) * padding)) / xSamples).toInt()
        val sampleHeight = ((size.height - ((ySamples - 1f) * padding)) / ySamples).toInt()
        val random = Random

        // Iterate over the samples in both x and y dimensions
        for (j in 0 until ySamples.toInt()) {
            for (i in 0 until xSamples.toInt()) {
                // Calculate the source and destination rectangles
                val srcOffset = IntOffset(
                    random.nextInt(0, imageBitmap.width.toInt() - sampleWidth),
                    random.nextInt(0, imageBitmap.height.toInt() - sampleHeight)
                )
                val srcSize = IntSize(sampleWidth, sampleHeight)

                val dstOffset = IntOffset(
                    i * (sampleWidth + padding.toInt()),
                    j * (sampleHeight + padding.toInt())
                )
                val dstSize = IntSize(sampleWidth, sampleHeight)

                // Draw the image
                drawImage(
                    imageBitmap,
                    srcOffset = srcOffset,
                    srcSize = srcSize,
                    dstOffset = dstOffset,
                    dstSize = dstSize
                )
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

private fun DrawScope.drawCircleTrueColor(
    pixel: Color,
    dotCount: Float,
    x: Float,
    y: Float
) {
    drawCircle(
        color = pixel,
        radius = dotCount / 2,
        center = Offset(x, y)
    )
}


@Composable
private fun createImageBitmap(): ImageBitmap {
    return ImageBitmap.imageResource(id = R.drawable.random7)
}
