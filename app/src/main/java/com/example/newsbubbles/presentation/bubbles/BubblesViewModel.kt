package com.example.newsbubbles.presentation.bubbles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import javax.inject.Inject

/**
 * ViewModel for the Bubbles (home) screen.
 *
 * Responsibilities:
 *  - Initialises [BubbleState] list once canvas dimensions are known.
 *  - Advances physics each frame via [BubblePhysics.step].
 *
 * No use case is needed here, bubble animation is purely a presentation concern
 * with no domain business logic.
 */
@HiltViewModel
class BubblesViewModel @Inject constructor() : ViewModel() {

    private val _bubbles = MutableStateFlow<List<BubbleState>>(emptyList())
    val bubbles: StateFlow<List<BubbleState>> = _bubbles.asStateFlow()

    private var initialized = false

    fun initialize(canvasWidth: Float, canvasHeight: Float, topInset: Float) {
        if (initialized || canvasWidth == 0f || canvasHeight == 0f) return
        initialized = true

        viewModelScope.launch {
            // Base size derived from screen size, then randomised per bubble so bubbles
            // aren't all identical — each one lands somewhere in [0.7x, 1.4x] of the base.
            val baseRadius = (minOf(canvasWidth, canvasHeight - topInset) / 5f).coerceIn(70f, 150f)
            val speed = 120f   // px/s
            val uiModels = BubbleUiModel.all
            val usableHeight = canvasHeight - topInset

            val states = uiModels.mapIndexed { index, uiModel ->
                val radius = (baseRadius * (0.7f + Random.nextFloat() * 0.7f)).coerceIn(55f, 190f)
                val angle = (2 * PI / uiModels.size * index).toFloat()
                val cx = (canvasWidth / 2f + cos(angle) * canvasWidth * 0.28f)
                    .coerceIn(radius, canvasWidth - radius)
                val cy = (topInset + usableHeight / 2f + sin(angle) * usableHeight * 0.28f)
                    .coerceIn(topInset + radius, canvasHeight - radius)

                val vAngle = Random.nextFloat() * 2f * PI.toFloat()
                BubbleState(
                    uiModel = uiModel,
                    radius = radius,
                    x = cx,
                    y = cy,
                    vx = cos(vAngle) * speed,
                    vy = sin(vAngle) * speed
                )
            }
            _bubbles.value = states
        }
    }

    fun step(canvasWidth: Float, canvasHeight: Float, deltaSeconds: Float, topInset: Float) {
        val current = _bubbles.value
        if (current.isEmpty() || deltaSeconds <= 0f) return
        BubblePhysics.step(current, canvasWidth, canvasHeight, deltaSeconds, topInset)
    }
}
