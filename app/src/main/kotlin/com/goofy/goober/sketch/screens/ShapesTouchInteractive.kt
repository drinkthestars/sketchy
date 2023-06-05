package com.goofy.goober.sketch.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketch.CanvasRecorder
import com.goofy.goober.sketch.PI
import com.goofy.goober.sketch.capture.captureAndShare
import com.goofy.goober.sketch.distanceTo
import com.goofy.goober.sketch.ui.Bg1
import com.goofy.goober.sketch.ui.Palette3
import com.goofy.goober.sketch.ui.PaletteBlues
import kotlinx.coroutines.launch

private val Padding = 32.dp

@Preview(showBackground = true)
@Composable
fun PolygonsTouchInteractive(modifier: Modifier = Modifier) {
    val path = remember { Path() }
    var colors by remember { mutableStateOf(PaletteBlues.shuffled().take(2)) }
    var center by remember { mutableStateOf<Offset?>(null) }

    val centerDiff = remember { 25f }

    Container(
        modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp),
        onAddClick = {
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
                .onGloballyPositioned {
                    onGloballyPositioned(it)
                }
                .padding(Padding)
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
                    val mirrorX = width - this.x
                    val mirrorY = height - this.y

                    println("WARP mirrorX: $mirrorX, mirrorY: $mirrorY")

                    val mirrorCenter = Offset(mirrorX, mirrorY)
                    val mirrorRadius = (height - mirrorY) / height * width * 0.5f
                    val mirrorAlpha = ((height - mirrorY) / height).coerceIn(0f, 0.8f)
                    val mirrorRotation = -rotation

                    drawPolygon(
                        color = colors[0],
                        sides = 3,
                        radius = radius,
                        center = this,
                        rotation = rotation,
                        path = path,
                        alpha = alpha,
                    )

                    drawPolygon(
                        color = colors[1],
                        sides = 3,
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
//                    sides = nbPoints,
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
//                    sides = nbPoints,
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
//                    sides = nbPoints,
//                    radius = width * 0.3f,
//                    center = this.center,
//                    rotation = PI,
//                    path = path,
//                )
//                // Hexes
//                path.reset()
//                drawPolygon(
//                    color = colorQuad[3],
//                    sides = nbPoints,
//                    radius = width * 0.2f,
//                    center = this.center,
//                    rotation = PI,
//                    path = path,
//                )
//                path.reset()
//                drawPolygon(
//                    color = colorQuad[2],
//                    sides = 3,
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
//                    sides =  nbPoints / 2,
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
//                    sides =  nbPoints / 2,
//                    radius = width * 0.15f,
//                    center = this.center,
//                    rotation = TWO_PI,
//                    path = path,
//                )

//                // SIX
//                path.reset()
//                drawQuad(
//                    color = colorQuad[5],
//                    sides =  nbPoints,
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
//                .align(Alignment.TopCenter)
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
