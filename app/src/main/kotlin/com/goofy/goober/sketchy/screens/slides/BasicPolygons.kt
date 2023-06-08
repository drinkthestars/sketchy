package com.goofy.goober.sketchy.screens.slides

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.common.InteractiveContainer
import com.goofy.goober.sketchy.common.drawPolygon
import com.goofy.goober.style.Sizing.Six

private val Wedgewood = Color(0xff477aaa)

@Preview(showBackground = true)
@Composable
fun BasicPolygons() {
    var vertices by remember { mutableIntStateOf(3) }
    val path = remember { Path() }

    InteractiveContainer(
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 26.dp, bottom = 10.dp),
        onShapeChangeClick = {
            vertices = if (vertices == 10) {
                3
            } else {
                (vertices + 1).coerceAtMost(10)
            }
        }
    ) { onGloballyPositioned ->
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Color.White)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Six)
        ) {
            val (width, height) = size
            drawPolygon(
                color = Wedgewood,
                vertices = vertices,
                radius = size.width * 0.3f,
                center = this.center,
                path = path,
            )
        }
    }
}
