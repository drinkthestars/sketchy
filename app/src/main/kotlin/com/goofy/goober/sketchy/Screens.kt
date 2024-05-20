package com.goofy.goober.sketchy

import androidx.navigation.NavGraphBuilder
import com.goofy.goober.sketchy.HomeScreens.Audio
import com.goofy.goober.sketchy.HomeScreens.CanvasDrawing
import com.goofy.goober.sketchy.HomeScreens.CanvasRedrawing
import com.goofy.goober.sketchy.HomeScreens.DrawingShapes
import com.goofy.goober.sketchy.HomeScreens.DrawingShapesInteractive
import com.goofy.goober.sketchy.HomeScreens.Grids
import com.goofy.goober.sketchy.HomeScreens.ImageRemixing
import com.goofy.goober.sketchy.HomeScreens.ImageSampling
import com.goofy.goober.sketchy.HomeScreens.Oscillations
import com.goofy.goober.sketchy.HomeScreens.Other
import com.goofy.goober.sketchy.HomeScreens.Particles
import com.goofy.goober.sketchy.HomeScreens.PolarCoords
import com.goofy.goober.sketchy.HomeScreens.Slides
import com.goofy.goober.sketchy.HomeScreens.Texturing
import com.goofy.goober.sketchy.audio.BlobbyViz
import com.goofy.goober.sketchy.audio.Spirals
import com.goofy.goober.sketchy.audio.Histogram
import com.goofy.goober.sketchy.audio.Oscilloscope
import com.goofy.goober.sketchy.audio.Shaders
import com.goofy.goober.sketchy.audio.VisualizerBasics
import com.goofy.goober.sketchy.other.MatrixRain
import com.goofy.goober.sketchy.other.Starfield
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
import com.goofy.goober.sketchy.screens.slides.StatefulCanvasBlobs
import com.goofy.goober.sketchy.screens.slides.TransformWithTexture
import com.goofy.goober.sketchy.temp.dots.BasicGrid
import com.goofy.goober.sketchy.temp.dots.Dot2DNoiseRadius
import com.goofy.goober.sketchy.temp.dots.Dot4DNoiseOffset
import com.goofy.goober.sketchy.temp.dots.DotAnimatedRadiusAndCenterVariation
import com.goofy.goober.sketchy.temp.dots.DotAnimatedRadiusAndOffset
import com.goofy.goober.sketchy.temp.dots.DotParametric
import com.goofy.goober.sketchy.temp.dots.DotSinRadiusVariation
import com.goofy.goober.sketchy.temp.dots.DotStaticXRadiusVariation
import com.goofy.goober.sketchy.temp.dots.DotStaticYRadiusVariation
import com.goofy.goober.sketchy.temp.dots.DotYPendulum
import com.goofy.goober.sketchy.temp.dots.DotYSpread
import com.goofy.goober.sketchy.temp.dots.DotsAroundCircleHalftones
import com.goofy.goober.sketchy.temp.dots.DotsAroundCircleWavy
import com.goofy.goober.sketchy.temp.dots.DotsStaticXYRadiusVariation
import com.goofy.goober.sketchy.temp.dots.HueNoisyUVMesh
import com.goofy.goober.sketchy.temp.dots.HueNoisyXYMesh
import com.goofy.goober.sketchy.temp.dots.Lines2DNoise
import com.goofy.goober.sketchy.temp.dots.Lines3DNoise
import com.goofy.goober.sketchy.temp.dots.Lines4DNoise
import com.goofy.goober.sketchy.temp.dots.MonotoneNoisyUVMesh
import com.goofy.goober.sketchy.temp.dots.MonotoneNoisyXYMesh
import com.goofy.goober.sketchy.temp.dots.Noise2DGrowingDots
import com.goofy.goober.sketchy.temp.dots.Noise3DGrowingDots
import com.goofy.goober.sketchy.temp.dots.NoisyPoints
import com.goofy.goober.sketchy.temp.dots.NoisyXYRandomRectMesh
import com.goofy.goober.sketchy.temp.dots.NoisyXYRectMesh
import com.goofy.goober.sketchy.temp.dots.Random2DDots
import com.goofy.goober.sketchy.temp.dots.Random2DGrowingDots
import com.goofy.goober.sketchy.temp.imageproc.ImageSampling
import com.goofy.goober.sketchy.temp.imageproc.ImageSamplingGestures
import com.goofy.goober.sketchy.temp.osc.Harmonic
import com.goofy.goober.sketchy.temp.osc.ParametricHarmonic
import com.goofy.goober.sketchy.temp.particles.Attractor
import com.goofy.goober.sketchy.temp.particles.Constellation
import com.goofy.goober.sketchy.temp.particles.FlowField
import com.goofy.goober.sketchy.temp.particles.RasterizeAttractor
import com.goofy.goober.sketchy.temp.polar.PolygonsColor
import com.goofy.goober.sketchy.temp.polar.PolygonsComplex
import com.goofy.goober.sketchy.temp.polar.PolygonsSimple

