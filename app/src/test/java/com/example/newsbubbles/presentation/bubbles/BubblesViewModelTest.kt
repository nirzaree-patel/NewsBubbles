package com.example.newsbubbles.presentation.bubbles

import com.example.newsbubbles.MainDispatcherRule
import com.example.newsbubbles.domain.model.NewsCategory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class BubblesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `starts with no bubbles`() {
        assertEquals(emptyList<BubbleState>(), BubblesViewModel().bubbles.value)
    }

    @Test
    fun `initialize creates one bubble per category, inside canvas bounds`() = runTest {
        val vm = BubblesViewModel()

        vm.initialize(canvasWidth = 1000f, canvasHeight = 2000f, topInset = 100f)

        val bubbles = vm.bubbles.value
        assertEquals(NewsCategory.entries.size, bubbles.size)
        assertEquals(NewsCategory.entries.toSet(), bubbles.map { it.uiModel.category }.toSet())
        bubbles.forEach { b ->
            assertTrue(b.x - b.radius >= -0.01f && b.x + b.radius <= 1000f + 0.01f)
            assertTrue(b.y - b.radius >= 100f - 0.01f && b.y + b.radius <= 2000f + 0.01f)
        }
    }

    @Test
    fun `initialize is a no-op with zero dimensions`() = runTest {
        val vm = BubblesViewModel()

        vm.initialize(canvasWidth = 0f, canvasHeight = 1000f, topInset = 0f)

        assertEquals(emptyList<BubbleState>(), vm.bubbles.value)
    }

    @Test
    fun `initialize only runs once`() = runTest {
        val vm = BubblesViewModel()

        vm.initialize(canvasWidth = 1000f, canvasHeight = 1000f, topInset = 0f)
        val firstBubbles = vm.bubbles.value

        vm.initialize(canvasWidth = 500f, canvasHeight = 500f, topInset = 0f)

        assertEquals(firstBubbles, vm.bubbles.value)
    }

    @Test
    fun `step advances bubble positions`() {
        val vm = BubblesViewModel()
        vm.initialize(canvasWidth = 1000f, canvasHeight = 1000f, topInset = 0f)
        val before = vm.bubbles.value.map { it.x to it.y }

        vm.step(canvasWidth = 1000f, canvasHeight = 1000f, deltaSeconds = 1f, topInset = 0f)

        val after = vm.bubbles.value.map { it.x to it.y }
        assertTrue("at least one bubble should have moved", before != after)
    }

    @Test
    fun `step is a no-op before initialize`() {
        val vm = BubblesViewModel()

        vm.step(canvasWidth = 1000f, canvasHeight = 1000f, deltaSeconds = 1f, topInset = 0f)

        assertEquals(emptyList<BubbleState>(), vm.bubbles.value)
    }
}
