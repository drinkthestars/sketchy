package com.goofy.goober.sketch.static

import androidx.navigation.NavGraphBuilder
import com.goofy.goober.sketch.ui.DestinationScreen
import com.goofy.goober.sketch.ui.HomeScreens
import com.goofy.goober.sketch.ui.Screen
import com.goofy.goober.sketch.ui.nestedContent

private val Screens = listOf(
    DestinationScreen(
        title = "Noise Grain 1",
        description = "Subtle noise grain"
    ) { NoiseGrain1Texture() },
    DestinationScreen(
        title = "Noise Grain 2",
        description = "Noisier grain"
    ) { NoiseGrain2Texture() },
    DestinationScreen(title = "Risograph", description = "Risograph print") { RisographTexture() },
    DestinationScreen(
        title = "Paper Texture",
        description = "Like noise grain, but more like paper"
    ) { PaperTexture() },
    DestinationScreen(
        title = "Sketching Paper Texture",
        description = "Texture of rough/sketchpad paper"
    ) { SketchingPaperTexture() },
    DestinationScreen(
        title = "Marbled Texture",
        description = "A weird watery/marbled texture"
    ) { MarbledTexture() },
)

fun NavGraphBuilder.textureShadersGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = Screens, home = HomeScreens.CanvasDrawing)
}
