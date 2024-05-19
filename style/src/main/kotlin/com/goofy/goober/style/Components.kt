package com.goofy.goober.style

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import androidx.compose.material3.Checkbox as Material3Checkbox

val LargeCardShape = RoundedCornerShape(12.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LargeCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    title: String,
    subtitle: String? = null
) {
    ElevatedCard(
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
            .border(1.dp, color = MaterialTheme.colorScheme.outlineVariant, shape = LargeCardShape),
        onClick = onClick,
        shape = LargeCardShape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .padding(start = 12.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    text = title
                )
                if (subtitle != null) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        modifier = Modifier.fillMaxWidth(0.6f),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        text = subtitle
                    )
                }
            }
            Icon(
                modifier = Modifier.padding(end = 12.dp),
                imageVector = Icons.Filled.ArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    title: String
) {
    ElevatedCard(
        modifier = modifier
            .wrapContentSize(align = Alignment.Center)
            .border(1.dp, color = MaterialTheme.colorScheme.outlineVariant, shape = LargeCardShape),
        elevation = elevatedCardElevation(4.dp),
        shape = LargeCardShape,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(Sizing.Five),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                text = title
            )
            Icon(
                modifier = Modifier.padding(end = Sizing.Five),
                imageVector = Icons.Filled.ArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun SketchyContainer(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
    controls: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.8f)
                .padding(Sizing.Five)
                .clip(LargeCardShape)
                .background(color = MaterialTheme.colorScheme.surfaceVariant),
            content = content
        )
        Divider(
            Modifier
                .fillMaxWidth()
                .padding(vertical = Sizing.Five)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.2f)
                .padding(horizontal = Sizing.Five),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Sizing.Five)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                content = controls
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MicPermContainer(
    modifier: Modifier = Modifier,
    grantedContent: @Composable () -> Unit
) {

    val micPermStatus = rememberPermissionState(
        Manifest.permission.RECORD_AUDIO
    )

    if (micPermStatus.status.isGranted) {
        grantedContent()
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(Sizing.Six),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LaunchedEffect(micPermStatus.status.shouldShowRationale) {
                if (!micPermStatus.status.shouldShowRationale) {
                    micPermStatus.launchPermissionRequest()
                }
            }
            if (micPermStatus.status.shouldShowRationale) {
                val context = LocalContext.current
                Text(
                    text =
                    "The audio examples require permission to access audio playback and recording",
                    modifier = Modifier.padding(Sizing.Five),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.size(Sizing.Five))
                Button(onClick = { context.openAppSystemSettings() }) {
                    Text("Enable in Settings")
                }
            }
        }
    }
}

private fun Context.openAppSystemSettings() {
    startActivity(
        Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }
    )
}

@Composable
fun Slider(
    modifier: Modifier = Modifier,
    label: String = "Slider",
    value: Float = 0.5f,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    steps: Int = 0,
    enabled: Boolean = true,
    onValueChange: (Float) -> Unit = {},
) {
    Column(
        modifier
            .wrapContentSize()
            .padding(Sizing.Three),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = Sizing.Four),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            text = label
        )
        Spacer(Modifier.height(Sizing.One))
        Slider(
            enabled = enabled,
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps
        )
    }
}

@Composable
fun IntSlider(
    modifier: Modifier = Modifier,
    label: String = "Slider",
    value: Int = 0,
    valueRange: ClosedRange<Int> = 0..5,
    steps: Int = valueRange.endInclusive - valueRange.start,
    onValueChange: (Int) -> Unit = {},
) {
    Column(
        modifier
            .wrapContentSize()
            .padding(Sizing.Three),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            modifier = Modifier.padding(horizontal = Sizing.Four),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            text = label
        )
        Spacer(Modifier.height(4.dp))
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = valueRange.start.toFloat().rangeTo(valueRange.endInclusive.toFloat()),
            steps = steps
        )
    }
}

@Composable
fun MenuItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onExpansionClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .outline()
                .clickable { onExpansionClick() }
                .padding(4.dp)
        )
    }
}

@Composable
fun Checkbox(
    modifier: Modifier = Modifier,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Material3Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
        )
        Spacer(modifier = Modifier.width(Sizing.Four))
    }
}


fun Modifier.outline(): Modifier = composed {
    this.border(
        width = 1.dp,
        color = MaterialTheme.colorScheme.outline,
        shape = RoundedCornerShape(4.dp)
    )
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_MASK)
fun SketchyContainerPreview() {
    SketchyTheme {
        SketchyContainer(
            content = {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(color = MaterialTheme.colorScheme.surfaceVariant)
                )
            },
            controls = {
                Slider()
                Spacer(modifier = Modifier.height(24.dp))
                Slider()
            }
        )
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_MASK)
fun SmallCardColumnPreview() {
    SketchyTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.size(0.dp, 12.dp))
            SmallCard(title = "Title 1")
            Spacer(Modifier.size(0.dp, 12.dp))
            SmallCard(title = "Title 2")
            Spacer(Modifier.size(0.dp, 12.dp))
            SmallCard(title = "Title 3")
            Spacer(Modifier.size(0.dp, 12.dp))
        }
    }
}

@Composable
@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_MASK)
fun LargeCardColumnPreview() {
    SketchyTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.size(0.dp, 12.dp))
            LargeCard(Modifier.size(360.dp, 100.dp), title = "Title 1")
            Spacer(Modifier.size(0.dp, 12.dp))
            LargeCard(Modifier.size(360.dp, 100.dp), title = "Title 2")
            Spacer(Modifier.size(0.dp, 12.dp))
            LargeCard(Modifier.size(360.dp, 100.dp), title = "Title 3")
            Spacer(Modifier.size(0.dp, 12.dp))
        }
    }
}

