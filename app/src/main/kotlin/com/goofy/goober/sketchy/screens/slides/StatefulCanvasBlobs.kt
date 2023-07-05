package com.goofy.goober.sketchy.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.goofy.goober.sketchy.StatefulCanvas
import com.goofy.goober.sketchy.common.Bg1
import com.goofy.goober.sketchy.common.InteractiveContainer
import com.goofy.goober.sketchy.common.Palette3
import com.goofy.goober.sketchy.common.RandomBlobs
import com.goofy.goober.sketchy.common.splatter
import com.goofy.goober.sketchy.nextFloat
import com.goofy.goober.style.IntSlider
import com.goofy.goober.style.Sizing
import kotlin.random.Random

private val MaxSize = 180f
private val MinCount = 10

@Preview(showBackground = true)
@Composable
fun StatefulCanvasBlobs(modifier: Modifier = Modifier) {
    var currentCount by remember { mutableIntStateOf(MinCount) }
    var currentColor by remember { mutableStateOf(Palette3.first()) }
    var randomBlobs by remember { mutableStateOf(RandomBlobs()) }

    InteractiveContainer(
        modifier.padding(16.dp),
        onRefreshClick = {
            currentColor = Palette3.shuffled().take(1).first()
        },
        onAddClick = {
            randomBlobs = RandomBlobs(
                color = currentColor,
                count = currentCount
            )
        }
    ) { onGloballyPositioned ->
        Row(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()) {
            Box(
                modifier
                    .size(24.dp)
                    .background(currentColor)
            )
            Spacer(Modifier.width(10.dp))
            IntSlider(
                label = "Blob count: $currentCount",
                value = currentCount,
                valueRange = MinCount..300,
                onValueChange = { currentCount = it }
            )
        }
        Spacer(Modifier.height(10.dp))
        StatefulCanvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(0.67f)
                .background(Bg1)
                .onGloballyPositioned(onGloballyPositioned)
                .padding(Sizing.Eight),
            onDraw = {
                val (width, height) = size
                with(randomBlobs) {
                    repeat(count) {
                        val centerX = Random.nextFloat(min = 0f, max = width)
                        val centerY = Random.nextFloat(min = 0f, max = height)

                            splatter(
                                center = Offset(centerX, centerY),
                                color = color,
                                maxRadius = Random.nextFloat(min = 0f, max = MaxSize),
                                droplets = Random.nextInt(from = 23, until = 50)
                            )

                        // Uncomment this for regular circles
//                        drawCircle(
//                            center = Offset(centerX, centerY),
//                            color = color,
//                            radius = Random.nextFloat(min = 0f, max = 100f),
////                            path= path,
////                            vertices = 6,
//                            blendMode = BlendMode.Xor
//                        )
                    }
                }
            }
        )
    }
}
