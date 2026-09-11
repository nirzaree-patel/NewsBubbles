package com.example.newsbubbles.presentation.bubbles

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import kotlin.math.sqrt

/**
 * Mutable physics state for a single bubble. All values are in pixels.
 *
 * [x] and [y] are backed by Compose's snapshot state (`mutableFloatStateOf`) rather than
 * plain vars. Mutating them here — from [BubblePhysics.step], once per frame — invalidates
 * only the composables that read them, so the UI animates without ever needing to publish
 * a new List/StateFlow value. (A data class with plain `var` fields mutated in place would
 * make every frame's list "equal" to the last by reference, which StateFlow treats as a
 * no-op and never emits — the bug this class works around.)
 */
class BubbleState(
    val uiModel: BubbleUiModel,
    val radius: Float,
    x: Float,
    y: Float,
    var vx: Float,
    var vy: Float
) {
    var x by mutableFloatStateOf(x)
    var y by mutableFloatStateOf(y)
}

/**
 * Stateless physics engine — no Compose, no Android dependencies.
 *
 * Each call to [step] mutates the [BubbleState] list in-place:
 *   1. Move by velocity × Δt.
 *   2. Reflect off screen edges.
 *   3. Resolve bubble–bubble elastic collisions (equal mass, O(n²), fine for n=7).
 */
object BubblePhysics {

    fun step(
        bubbles: List<BubbleState>,
        canvasWidth: Float,
        canvasHeight: Float,
        deltaSeconds: Float,
        topInset: Float = 0f
    ) {
        move(bubbles, deltaSeconds)
        bounceWalls(bubbles, canvasWidth, canvasHeight, topInset)
        resolveBubbleCollisions(bubbles)
    }

    private fun move(bubbles: List<BubbleState>, dt: Float) {
        for (b in bubbles) {
            b.x += b.vx * dt
            b.y += b.vy * dt
        }
    }

    private fun bounceWalls(bubbles: List<BubbleState>, width: Float, height: Float, topInset: Float) {
        for (b in bubbles) {
            if (b.x - b.radius < 0f) { b.x = b.radius; b.vx = -b.vx }
            else if (b.x + b.radius > width) { b.x = width - b.radius; b.vx = -b.vx }

            if (b.y - b.radius < topInset) { b.y = topInset + b.radius; b.vy = -b.vy }
            else if (b.y + b.radius > height) { b.y = height - b.radius; b.vy = -b.vy }
        }
    }

    private fun resolveBubbleCollisions(bubbles: List<BubbleState>) {
        for (i in bubbles.indices) {
            for (j in i + 1 until bubbles.size) {
                val a = bubbles[i]
                val b = bubbles[j]

                val dx = b.x - a.x
                val dy = b.y - a.y
                val dist = sqrt(dx * dx + dy * dy)
                val minDist = a.radius + b.radius

                if (dist < minDist && dist > 0f) {
                    // Collision normal
                    val nx = dx / dist
                    val ny = dy / dist

                    // Separate overlapping circles
                    val overlap = (minDist - dist) / 2f
                    a.x -= nx * overlap
                    a.y -= ny * overlap
                    b.x += nx * overlap
                    b.y += ny * overlap

                    // Elastic velocity exchange along normal (equal mass)
                    val dvx = a.vx - b.vx
                    val dvy = a.vy - b.vy
                    val dot = dvx * nx + dvy * ny
                    if (dot > 0f) {          // only resolve if approaching
                        a.vx -= dot * nx;  a.vy -= dot * ny
                        b.vx += dot * nx;  b.vy += dot * ny
                    }
                }
            }
        }
    }
}
