package com.goofy.goober.sketch

import android.graphics.RuntimeShader
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalView
import kotlin.math.roundToInt

@Composable
fun CanvasRecorder(
    modifier: Modifier = Modifier,
    drawRecorder: DrawRecorder = remember { DrawRecorder() },
    onDraw: DrawScope.() -> Unit
) {
    val view = LocalView.current
    view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
    Box(
        modifier = modifier
            .drawWithCache {
                val drawContext = drawRecorder.create(size)
                drawContext.drawScope.draw(
                    density = this,
                    layoutDirection = layoutDirection,
                    canvas = drawContext.canvas,
                    size = size
                ) {
                    onDraw()
                }

                onDrawBehind {
                    drawImage(drawContext.imageBitmap)
                }
            }
    )
}

class DrawRecorder {
    val state = mutableStateOf<DrawContext?>(null)

    fun create(size: Size): DrawContext {
        var drawContext = state.value
        if (drawContext == null) {
            val imageBitmap = ImageBitmap(size.width.roundToInt(), size.height.roundToInt())
            val canvas = Canvas(imageBitmap)
            val drawScope = CanvasDrawScope()
            drawContext = DrawContext(imageBitmap, canvas, drawScope)
            state.value = drawContext
        }
        return drawContext
    }
}

data class DrawContext(
    val imageBitmap: ImageBitmap,
    val canvas: Canvas,
    val drawScope: CanvasDrawScope
)

/**
 * Messing around from other shaders here plus other references:
 * - ChatGPT
 * - https://lygia.xyz/
 * - https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83 by patriciogonzalezvivo
 */
val MarbledTexture = RuntimeShader(
    """
        uniform float2 resolution;
        uniform shader image; 
      
        float mod289(float x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float2 mod289(float2 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float3 mod289(float3 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        float4 mod289(float4 x) { return x - floor(x * (1.0 / 289.0)) * 289.0; }
        
        float permute(float x) { return mod289((34.0 * x + 1.0) * x); }
        float3 permute(float3 x) { return mod289((34.0 * x + 1.0) * x); }
        
        float4 permute(float4 x) {
          return mod289(((x * 34.0) + 1.0) * x);
        }
        
        vec4 noise2(vec2 uv) {
          vec4 n = vec4(fract(sin(dot(uv.xy, vec2(12.9898,78.233))) * 43758.5453));
          return vec4(n.x, n.y, n.z, n.w);
        }
        
        float snoise(float2 v) {
          const float4 C = float4(0.211324865405187, 0.366025403784439, -0.577350269189626, 0.024390243902439);
          float2 i  = floor(v + dot(v, C.yy));
          float2 x0 = v - i + dot(i, C.xx);
          float2 i1;
          i1 = (x0.x > x0.y) ? float2(1.0, 0.0) : float2(0.0, 1.0);
          float4 x12 = x0.xyxy + C.xxzz;
          x12.xy -= i1;
          i = mod289(i);
          float3 p = permute(permute(i.y + float3(0.0, i1.y, 1.0)) + i.x + float3(0.0, i1.x, 1.0));
          float3 m = max(0.5 - float3(dot(x0, x0), dot(x12.xy, x12.xy), dot(x12.zw, x12.zw)), 0.0);
          m = m * m;
          m = m * m;
          float3 x = 2.0 * fract(p * C.www) - 1.0;
          float3 h = abs(x) - 0.5;
          float3 ox = floor(x + 0.5);
          float3 a0 = x - ox;
          m *= 1.79284291400159 - 0.85373472095314 * (a0 * a0 + h * h);
          float3 g;
          g.x = a0.x * x0.x + h.x * x0.y;
          g.yz = a0.yz * x12.xz + h.yz * x12.yw;
          return 130.0 * dot(m, g);
        }
        
        half4 main( vec2 fragCoord )  {
          vec2 uv = fragCoord.xy / resolution.xy;
        
            if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
                return image.eval(fragCoord);
            }
        
            half4 grain = half4(snoise(fragCoord * 0.1) * 0.1 + 0.5);
            half4 fiber = half4(snoise(uv * 20.0) * 0.1 + 0.5);
            half4 dots = half4(snoise(fragCoord * 0.03) * 0.1 + 0.5);
        
            half4 randomSpecs = half4(0.0, 0.0, 0.0, 0.0);
            if (fract(dots.x * 10.0) > 0.8) {
                randomSpecs = half4(0.1, 0.1, 0.1, 1.0);
            }
        
            half4 randomFibers = half4(0.0, 0.0, 0.0, 0.0);
            if (fract(fiber.y * 10.0 + uv.y * 10.0) > 0.95) {
                randomFibers = half4(0.2, 0.2, 0.2, 1.0);
            }
        
            half4 baseColor = image.eval(fragCoord);
            half4 combinedNoise = baseColor + grain * 0.05 + fiber * 0.1 + randomSpecs + randomFibers;
            return mix(baseColor, combinedNoise, 0.8);
        }
    """
)
