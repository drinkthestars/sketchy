package com.goofy.goober.sketchy

import androidx.navigation.NavGraphBuilder
import com.goofy.goober.sketchy.HomeScreens.CanvasDrawing
import com.goofy.goober.sketchy.HomeScreens.CanvasRedrawing
import com.goofy.goober.sketchy.HomeScreens.DrawingShapes
import com.goofy.goober.sketchy.HomeScreens.DrawingShapesInteractive
import com.goofy.goober.sketchy.HomeScreens.ImageRemixing
import com.goofy.goober.sketchy.HomeScreens.ImageSampling
import com.goofy.goober.sketchy.HomeScreens.Slides
import com.goofy.goober.sketchy.HomeScreens.Texturing
import com.goofy.goober.sketchy.scaffolding.DestinationScreen
import com.goofy.goober.sketchy.scaffolding.NestedNavScreen
import com.goofy.goober.sketchy.scaffolding.Screen
import com.goofy.goober.sketchy.scaffolding.nestedContent
import com.goofy.goober.sketchy.screens.BasicDrawing
import com.goofy.goober.sketchy.screens.CanvasRedrawing
import com.goofy.goober.sketchy.screens.Circles
import com.goofy.goober.sketchy.screens.Parallelograms
import com.goofy.goober.sketchy.screens.Polygons
import com.goofy.goober.sketchy.screens.PolygonsInteractive
import com.goofy.goober.sketchy.screens.PolygonsTouchInteractive
import com.goofy.goober.sketchy.screens.Quads
import com.goofy.goober.sketchy.screens.StatefulCanvasBlobs
import com.goofy.goober.sketchy.screens.TexturingHexagons
import com.goofy.goober.sketchy.screens.TexturingInteractivePolygons
import com.goofy.goober.sketchy.screens.Triangles
import com.goofy.goober.sketchy.screens.images.Downsampled
import com.goofy.goober.sketchy.screens.images.GlitchyImageRemixing
import com.goofy.goober.sketchy.screens.images.ImageRemixing
import com.goofy.goober.sketchy.screens.images.ImageRemixingInteractive
import com.goofy.goober.sketchy.screens.images.Rasterized
import com.goofy.goober.sketchy.screens.images.RasterizedZIndex
import com.goofy.goober.sketchy.screens.slides.BasicPolygons
import com.goofy.goober.sketchy.screens.slides.Generate
import com.goofy.goober.sketchy.screens.slides.GeneratePositions
import com.goofy.goober.sketchy.screens.slides.GenerateRandomly
import com.goofy.goober.sketchy.screens.slides.OutOfBoxShapes
import com.goofy.goober.sketchy.screens.slides.TransformWithTexture

object HomeScreens {
    const val Home = "Sketch"
    const val CanvasDrawing = "Basic Canvas Drawing"
    const val DrawingShapes = "Shapes"
    const val DrawingShapesInteractive = "Interactive Shapes"
    const val CanvasRedrawing = "Redrawing"
    const val ImageSampling = "Image Sampling"
    const val ImageRemixing = "Image Remixing"
    const val Texturing = "Texturing"
    const val Slides = "Creative Coding '23"
}

val TopLevelScreens = listOf(
    NestedNavScreen(
        title = Slides,
        description = "Specific Examples from the Creative Coding Compose '23 talk"
    ) { onNavigate -> slidesImagesGraph(onNavigate) },
    DestinationScreen(
        title = CanvasDrawing,
        description = "Showcasing the coordinate system"
    ) { BasicDrawing() },
    NestedNavScreen(
        title = DrawingShapes,
        description = "Drawing custom shapes"
    ) { onNavigate -> shapesGraph(onNavigate) },
    NestedNavScreen(
        title = DrawingShapesInteractive,
        description = "Drawing custom shapes and adding interactivity"
    ) { onNavigate -> shapesInteractiveGraph(onNavigate) },
    DestinationScreen(
        title = CanvasRedrawing,
        description = "Recording canvas drawing"
    ) { CanvasRedrawing() },
    NestedNavScreen(
        title = ImageSampling,
        description = "Sampling input images"
    ) { onNavigate -> imageSamplingGraph(onNavigate) },
    NestedNavScreen(
        title = ImageRemixing,
        description = "Remixing images"
    ) { onNavigate -> imageRemixingGraph(onNavigate) },
    NestedNavScreen(
        title = Texturing,
        description = "Adding textures with shaders"
    ) { onNavigate -> texturingGraph(onNavigate) },
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

//region InteractiveShapes
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
    DestinationScreen(title = "Texturing") {
        TexturingHexagons()
    },
    DestinationScreen(title = "Texturing Interactive") {
        TexturingInteractivePolygons()
    },
)

fun NavGraphBuilder.texturingGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = TexturingScreens, home = Texturing)
}
//endregion

//region Slides
private val SlidesImages = listOf(
    DestinationScreen(title = "Form - Basic Shapes") {
        OutOfBoxShapes()
    },
    DestinationScreen(title = "Form - Basic Polygons") {
        BasicPolygons()
    },
    DestinationScreen(title = "Generate") {
        Generate()
    },
    DestinationScreen(title = "Generate + Position") {
        GeneratePositions()
    },
    DestinationScreen(title = "Generate + Randomize") {
        GenerateRandomly()
    },
    DestinationScreen(title = "Stateful Canvas") {
        StatefulCanvasBlobs()
    },
    DestinationScreen(title = "Transform - Texturing") {
        TransformWithTexture()
    },
)

fun NavGraphBuilder.slidesImagesGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = SlidesImages, home = Slides)
}
//endregion
