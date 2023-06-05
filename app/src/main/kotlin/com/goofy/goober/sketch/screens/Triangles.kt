package com.goofy.goober.sketch.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.FiberSmartRecord
import androidx.compose.material.icons.outlined.LayersClear
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Rotate90DegreesCw
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material.icons.outlined.SwitchAccessShortcut
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketch.CanvasRecorder
import com.goofy.goober.sketch.CapturableContainer
import com.goofy.goober.sketch.PI
import com.goofy.goober.sketch.capture.captureAndShare
import com.goofy.goober.sketch.ui.Bg1
import com.goofy.goober.sketch.ui.Palette1
import kotlinx.coroutines.launch
import kotlin.math.pow

private val Padding = 16.dp

@Preview(showBackground = true)
@Composable
fun Triangles(modifier: Modifier = Modifier) {
    val path = remember { Path() }
    var nbPoints by remember { mutableIntStateOf(3) }
    val count = remember { mutableIntStateOf(10) }
    var colors by remember {
        mutableStateOf(Palette1.shuffled().take(2))
    }
    var yAbsOffsetFactor by remember {
        mutableFloatStateOf(0.1f)
    }

    Container(
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp),
        onRefreshClick = {
            colors = Palette1.shuffled().take(2)
        },
        onArrowUpClick = {
            yAbsOffsetFactor = (yAbsOffsetFactor + 0.05f).coerceAtMost(0.8f)
        },
        onArrowDownClick = {
            yAbsOffsetFactor = (yAbsOffsetFactor - 0.05f).coerceAtLeast(0.03f)
        },
        onAddClick = {
            count.value = (count.value + 1).coerceAtMost(50)
        },
        onRemoveClick = {
            count.value = (count.value - 1).coerceAtLeast(3)
        },
        onShapeChangeClick = {
            nbPoints = if (nbPoints == 10) {
                3
            } else {
                (nbPoints + 1).coerceAtMost(10)
            }
        }
    ) { onGloballyPositioned ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg1)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Padding),
            onDraw = {
                val (width, height) = size
                for (i in 0 until count.value) {
                    path.reset()
                    val ratio = i.toFloat() / count.value.toFloat()
                    val radius = width * 0.5f * (1f - ratio.pow(2))
//                    val yOffsetFactor = 0.1f + i.toFloat() / count.value.toFloat() * 0.7f
//                    val yOffset = height / 2f * yOffsetFactor
//                    val centerY = height / 2f + yOffset

                    val yOffsetFactor = if (i % 2 == 0) yAbsOffsetFactor else -yAbsOffsetFactor
//                    val yOffsetFactor = if (i < 3) 0.1f else -0.1f

                    val yOffset = height / 2 * yOffsetFactor
                    val centerY = height / 2 + yOffset

                    drawPolygon(
                        color = colors[i % colors.size],
                        sides = nbPoints,
                        radius = radius,
                        center = Offset(
                            width / 2f,
                            centerY
                        ),
                        rotation = if (i % 2 == 0) PI else 0f,
                        alpha = ratio,
                        path = path,
                    )
                }
            }
        )
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    onRefreshClick: () -> Unit = {},
    onArrowUpClick: () -> Unit = {},
    onArrowDownClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {},
    onShapeChangeClick: () -> Unit = {},
    content: @Composable ColumnScope.(onGloballyPositioned: (LayoutCoordinates) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var size by remember { mutableStateOf(IntSize.Zero) }
    var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp)
        ) {
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
                IconButton(onClick = { onRefreshClick() }) {
                    Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                }
                IconButton(onClick = { onArrowUpClick() }) {
                    Icon(imageVector = Icons.Outlined.ArrowUpward, contentDescription = null)
                }
                IconButton(onClick = { onArrowDownClick() }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDownward,
                        contentDescription = null
                    )
                }
                IconButton(onClick = {  onAddClick() }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                }
                IconButton(onClick = {  onRemoveClick() }) {
                    Icon(imageVector = Icons.Outlined.Remove, contentDescription = null)
                }
                IconButton(onClick = {  onShapeChangeClick() }) {
                    Icon(imageVector = Icons.Outlined.SwitchAccessShortcut, contentDescription = null)
                }
            }
        }
        content {
            size = it.size
            boundsInWindow = it.boundsInWindow()
        }
    }
}
