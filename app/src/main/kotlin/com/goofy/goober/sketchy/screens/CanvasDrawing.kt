package com.goofy.goober.sketchy.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.common.InteractiveContainer
import com.goofy.goober.style.Sizing
import com.goofy.goober.style.md_theme_light_onSecondaryContainer
import com.goofy.goober.style.md_theme_light_tertiaryContainer

private const val LabelSize = 50f
private val paint = Paint().apply {
    isAntiAlias = true
    style = PaintingStyle.Fill
    color = md_theme_light_onSecondaryContainer
}.asFrameworkPaint().apply {
    textSize = LabelSize
}

@Preview(showBackground = true)
@Composable
fun BasicDrawing(modifier: Modifier = Modifier) {
    InteractiveContainer(modifier.fillMaxSize()) { onGloballyPositioned ->
        SimpleCircleWithBox(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.CenterHorizontally)
                .onGloballyPositioned(onGloballyPositioned)
        )
    }
}

@Composable
private fun SimpleCircleWithBox(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(Sizing.Six)
            .border(1.dp, Color.DarkGray)
            .drawBehind { // this: Dra
                val points = listOf(
                    Offset(0f, 0f),
                    Offset(this.size.width, 0f),
                    Offset(0f, this.size.height),
                    Offset(this.size.width, this.size.height),
                    Offset(this.size.width / 2f, this.size.height / 2f)
                )
                val labels = listOf(
                    Offset(30f, 80f),
                    Offset(this.size.width - 330f, 80f),
                    Offset(40f, this.size.height - 40f),
                    Offset(this.size.width - 380f, this.size.height - 40f),
                    Offset((this.size.width / 2f) - 150f, (this.size.height / 2f) + 70f)
                )

                // Draws circle at this.center
                drawCircle(
                    color = md_theme_light_tertiaryContainer,
                    radius = 200f
                )
                // Draws points at their Offsets
                drawPoints(
                    points = points,
                    pointMode = PointMode.Points,
                    color = Color.Magenta,
                    cap = StrokeCap.Round,
                    strokeWidth = 30f
                )

                points
                    .zip(labels)
                    .forEachIndexed { index, (pointOffset, labelOffset) ->
                        drawContext.canvas.nativeCanvas.drawText(
                            "(${pointOffset.x}, ${pointOffset.y})",
                            labelOffset.x,
                            labelOffset.y,
                            paint
                        )
                    }
            }
    )
}
