package com.goofy.goober.sketch.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketch.R
import com.goofy.goober.sketch.capture.captureAndShare
import com.goofy.goober.style.IntSlider
import kotlin.random.Random

private const val XSamples = 90f

@Composable
fun GlitchyImageRemixing() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        val imageBitmap = createImageBitmap()
        var size by remember { mutableStateOf(IntSize.Zero) }
        var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
        val context = LocalContext.current

        var glitchCount = remember { mutableStateOf(40) }

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
                label = "Glitch Count",
                value = glitchCount.value,
                onValueChange = { glitchCount.value = it.toInt() },
                valueRange = 8..140
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
                glitchCount = glitchCount
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
    glitchCount: State<Int>
) {
    var offsetY by remember { mutableFloatStateOf(0f) }

    val pixelMap = imageBitmap.toPixelMap(
        width = imageBitmap.width, height = imageBitmap.height
    )
    val canvasSize = with(LocalDensity.current) {
        DpSize(imageBitmap.width.toDp(), imageBitmap.height.toDp())
    }

    val randoms by remember { derivedStateOf { List(glitchCount.value) { Random.nextFloat() } } }

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
        // draw imagebitmap
        drawImage(imageBitmap)
        // create a for loop of 30 iterations
        for (i in 0 until glitchCount.value) {
//            val random = randoms[i]
            // create a random x value
            val randomStartX = Random.nextFloat() * imageBitmap.width
            // create a random y value
            val randomStartY = Random.nextFloat() * imageBitmap.height
            // random end x and y

            // create a random dot count
            val randomSize = Random.nextFloat() * imageBitmap.width / 3f
            // get pixel color at the random x and y values and invert it
            val pixel = pixelMap[randomStartX.toInt(), randomStartY.toInt()]


            val glitchDirectionX = if (Random.nextFloat() < 0.5f) -1f else 1f

            drawLine(
                start = Offset(randomStartX, randomStartY),
                end = Offset(
                    x = (randomStartX + (glitchDirectionX * randomSize)).coerceIn(
                        0f,
                        imageBitmap.width.toFloat()
                    ),
                    y = randomStartY
                ),
                color = pixel,
                strokeWidth = (Random.nextFloat()  * 15f) + 2f,
            )

//            if (Random.nextFloat() < 0.5f) {
//                drawLine(
//                    start = Offset(randomStartX, randomStartY),
//                    end = Offset(
//                        x = randomStartX,
//                        y = (randomStartY + (glitchDirectionY * randomDotCount)).coerceIn(
//                            0f,
//                            imageBitmap.height.toFloat()
//                        )
//                    ),
//                    color = pixel,
//                    strokeWidth = (Random.nextFloat()  * 20f) + 5f
//                )
//            } else {
//                drawLine(
//                    start = Offset(randomStartX, randomStartY),
//                    end = Offset(
//                        x = (randomStartX + (glitchDirectionX * randomDotCount)).coerceIn(
//                            0f,
//                            imageBitmap.width.toFloat()
//                        ),
//                        y = randomStartY
//                    ),
//                    color = pixel,
//                    strokeWidth = (Random.nextFloat()  * 35f) + 7f
//                )
//            }
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
        size = Size(dotCount, dotCount / 2f),
        color = pixel
    )
}

@Composable
private fun createImageBitmap(): ImageBitmap {
    return ImageBitmap.imageResource(id = R.drawable.image8)
}
