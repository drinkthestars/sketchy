package com.goofy.goober.sketchy.other

import android.content.Context
import android.text.TextPaint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import com.goofy.goober.sketchy.R
import com.goofy.goober.sketchy.mapTo
import java.util.Random
import android.graphics.Color as AndroidColor

private val Random = Random()

// Colors
private val BrightGreen = AndroidColor.rgb(0, 253, 32)
private val LightGreen = AndroidColor.rgb(225, 254, 233)
private val LighterGreen = AndroidColor.rgb(187, 250, 217)
private val GlyphShadow = AndroidColor.rgb(122, 250, 220)

// Glyph ints
private const val GlyphMaxCharInt = 126
private const val AsperandInt = 64
private const val BackwardsTwoInt = 50

// Glyphs
private const val MinGlyphCount = 10
private const val DefaultMaxGlyphCount = 90
private const val TextSize = 45
private const val GlyphShadowRadius = TextSize + 14f
private const val RandomizeCharThresh = 0.003f

// Streams
private const val AdvanceResetMax = 1000
private const val HighlightCharCount = 3
private const val StreamSpacingDelta = 15
private const val StartingYOffset = -(10 * TextSize)
private const val FrameRate = 16

// Note: smaller denominator = slower
private const val FastSpeedMin = FrameRate / 6
private const val FastSpeedMax = FrameRate / 3
private const val SlowSpeedMin = FrameRate / 2
private const val SlowSpeedMax = FrameRate * 2

@Composable
fun MatrixRain() {
    val context = LocalContext.current
    val paint = remember { paint(context) }
    val streams = remember { mutableListOf<Stream>() }
    var width = remember { 0 }
    var advance by remember { mutableIntStateOf(0) }

    Canvas(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .onSizeChanged {
            if (width == 0 && it.width != 0) {
                width = it.width
                streams.addStreams(width, it.height)
            }
        },
        onDraw = {
            streams.forEach { it.draw(paint, this, advance) }
            if (advance == AdvanceResetMax) advance = 0 else advance += 1
        }
    )
}

private class Glyph(val xPos: Float, var yPos: Float) {
    var char: Char = randomChar()

    fun randomize() {
        char = randomChar()
    }
}

private class Stream(
    xPos: Int,
    private val height: Int,
    private var speed: Int,
    maxGlyphs: Int = DefaultMaxGlyphCount,
    private val fixedAlpha: Int? = null,
) {

    private val glyphs: ArrayList<Glyph> = ArrayList(maxGlyphs)
    private val glyphCount: Int = randomInt(MinGlyphCount, maxGlyphs)
    private val glyphYDelta: Int = randomInt(0, height - TextSize)
    private var highlightRange: IntRange
    private val glyphsLastIndex: Int

    init {
        var yPos = StartingYOffset
        while (yPos < glyphCount * TextSize) {
            glyphs.add(Glyph(xPos.toFloat(), (yPos + glyphYDelta).toFloat()))
            yPos += TextSize
        }
        highlightRange = (glyphs.size - HighlightCharCount)..glyphs.size
        glyphsLastIndex = glyphs.lastIndex
    }

    fun draw(paint: TextPaint, drawScope: DrawScope, advance: Int) {
        glyphs.forEachIndexed { index, glyph ->
            val alpha = index.mapTo(
                sourceMin = 0f,
                sourceMax = glyphsLastIndex.toFloat(),
                destMin = 40f,
                destMax = 255f
            )

            val color = if (index in highlightRange) {
                if (index == glyphsLastIndex) LightGreen else LighterGreen
            } else {
                BrightGreen
            }

            if (advance % speed == 0) {
                glyph.yPos += TextSize

                if (index == glyphsLastIndex) {
                    glyph.randomize()
                } else {
                    glyph.char = glyphs[index + 1].char
                }
            }

            if (Random.nextFloat() < RandomizeCharThresh) glyph.randomize()

            glyph.draw(
                paint = paint,
                drawScope = drawScope,
                color = color,
                dynamicAlpha = alpha.toInt(),
                fixedAlpha = fixedAlpha
            )
        }

        resetYPos()
    }

    private fun resetYPos() {
        if (glyphs[0].yPos > height) {
            glyphs.forEachIndexed { index, glyph ->
                glyph.yPos = ((glyphsLastIndex - index) * -TextSize).toFloat()
            }
        }
    }

    private fun Glyph.draw(
        paint: TextPaint,
        drawScope: DrawScope,
        color: Int,
        fixedAlpha: Int?,
        dynamicAlpha: Int
    ) {
        drawScope.drawIntoCanvas {
            it.nativeCanvas.drawText(
                charArrayOf(char),
                0,
                1,
                xPos,
                yPos,
                paint.apply {
                    this.color = color
                    if (fixedAlpha != null) {
                        this.alpha = fixedAlpha
                    } else {
                        this.alpha = dynamicAlpha
                    }
                }
            )
        }
    }
}

private fun randomChar(): Char {
    // Exclude @s
    val randomInt = randomInt(0, GlyphMaxCharInt)
    return (if (randomInt == AsperandInt) BackwardsTwoInt else randomInt).toChar()
}

private fun MutableList<Stream>.addStreams(width: Int, height: Int) {
    var streamXPos = 0
    while (streamXPos < width) {
        add(
            Stream(
                xPos = streamXPos,
                height = height,
                fixedAlpha = 25,
                maxGlyphs = 20,
                speed = slowSpeed()
            )
        )
        streamXPos += TextSize * 2
    }
    streamXPos = 0
    while (streamXPos < width) {
        add(
            Stream(
                xPos = streamXPos,
                height = height,
                speed = fastSpeed()
            )
        )
        streamXPos += TextSize - StreamSpacingDelta
    }
}

private fun randomInt(from: Int, to: Int): Int {
    return Random.nextInt(to - from + 1) + from
}

private fun paint(context: Context): TextPaint {
    return TextPaint().apply {
        // https://www.norfok.com/portfolio-freeware_matrixcode.html
        typeface = ResourcesCompat.getFont(context, R.font.matrix_code_nfi)
        textSize = TextSize.toFloat()
        isAntiAlias = true
        isDither = false
        setShadowLayer(
            GlyphShadowRadius,
            1.0f,
            -1.0f,
            GlyphShadow
        )
    }
}

private fun fastSpeed() = randomInt(FastSpeedMin, FastSpeedMax)
private fun slowSpeed() = randomInt(SlowSpeedMin, SlowSpeedMax)