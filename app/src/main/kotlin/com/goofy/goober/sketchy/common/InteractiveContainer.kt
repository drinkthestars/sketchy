package com.goofy.goober.sketchy.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material.icons.outlined.SwitchAccessShortcut
import androidx.compose.material.icons.outlined.Texture
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import com.goofy.goober.sketchy.capture.captureAndShare
import com.goofy.goober.style.Slider
import com.goofy.goober.style.Sizing
import kotlinx.coroutines.launch

@Composable
fun InteractiveContainer(
    modifier: Modifier = Modifier,
    onRefreshClick: (() -> Unit)? = null,
    onArrowUpClick: (() -> Unit)? = null,
    onArrowDownClick: (() -> Unit)? = null,
    onAddClick: (() -> Unit)? = null,
    onRemoveClick: (() -> Unit)? = null,
    onShapeChangeClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.(onGloballyPositioned: (LayoutCoordinates) -> Unit) -> Unit
) {
    val context = LocalContext.current
    var size by remember { mutableStateOf(IntSize.Zero) }
    var boundsInWindow by remember { mutableStateOf(Rect.Zero) }
    val scope = rememberCoroutineScope()

    Column(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.wrapContentSize(),
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
                onRefreshClick?.run {
                    IconButton(onClick = { this() }) {
                        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = null)
                    }
                }
                onArrowUpClick?.run {
                    IconButton(onClick = { this() }) {
                        Icon(imageVector = Icons.Outlined.ArrowUpward, contentDescription = null)
                    }
                }
                onArrowDownClick?.run {
                    IconButton(onClick = { this() }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowDownward,
                            contentDescription = null
                        )
                    }
                }
                onAddClick?.run {
                    IconButton(onClick = { this() }) {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
                    }
                }
                onRemoveClick?.run {
                    IconButton(onClick = { this() }) {
                        Icon(imageVector = Icons.Outlined.Remove, contentDescription = null)
                    }
                }
                onShapeChangeClick?.apply {
                    IconButton(onClick = { this() }) {
                        Icon(
                            imageVector = Icons.Outlined.SwitchAccessShortcut,
                            contentDescription = null
                        )
                    }
                }
            }
        }
        content {
            size = it.size
            boundsInWindow = it.boundsInWindow()
        }
    }
}

@Composable
fun TextureControls(
    modifier: Modifier = Modifier,
    onTexturingToggled: (Boolean) -> Unit,
    onSaturationToggled: ((Boolean) -> Unit)? = null,
    onIntensityChanged: (Float) -> Unit
) {
    var intensity by remember { mutableFloatStateOf(0.15f) }
    var texturingEnabled by remember { mutableStateOf(false) }
    var saturationEnabled by remember { mutableStateOf(false) }
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            texturingEnabled = !texturingEnabled
            onTexturingToggled(texturingEnabled)
        }) {
            Icon(
                imageVector = Icons.Outlined.Texture,
                contentDescription = null
            )
        }
        onSaturationToggled?.run {
            IconButton(onClick = {
                saturationEnabled = !saturationEnabled
                this(saturationEnabled)
            }) {
                Icon(
                    imageVector = Icons.Outlined.WbSunny,
                    contentDescription = null
                )
            }
        }
        Slider(
            enabled = texturingEnabled,
            label = if (texturingEnabled) "Intensity = $intensity" else "Texturing Disabled",
            value = intensity,
            onValueChange = {
                intensity = it
                onIntensityChanged(it)
            },
            valueRange = 0f..2f
        )
    }
}

@Composable
fun ColorPaletteViewer(colors: List<Color>, modifier: Modifier = Modifier) {
    Row(modifier = modifier.wrapContentSize()) {
        Text(text = "Colors: ", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.width(Sizing.Four))
        colors.forEach {
            key(it) {
                Box(
                    modifier = Modifier
                        .size(Sizing.Six)
                        .background(it)
                )
                Spacer(Modifier.width(Sizing.Four))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TextureControlsPreview() {
    TextureControls(onTexturingToggled = {}, onIntensityChanged = {})
}

@Preview(showBackground = true)
@Composable
fun ColorPaletteViewerPreview() {
    ColorPaletteViewer(
        colors = listOf(Color.DarkGray, Color.Blue, Color.Green)
    )
}
