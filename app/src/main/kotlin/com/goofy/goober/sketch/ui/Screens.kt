package com.goofy.goober.sketch.ui

import androidx.navigation.NavGraphBuilder
import com.goofy.goober.sketch.screens.BasicDrawing
import com.goofy.goober.sketch.screens.CanvasRedrawing
import com.goofy.goober.sketch.screens.Circles
import com.goofy.goober.sketch.screens.Downsampled
import com.goofy.goober.sketch.screens.ImageRemixing
import com.goofy.goober.sketch.screens.ImageRemixingInteractive
import com.goofy.goober.sketch.screens.GlitchyImageRemixing
import com.goofy.goober.sketch.screens.Parallelograms
import com.goofy.goober.sketch.screens.Polygons
import com.goofy.goober.sketch.screens.PolygonsInteractive
import com.goofy.goober.sketch.screens.PolygonsTouchInteractive
import com.goofy.goober.sketch.screens.Quads
import com.goofy.goober.sketch.screens.Rasterized
import com.goofy.goober.sketch.screens.RasterizedZIndex
import com.goofy.goober.sketch.screens.SolWall
import com.goofy.goober.sketch.screens.TexturingHexagons
import com.goofy.goober.sketch.screens.TexturingInteractivePolygons
import com.goofy.goober.sketch.screens.Triangles
import com.goofy.goober.sketch.ui.HomeScreens.CanvasDrawing
import com.goofy.goober.sketch.ui.HomeScreens.CanvasRedrawing
import com.goofy.goober.sketch.ui.HomeScreens.DrawingShapes
import com.goofy.goober.sketch.ui.HomeScreens.DrawingShapesInteractive
import com.goofy.goober.sketch.ui.HomeScreens.ImageRemixing
import com.goofy.goober.sketch.ui.HomeScreens.ImageSampling
import com.goofy.goober.sketch.ui.HomeScreens.NoisyShaders
import com.goofy.goober.sketch.ui.HomeScreens.SolWall

object HomeScreens {
    const val Home = "Sketch"
    const val CanvasDrawing = "Canvas Drawing"
    const val DrawingShapes = "Drawing Shapes"
    const val DrawingShapesInteractive = "Drawing Shapes - Interactive"
    const val CanvasRedrawing = "Canvas Re-drawing"
    const val ImageSampling = "Image Sampling"
    const val ImageRemixing = "Image Remixing"
    const val ImageRemixingInteractive = "Image Remixing - Interactive"
    const val NoisyShaders = "Noisy Shaders"
    const val SolWall = "Sol Wall Drawing"
}

val TopLevelScreens = listOf(
    DestinationScreen(
        title = CanvasDrawing,
        description = "Showcasing the coordinate system"
    ) { BasicDrawing() },
    NestedNavScreen(DrawingShapes) { onNavigate -> shapesGraph(onNavigate) },
    NestedNavScreen(DrawingShapesInteractive) { onNavigate -> shapesInteractiveGraph(onNavigate) },
    DestinationScreen(
        title = CanvasRedrawing,
        description = "Recording and re-drawing the canvas"
    ) { CanvasRedrawing() },
    NestedNavScreen(ImageSampling) { onNavigate -> imageSamplingGraph(onNavigate) },
    NestedNavScreen(ImageRemixing) { onNavigate -> imageRemixingGraph(onNavigate) },
    NestedNavScreen(NoisyShaders) { onNavigate -> texturingGraph(onNavigate) },
    DestinationScreen(
        title = SolWall,
        description = "Sol Wall Drawing"
    ) { SolWall() },
)

//region Shapes
private val ShapesScreens = listOf(
    DestinationScreen(title = "Polygons") {
        Polygons()
    },
    DestinationScreen(title = "Circles") {
        Circles()
    },
    DestinationScreen(title = "Triangles") {
        Triangles()
    },
    DestinationScreen(title = "Parallelograms") {
        Parallelograms()
    },
    DestinationScreen(title = "Quads") {
        Quads()
    },
)

fun NavGraphBuilder.shapesGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = ShapesScreens, home = DrawingShapes)
}
//endregion

//region Shapes
private val ShapesInteractiveScreens = listOf(
    DestinationScreen(title = "Polygons - Interactive") {
        PolygonsInteractive()
    },
    DestinationScreen(title = "Polygons - Touch Interactive") {
        PolygonsTouchInteractive()
    },
)

fun NavGraphBuilder.shapesInteractiveGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = ShapesInteractiveScreens, home = DrawingShapesInteractive)
}
//endregion

//region Images
private val ImageSamplingScreens = listOf(
    DestinationScreen(title = "Downsampled") {
        Downsampled()
    },
    DestinationScreen(title = "Rasterized") {
        Rasterized()
    },
    DestinationScreen(title = "Rasterized Z Index") {
        RasterizedZIndex()
    },
)

fun NavGraphBuilder.imageSamplingGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = ImageSamplingScreens, home = ImageSampling)
}
//endregion

//region ImageRemixing
private val ImageRemixingScreens = listOf(
    DestinationScreen(title = "Triangles - Remixed") {
        ImageRemixing()
    },
    DestinationScreen(title = "Image Remixing - Interactive") {
        ImageRemixingInteractive()
    },
    DestinationScreen(title = "Image Remixing - Glitchy") {
        GlitchyImageRemixing()
    },
)

fun NavGraphBuilder.imageRemixingGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = ImageRemixingScreens, home = ImageRemixing)
}
//endregion

//region Texturing
private val TexturingScreens = listOf(
    DestinationScreen(title = "Texturing Polygons") {
        TexturingHexagons()
    },
    DestinationScreen(title = "Texturing Interactive Polygons") {
        TexturingInteractivePolygons()
    },
)

fun NavGraphBuilder.texturingGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = TexturingScreens, home = NoisyShaders)
}
//endregion
