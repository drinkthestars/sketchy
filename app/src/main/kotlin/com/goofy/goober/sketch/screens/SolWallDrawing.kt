package com.goofy.goober.sketch.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.FiberSmartRecord
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Rotate90DegreesCw
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketch.CanvasRecorder
import com.goofy.goober.sketch.capture.captureAndShare
import com.goofy.goober.sketch.drawing.drawTriangle
import com.goofy.goober.sketch.ui.Bg1
import com.goofy.goober.sketch.ui.Palette3
import com.goofy.goober.sketch.ui.PaletteBright2
import kotlinx.coroutines.launch
import kotlin.random.Random

private val Padding = 32.dp

@Preview(showBackground = true)
@Composable
fun SolWall(modifier: Modifier = Modifier) {
    val path = remember { Path() }
    var triangle by remember {
        mutableStateOf(
            Triangle(
                v1Offset = 0f,
                v2Offset = 0f,
                v3Offset = 0f,
                alpha = 0f,
                color = Color.Transparent,
            )
        )
    }
    var colors by remember { mutableStateOf(PaletteBright2.shuffled().take(2)) }

    Container(
        modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp),
        onAddClick = {
            val v2Offset = Random.nextFloat()
            val v3Offset = Random.nextFloat()

            triangle = Triangle(
                0f,
                v2Offset,
                v3Offset,
                alpha = 0.23f,
                color = colors[Random.nextInt(colors.size)],
            )
        },
        onRefreshClick = {
            colors = Palette3.shuffled().take(2)
        },
        onShapeRefreshClick = {
        },
        onRotateClick = {
        },
    ) { onGloballyPositioned ->
        Row(modifier = Modifier.wrapContentSize()) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(colors[0])
            )
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(colors[1])
            )
        }
        CanvasRecorder(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg1)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Padding),
            onDraw = {
                val (width, height) = size

                with(triangle) {
                    path.reset()

                    val vertex1 = Offset(width, 0f)
                    val vertex2 = Offset(
                        0f,
                        v2Offset * height
                    )
                    val vertex3 = Offset(
                        0f,
                        v3Offset * height
                    )

                    drawTriangle(
                        vertex1 = vertex1,
                        vertex2 = vertex2,
                        vertex3 = vertex3,
                        path = path,
                        color = color,
                        alpha = alpha,
                    )
                }

            }
        )
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onShapeRefreshClick: () -> Unit = {},
    onRotateClick: () -> Unit = {},
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
                IconButton(onClick = { onAddClick() }) {
                    Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                }
                IconButton(onClick = { onRefreshClick() }) {
                    Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                }
                IconButton(onClick = { onShapeRefreshClick() }) {
                    Icon(imageVector = Icons.Outlined.FiberSmartRecord, contentDescription = null)
                }
                IconButton(onClick = { onRotateClick() }) {
                    Icon(imageVector = Icons.Outlined.Rotate90DegreesCw, contentDescription = null)
                }
            }
        }
        content {
            size = it.size
            boundsInWindow = it.boundsInWindow()
        }
    }
}
