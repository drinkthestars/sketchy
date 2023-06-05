package com.goofy.goober.sketch.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketch.CanvasRecorder
import com.goofy.goober.sketch.CapturableContainer
import com.goofy.goober.sketch.capture.captureAndShare
import com.goofy.goober.sketch.nextFloat
import com.goofy.goober.sketch.ui.PaletteBright
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val Padding = 32.dp
private val Rad360 = 2f * PI.toFloat() / 360f

val MaxSize = 150f

@Preview(showBackground = true)
@Composable
fun CanvasRedrawing(modifier: Modifier = Modifier) {
    ContainerWithControls(
        modifier.padding(16.dp),
        onRefreshClick = { }
    ) { count, onGloballyPositioned ->
        CanvasRecorder(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .background(Color(0xffF8F8FF))
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Padding),
            onDraw = {
                val (width, height) = size
                translate((width / 2f), (height / 2f)) {

                    List(count) {
                        // draw lines of random length and angle startig tom bottom left
                        val random =
                            Random.nextFloat(min = -(width - MaxSize), max = (width - MaxSize))

                        val centerX =
                            Random.nextFloat(min = -(width - MaxSize), max = (width - MaxSize))
                        val centerY =
                            Random.nextFloat(min = -(height - MaxSize), max = height - MaxSize)
                        val angle = Random.nextFloat(min = 0f, max = 360f)
                        val length = Random.nextFloat(min = 0f, max = MaxSize)
                        val x = centerX + cos(angle * Rad360) * length
                        val y = centerY + sin(angle * Rad360) * length


                        val color =
                            PaletteBright[Random.nextFloat(min = 0f, max = PaletteBright.size.toFloat() - 1f)
                                .toInt()]
                        val stroke = if (
                            color == Color(0xffD8BFD8)
                            || color == Color(0xffFFF0F5)
                            || color == Color(0xffffc0cb)
                        ) {
                            Random.nextFloat(min = 1f, max = 3f)
                        } else {
                            Random.nextFloat(min = 0.1f, max = 0.3f)
                        }

                        drawLine(
                            start = Offset(x, y),
                            end = Offset(centerX, centerY),
                            color = color,
                            strokeWidth = stroke,
                            blendMode = BlendMode.Multiply
                        )

//                        if (it % 756 == 0) {
//                            drawCircle(
//                                color = Dots[glm.linearRand(0f, Dots.size.toFloat() - 1f).toInt()],
//                                radius = glm.gaussRand(25f, 4f),
//                                center = Offset(x, y)
//                            )
//                        }
                    }
                }
            }
        )
    }
}

@Composable
internal fun ContainerWithControls(
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 3f..50f,
    initialValue: Float = 20f,
    onRefreshClick: (Int) -> Unit = {},
    content: @Composable (dotCount: Int, onGloballyPositioned: (LayoutCoordinates) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var dotCount by remember { mutableStateOf(initialValue) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
//                .align(Alignment.TopCenter)
                .padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp)
        ) {
            Slider(modifier = Modifier.fillMaxWidth(),
                value = dotCount,
                valueRange = range,
                onValueChange = { dotCount = it }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(onClick = {
                    scope.launch {
                        captureAndShare(
                            width = size.width,
                            height = size.height,
                            rect = boundsInWindow,
                            context = context
                        )
                    }
                }) {
                    Icon(imageVector = Icons.Outlined.Screenshot, contentDescription = null)
                }
                IconButton(onClick = { onRefreshClick(dotCount.toInt()) }) {
                    Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                }
                Spacer(Modifier.width(10.dp))
                Text(text = "Dot count: $dotCount")
            }
        }
        content(dotCount.toInt()) {
            size = it.size
            boundsInWindow = it.boundsInWindow()
        }
    }
}