object HomeScreens {
    const val Home = "Sketch"
    const val CanvasDrawing = "Basic Canvas Drawing"
    const val CanvasRedrawing = "Redrawing"
    const val Audio = "Audio"
    const val Grids = "Dippin' Dots"
    const val DrawingShapes = "Shapes"
    const val DrawingShapesInteractive = "Interactive Shapes"
    const val ImageRemixing = "Image Remixing"
    const val ImageSampling = "Image Sampling"
    const val Oscillations = "Oscillations"
    const val Other = "Other"
    const val Particles = "Particles"
    const val PolarCoords = "Polar Coordinates"
    const val Slides = "Creative Coding '23"
    const val Texturing = "Texturing"
}

val TopLevelScreens = listOf(
    NestedNavScreen(
        title = Audio,
        description = "Audio"
    ) { onNavigate -> audioGraph(onNavigate) },
    NestedNavScreen(
        title = Grids,
        description = "Groovy grids!"
    ) { onNavigate -> gridsGraph(onNavigate) },
    NestedNavScreen(
        title = Oscillations,
        description = "Oscillations with trig"
    ) { onNavigate -> oscillationsGraph(onNavigate) },
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
        title = Particles,
        description = "Basic particle systems"
    ) { onNavigate -> particlesGraph(onNavigate) },
    NestedNavScreen(
        title = PolarCoords,
        description = "Polar coordinates + noise"
    ) { onNavigate -> polarCoordsGraph(onNavigate) },
    NestedNavScreen(
        title = Other,
        description = "Other experiences"
    ) { onNavigate -> otherGraph(onNavigate) },
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
    NestedNavScreen(
        title = Slides,
        description = "Specific Examples from the Creative Coding Compose '23 talk"
    ) { onNavigate -> slidesGraph(onNavigate) }
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
    DestinationScreen(title = "Downsampled pt 2") {
        ImageSampling()
    },
    DestinationScreen(title = "Rasterized") {
        Rasterized()
    },
    DestinationScreen(title = "Rasterized Z Index") {
        RasterizedZIndex()
    },
    DestinationScreen(title = "Image sampling & gestures") {
        ImageSamplingGestures()
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
private val SlidesScreens = listOf(
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

fun NavGraphBuilder.slidesGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = SlidesScreens, home = Slides)
}
//endregion

