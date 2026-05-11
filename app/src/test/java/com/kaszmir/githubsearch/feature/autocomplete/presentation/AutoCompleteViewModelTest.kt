package com.kaszmir.githubsearch.feature.autocomplete.presentation

import app.cash.turbine.test
import com.kaszmir.githubsearch.core.system.UrlOpener
import com.kaszmir.githubsearch.feature.autocomplete.domain.SearchUseCase
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchError
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchException
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AutoCompleteViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val useCase: SearchUseCase = mockk()
    private val urlOpener: UrlOpener = mockk()
    private lateinit var viewModel: AutoCompleteViewModel

    private val fakeResults = listOf(
        SearchResult.User(id = 1, displayName = "test", redirectUrl = "http://gototheweb.com")
    )

    @Before
    fun setUp() {
        viewModel = AutoCompleteViewModel(useCase, urlOpener)
    }

    @Test
    fun `initial state is empty`() = runTest {
        val state = viewModel.uiState.value
        assertNull(state.searchResults)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `query shorter than 3 chars does not trigger search`() = runTest {
        viewModel.onAction(AutoCompleteAction.QueryChanged("te"))
        advanceTimeBy(400)

        assertNull(viewModel.uiState.value.searchResults)
        assertFalse(viewModel.uiState.value.isLoading)
        coVerify(exactly = 0) { useCase(any()) }
    }

    @Test
    fun `query of 3+ chars triggers search and shows results`() = runTest {
        coEvery { useCase(any()) } returns Result.success(fakeResults)

        viewModel.onAction(AutoCompleteAction.QueryChanged("tes"))
        advanceTimeBy(400)

        coVerify(exactly = 1) { useCase(query = "tes") }
        assertEquals(fakeResults, viewModel.uiState.value.searchResults)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `rapid input cancels previous search`() = runTest {
        coEvery { useCase(any()) } returns Result.success(fakeResults)

        viewModel.onAction(AutoCompleteAction.QueryChanged("tes"))
        advanceTimeBy(100) // too short
        viewModel.onAction(AutoCompleteAction.QueryChanged("test"))
        advanceTimeBy(400) // this query should pass

        coVerify(exactly = 1) { useCase(query = "test") } // use case should be called only once
    }

    @Test
    fun `error state is set when use case fails`() = runTest {
        coEvery { useCase(any()) } returns Result.failure(
            SearchException(SearchError.RateLimited)
        )

        viewModel.onAction(AutoCompleteAction.QueryChanged("test"))
        advanceTimeBy(400)

        assertNull(viewModel.uiState.value.searchResults)
        assertEquals("GitHub API rate limit exceeded", viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `clear action resets state`() = runTest {
        coEvery { useCase(any()) } returns Result.success(fakeResults)

        viewModel.onAction(AutoCompleteAction.QueryChanged("test"))
        advanceTimeBy(400)
        viewModel.onAction(AutoCompleteAction.OnClear)

        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertNull(state.searchResults)
        assertNull(state.errorMessage)
    }

    @Test
    fun `shortening query below threshold clears stale results`() = runTest {
        coEvery { useCase(any()) } returns Result.success(fakeResults)

        viewModel.onAction(AutoCompleteAction.QueryChanged("test"))
        advanceTimeBy(400)
        assertEquals(fakeResults, viewModel.uiState.value.searchResults)

        viewModel.onAction(AutoCompleteAction.QueryChanged("te"))

        val state = viewModel.uiState.value
        assertEquals("te", state.query)
        assertNull(state.searchResults)
        assertNull(state.errorMessage)
        assertFalse(state.isLoading)
    }

    @Test
    fun `ResultClicked emits OpenUrlFailed when opener fails`() = runTest {
        every { urlOpener.open(any()) } returns false

        viewModel.effects.test {
            viewModel.onAction(
                AutoCompleteAction.ResultClicked(
                    SearchResult.User(
                        id = 1,
                        displayName = "test-user",
                        redirectUrl = "https://github.com/testuser",
                    )
                )
            )
            assertEquals(AutoCompleteUiEffect.OpenUrlFailed, awaitItem())
        }
    }

    @Test
    fun `ResultClicked emits nothing when opener succeeds`() = runTest {
        every { urlOpener.open(any()) } returns true

        viewModel.effects.test {
            viewModel.onAction(
                AutoCompleteAction.ResultClicked(
                    SearchResult.User(
                        id = 1,
                        displayName = "test-user",
                        redirectUrl = "https://github.com/testuser",
                    )
                )
            )
            expectNoEvents()
        }
    }

    @Test
    fun `ResultClicked with blank redirectUrl emits nothing`() = runTest {
        val user = SearchResult.User(id = 1, displayName = "x", redirectUrl = "")

        viewModel.effects.test {
            viewModel.onAction(AutoCompleteAction.ResultClicked(user))
            expectNoEvents()
        }
    }
}