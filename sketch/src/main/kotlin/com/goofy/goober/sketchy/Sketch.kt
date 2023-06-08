package com.goofy.goober.sketchy

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Screenshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.CacheDrawScope
import androidx.compose.ui.draw.DrawResult
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.capture.captureAndShare

@Composable
fun produceDrawLoopCounter(speed: Float = 1f): State<Float> {
    return produceState(0f) {
        while (true) {
            withInfiniteAnimationFrameMillis {
                value = it / 1000f * speed
            }
        }
    }
}

@Composable
fun SketchWithCache(
    modifier: Modifier = Modifier,
    speed: Float = 1f,
    onDrawWithCache: CacheDrawScope.(time: Float) -> DrawResult
) {
    val time by produceDrawLoopCounter(speed)
    Box(
        modifier = modifier.drawWithCache {
            onDrawWithCache(time)
        }
    )
}

@Composable
private fun Controls(
    fps: State<Long>,
    size: IntSize,
    boundsInWindow: Rect
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .wrapContentSize()
            .navigationBarsPadding(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
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
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "fps = ${fps.value}",
            color = Color.Black,
        )
    }
}
