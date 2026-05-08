package com.kaszmir.githubsearch.feature.autocomplete.presentation

import com.kaszmir.githubsearch.feature.autocomplete.domain.SearchUseCase
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchQuery
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import io.mockk.coEvery
import io.mockk.coVerify
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
    private lateinit var viewModel: AutoCompleteViewModel

    private val fakeResults = listOf(
        SearchResult.User(id = 1, displayName = "test", pictureUrl = "")
    )

    @Before
    fun setUp() {
        viewModel = AutoCompleteViewModel(useCase)
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

        coVerify(exactly = 1) { useCase(SearchQuery(query = "tes")) }
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

        coVerify(exactly = 1) { useCase(SearchQuery(query = "test")) } // use case should be called only once
    }

    @Test
    fun `error state is set when use case fails`() = runTest {
        coEvery { useCase(any()) } returns Result.failure(Exception("error"))

        viewModel.onAction(AutoCompleteAction.QueryChanged("test"))
        advanceTimeBy(400)

        assertNull(viewModel.uiState.value.searchResults)
        assertEquals("error", viewModel.uiState.value.errorMessage)
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
}