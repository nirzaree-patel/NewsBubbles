package com.example.newsbubbles

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Swaps [kotlinx.coroutines.Dispatchers.Main] for a [TestDispatcher] so ViewModels using
 * `viewModelScope` (which dispatches on `Dispatchers.Main.immediate`) can be tested on the JVM.
 *
 * Uses [UnconfinedTestDispatcher] (not [kotlinx.coroutines.test.StandardTestDispatcher]) so
 * `viewModelScope.launch { ... }` bodies run eagerly to their first real suspension point,
 * rather than needing a shared `TestCoroutineScheduler` with each test's `runTest` — with a
 * mocked suspend call that returns immediately, this makes StateFlow updates observable
 * synchronously, no `advanceUntilIdle()` required.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        kotlinx.coroutines.Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        kotlinx.coroutines.Dispatchers.resetMain()
    }
}