//region Grids
private val GridsScreens = listOf(
    DestinationScreen(title = "BasicGrid") { BasicGrid() },
    DestinationScreen(title = "MonotoneNoisyUVMesh") { MonotoneNoisyUVMesh() },
    DestinationScreen(title = "MonotoneNoisyXYMesh") { MonotoneNoisyXYMesh() },
    DestinationScreen(title = "HueNoisyUVMesh") { HueNoisyUVMesh() },
    DestinationScreen(title = "HueNoisyXYMesh") { HueNoisyXYMesh() },
    DestinationScreen(title = "NoisyXYRectMesh") { NoisyXYRectMesh() },
    DestinationScreen(title = "NoisyXYRandomRectMesh") { NoisyXYRandomRectMesh() },
    DestinationScreen(title = "NoisyPoints") { NoisyPoints() },
    DestinationScreen(title = "Random2DDots") { Random2DDots() },
    DestinationScreen(title = "Noise2DGrowingDots") { Noise2DGrowingDots() },
    DestinationScreen(title = "Noise3DGrowingDots") { Noise3DGrowingDots() },
    DestinationScreen(title = "Random2DGrowingDots") { Random2DGrowingDots() },
    DestinationScreen(title = "DotStaticYRadiusVariation") { DotStaticYRadiusVariation() },
    DestinationScreen(title = "DotStaticXRadiusVariation") { DotStaticXRadiusVariation() },
    DestinationScreen(title = "DotsStaticXYRadiusVariation") { DotsStaticXYRadiusVariation() },
    DestinationScreen(title = "DotYPendulum") { DotYPendulum() },
    DestinationScreen(title = "DotYSpread") { DotYSpread() },
    DestinationScreen(title = "DotSinRadiusVariation") { DotSinRadiusVariation() },
    DestinationScreen(title = "DotAnimatedRadiusAndCenterVariation") { DotAnimatedRadiusAndCenterVariation() },
    DestinationScreen(title = "Dot2DNoiseRadius") { Dot2DNoiseRadius() },
    DestinationScreen(title = "Dot4DNoiseOffset") { Dot4DNoiseOffset() },
    DestinationScreen(title = "Lines2DNoise") { Lines2DNoise() },
    DestinationScreen(title = "Lines3DNoise") { Lines3DNoise() },
    DestinationScreen(title = "Lines4DNoise") { Lines4DNoise() },
    DestinationScreen(title = "DotAnimatedRadiusAndOffset") { DotAnimatedRadiusAndOffset() },
    DestinationScreen(title = "DotParametric") { DotParametric() },
    DestinationScreen(title = "DotsAroundCircleWavy") { DotsAroundCircleWavy() },
    DestinationScreen(title = "DotsAroundCircleHalftones") { DotsAroundCircleHalftones() }
)

fun NavGraphBuilder.gridsGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = GridsScreens, home = Grids)
}
//endregion


//region Audio
fun NavGraphBuilder.audioGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = AudioScreens, home = Audio)
}
private val AudioScreens = listOf(
    DestinationScreen(title = "VisualizerBasics") { VisualizerBasics() },
    DestinationScreen(title = "Oscilloscope") { Oscilloscope() },
    DestinationScreen(title = "Histogram") { Histogram() },
    DestinationScreen(title = "Blobby") { BlobbyViz() },
    DestinationScreen(title = "Shaders") { Shaders() },
    DestinationScreen(title = "Spirals") { Spirals() }
)
//endregion


//region Oscillations
private val OscillationsScreens = listOf(
    DestinationScreen(title = "ParametricHarmonic") { ParametricHarmonic() },
    DestinationScreen(title = "Harmonic") { Harmonic() }
)

fun NavGraphBuilder.oscillationsGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = OscillationsScreens, home = Oscillations)
}
//endregion

//region Particles
private val ParticlesScreens = listOf(
    DestinationScreen(title = "Attractor") { Attractor() },
    DestinationScreen(title = "RasterizeAttractor") { RasterizeAttractor() },
    DestinationScreen(title = "FlowField") { FlowField() },
    DestinationScreen(title = "Constellation") { Constellation() }
)

fun NavGraphBuilder.particlesGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = ParticlesScreens, home = Particles)
}
//endregion

//region PolarCoords
private val PolarCoordsScreens = listOf(
    DestinationScreen(title = "PolygonsColor") { PolygonsColor() },
    DestinationScreen(title = "PolygonsSimple") { PolygonsSimple() },
    DestinationScreen(title = "PolygonsComplex") { PolygonsComplex() },
//    DestinationScreen(title = "BlobbyViz") { BlobbyLoop() },
//    DestinationScreen(title = "Blobby") { Blobby() },
)

fun NavGraphBuilder.polarCoordsGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = PolarCoordsScreens, home = PolarCoords)
}
//endregion

//region Other
private val OtherScreens = listOf(
    DestinationScreen(title = "Matrix Rain") { MatrixRain() },
    DestinationScreen(title = "Starfield") { Starfield() },
)

fun NavGraphBuilder.otherGraph(onNavigate: (Screen) -> Unit) {
    nestedContent(onNavigate, screens = OtherScreens, home = Other)
}
//endregion

