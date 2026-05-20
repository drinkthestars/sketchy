package com.goofy.goober.sketchy

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class UtilsTest {

    @Test
    fun `norm returns 0 at min`() {
        assertEquals(0f, norm(0f, 0f, 100f), 0f)
    }

    @Test
    fun `norm returns 1 at max`() {
        assertEquals(1f, norm(100f, 0f, 100f), 0f)
    }

    @Test
    fun `norm returns 0_5 at midpoint`() {
        assertEquals(0.5f, norm(50f, 0f, 100f), 0.0001f)
    }

    @Test
    fun `lerp returns min when norm is 0`() {
        assertEquals(10f, lerp(0f, 10f, 50f), 0f)
    }

    @Test
    fun `lerp returns max when norm is 1`() {
        assertEquals(50f, lerp(1f, 10f, 50f), 0f)
    }

    @Test
    fun `lerp and norm are inverse operations`() {
        val value = 37f
        val min = 10f
        val max = 80f
        assertEquals(value, lerp(norm(value, min, max), min, max), 0.0001f)
    }

    @Test
    fun `map remaps value between ranges`() {
        assertEquals(5f, map(50f, 0f, 100f, 0f, 10f), 0.0001f)
    }

    @Test
    fun `map returns destMin at source min`() {
        assertEquals(0f, map(0f, 0f, 100f, 0f, 10f), 0f)
    }

    @Test
    fun `map returns destMax at source max`() {
        assertEquals(10f, map(100f, 0f, 100f, 0f, 10f), 0f)
    }

    @Test
    fun `Int mapTo delegates to map`() {
        assertEquals(5f, 50.mapTo(0f, 100f, 0f, 10f), 0.0001f)
    }

    @Test
    fun `Float mapTo delegates to map`() {
        assertEquals(5f, 50f.mapTo(0f, 100f, 0f, 10f), 0.0001f)
    }

    @Test
    fun `Random nextFloat stays within range`() {
        val min = 3f
        val max = 7f
        repeat(1000) {
            val v = Random.nextFloat(min, max)
            assertTrue("$v not in [$min, $max]", v >= min && v < max)
        }
    }
}
