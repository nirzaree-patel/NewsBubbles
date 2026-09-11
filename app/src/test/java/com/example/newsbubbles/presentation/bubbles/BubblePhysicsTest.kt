package com.example.newsbubbles.presentation.bubbles

import com.example.newsbubbles.domain.model.NewsCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class BubblePhysicsTest {

    private fun bubble(x: Float, y: Float, vx: Float = 0f, vy: Float = 0f, radius: Float = 50f) =
        BubbleState(uiModel = BubbleUiModel.from(NewsCategory.GENERAL), radius = radius, x = x, y = y, vx = vx, vy = vy)

    @Test
    fun `moves by velocity times delta time`() {
        val b = bubble(x = 100f, y = 100f, vx = 20f, vy = -10f)

        BubblePhysics.step(listOf(b), canvasWidth = 1000f, canvasHeight = 1000f, deltaSeconds = 0.5f)

        assertEquals(110f, b.x, 0.001f)
        assertEquals(95f, b.y, 0.001f)
    }

    @Test
    fun `bounces off the right wall and clamps inside bounds`() {
        val b = bubble(x = 480f, y = 300f, vx = 100f, vy = 0f, radius = 50f)

        BubblePhysics.step(listOf(b), canvasWidth = 500f, canvasHeight = 1000f, deltaSeconds = 1f)

        assertEquals(450f, b.x, 0.001f) // clamped to width - radius
        assertTrue("vx should reflect to negative", b.vx < 0f)
    }

    @Test
    fun `bounces off the left wall`() {
        val b = bubble(x = 20f, y = 300f, vx = -100f, vy = 0f, radius = 50f)

        BubblePhysics.step(listOf(b), canvasWidth = 500f, canvasHeight = 1000f, deltaSeconds = 1f)

        assertEquals(50f, b.x, 0.001f) // clamped to radius
        assertTrue("vx should reflect to positive", b.vx > 0f)
    }

    @Test
    fun `bounces off the top inset instead of the raw top edge`() {
        val b = bubble(x = 300f, y = 120f, vx = 0f, vy = -100f, radius = 50f)

        BubblePhysics.step(listOf(b), canvasWidth = 1000f, canvasHeight = 1000f, deltaSeconds = 1f, topInset = 100f)

        assertEquals(150f, b.y, 0.001f) // clamped to topInset + radius
        assertTrue("vy should reflect to positive", b.vy > 0f)
    }

    @Test
    fun `overlapping approaching bubbles separate and exchange velocity along the normal`() {
        // Same y, overlapping on x, moving toward each other.
        val a = bubble(x = 100f, y = 100f, vx = 50f, vy = 0f, radius = 50f)
        val b = bubble(x = 160f, y = 100f, vx = -50f, vy = 0f, radius = 50f)

        BubblePhysics.step(listOf(a, b), canvasWidth = 1000f, canvasHeight = 1000f, deltaSeconds = 0f)

        val dist = sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y))
        assertTrue("bubbles should no longer overlap", dist >= a.radius + b.radius - 0.01f)
        // Equal-mass head-on collision along the x-axis swaps velocities.
        assertEquals(-50f, a.vx, 0.001f)
        assertEquals(50f, b.vx, 0.001f)
    }

    @Test
    fun `non-overlapping bubbles are left untouched by collision resolution`() {
        val a = bubble(x = 200f, y = 200f, vx = 10f, vy = 10f, radius = 20f)
        val b = bubble(x = 500f, y = 500f, vx = -10f, vy = -10f, radius = 20f)

        BubblePhysics.step(listOf(a, b), canvasWidth = 1000f, canvasHeight = 1000f, deltaSeconds = 0f)

        assertEquals(10f, a.vx, 0.001f)
        assertEquals(10f, a.vy, 0.001f)
        assertEquals(-10f, b.vx, 0.001f)
        assertEquals(-10f, b.vy, 0.001f)
    }
}
