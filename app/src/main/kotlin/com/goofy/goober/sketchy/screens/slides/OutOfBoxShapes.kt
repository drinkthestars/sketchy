package com.goofy.goober.sketchy.screens.slides

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import com.goofy.goober.sketchy.common.Bg1
import com.goofy.goober.sketchy.common.InteractiveContainer
import com.goofy.goober.style.Sizing

@Preview(showBackground = true)
@Composable
fun OutOfBoxShapes() {
    InteractiveContainer(
        Modifier
            .fillMaxSize()
            .padding(horizontal = Sizing.Six)
    ) { onGloballyPositioned ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg1)
                .onGloballyPositioned(onGloballyPositioned) // for screenshotting
                .padding(Sizing.Six),
        ) {
            val (width, height) = size
            drawCircle(
                color = Color.DarkGray,
                radius = 200f, center = Offset(width / 2f, height / 4f)
            )

            drawLine(
                color = Color.DarkGray,
                start = Offset(width / 4f, height / 2f), end = Offset(width * 3f / 4f, height / 2f),
                strokeWidth = 50f
            )

            drawRect(
                color = Color.DarkGray,
                topLeft = Offset(width / 4f, (height * 3f / 4f) - height / 8f),
                size = Size(width / 2f, height / 4f)
            )
        }
    }
}
