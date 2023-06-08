package com.goofy.goober.shaders

import android.graphics.RuntimeShader
import org.intellij.lang.annotations.Language

val SimpleShader = RuntimeShader(
    """
    uniform float2 resolution;
    uniform shader image; 
    
    vec4 main( vec2 fragCoord ) {
        // Normalized pixel coordinates (from 0 to 1)
        vec2 uv = fragCoord / resolution.xy;
        
        // Output to screen
        return vec4(uv.x, uv.y, 0.0, 1.0);
    }
    """.trimIndent()
)

/**
 * Tweaked from: https://www.shadertoy.com/view/3sGGRz Created by terchapone
 */
val NoiseGrain1 = RuntimeShader( /** shader code **/
    """
    uniform float2 resolution;
    uniform shader image; 
    uniform float intensity;
    
    vec4 main( vec2 fragCoord ) {
        vec2 uv = fragCoord/resolution.xy;
        
        // check if pixel is inside viewport bounds
        if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
            return vec4(image.eval(fragCoord));
        }
        
        // 1. create noise…
        float noiseFactor = -0.8 * intensity; // increase for noise amount 
        float noise = (fract(sin(dot(uv, vec2(12.9898,78.233)*2.0)) * 43758.5453));
        
        // 2. get color of image at fragCoord
        vec4 inputCol = vec4(image.eval(fragCoord));
        
        // 3. mix noise with image color
        noiseFactor *= 0.6; // adjust noise factor
        
        vec4 noiseCol = vec4(vec3(noise * noiseFactor), 0.0);
        
        vec4 finalCol = mix(inputCol, inputCol - noiseCol, 0.6); // Apply half of the noise reduction
        finalCol += inputCol * 0.1; // Add 10% of original color back to brighten it up
     
        finalCol.a = inputCol.a; // Keep the original alpha value
        return finalCol;
    }
    """.trimIndent()
)

/**
 * Tweaked from:
 * https://simonharris.co/making-a-noise-film-grain-post-processing-effect-from-scratch-in-threejs/
 */
val NoiseGrain2 = RuntimeShader(
    """
    uniform float2 resolution;
    uniform shader image;
    uniform float intensity;
    
    float random( vec2 p )
    {
        vec2 K1 = vec2(
            23.14069263277926, // e^pi (Gelfond's constant)
            2.665144142690225 // 2^sqrt(2) (Gelfond–Schneider constant)
        );
        return fract( cos( dot(p,K1) ) * 43758.5453 ); // 43758.5453
    }
    
    vec4 main( vec2 fragCoord )  {
        // Normalized pixel coordinates (from 0 to 1)
        vec2 uv = fragCoord/resolution.xy;
        
        // Check if pixel is inside viewport bounds
        if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
            return vec4(image.eval(fragCoord));
        }
        
        vec2 uvRandom = uv;
        float amount = 0.2;
        uvRandom.y *= random(vec2(uvRandom.y,amount));
        vec4 inputColor = vec4(image.eval(fragCoord));
        inputColor.rgb += random(uvRandom)*intensity;
    
        return vec4(inputColor);
    }
    """.trimIndent()
)

/**
 * Lighter grain by varying the * 0.15 + 0.16 coefficients
 *
 * Tweaked from:
 * https://github.com/Robpayot/risograph-grain-shader/blob/master/src/demo1/js/shaders/grain.frag
 */
val Risograph = RuntimeShader(
    """
    uniform float2 resolution;
    uniform shader image; 
    uniform float randomization;
    uniform float randomizationOffset;
    
    float random( vec2 p )
    {
        vec2 K1 = vec2(
            23.14069263277926, // e^pi (Gelfond's constant)
            2.665144142690225 // 2^sqrt(2) (Gelfond–Schneider constant)
        );
        return fract( cos( dot(p,K1) ) * 43758.5453 );
    }
    
    float noise( vec2 uv )
    {
      vec2 K1 = vec2(12.9898,78.233);
    	return (fract(sin(dot(uv, K1*2.0)) * 43758.5453));
    }
    
    vec4 main( vec2 fragCoord )  {
        // Normalized pixel coordinates (from 0 to 1)
        vec2 uv = fragCoord/resolution.xy;
        
        // Check if pixel is inside viewport bounds
        if (fragCoord.x < 0.0 || fragCoord.x > resolution.x || fragCoord.y < 0.0 || fragCoord.y > resolution.y) {
            return vec4(image.eval(fragCoord));
        }
        
        vec2 uvRandom = uv;
        float amount = 0.8;
        uvRandom.y *= noise(vec2(uvRandom.y,amount));
        vec4 inputColor = vec4(image.eval(fragCoord));
        vec4 originalinputColor = inputColor;
        inputColor.rgb += random(uvRandom) * randomization + randomizationOffset;
      
        
        float r = max(inputColor.r, originalinputColor.r);
        float g = max(inputColor.g, originalinputColor.g);
        float b = max(inputColor.b, originalinputColor.b);
        float a = 1.0;
      
        return vec4(r, g, b, a);
    }
    """.trimIndent()
)
