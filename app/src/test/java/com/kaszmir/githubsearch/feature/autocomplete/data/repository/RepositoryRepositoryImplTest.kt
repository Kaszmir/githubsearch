package com.kaszmir.githubsearch.feature.autocomplete.data.repository

import com.kaszmir.githubsearch.feature.autocomplete.data.SearchApi
import com.kaszmir.githubsearch.feature.autocomplete.data.model.GitHubRepositoriesResponseDto
import com.kaszmir.githubsearch.feature.autocomplete.data.model.GitHubRepositoryDto
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchQuery
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class RepositoryRepositoryImplTest {

    private val api: SearchApi = mockk()

    // UnconfinedTestDispatcher because repos don't have
    // any debounce and time logic so this dispatcher usage is easier
    private val repository = RepositoryRepositoryImpl(api, UnconfinedTestDispatcher())

    private val query = SearchQuery(query = "kotlin")

    private val fakeResponse = GitHubRepositoriesResponseDto(
        totalCount = 1,
        incompleteResults = false,
        items = listOf(
            GitHubRepositoryDto(
                id = 1,
                name = "awesome-kotlin",
                stargazersCount = 100
            )
        )
    )

    @Test
    fun `returns success with mapped results when api call succeeds`() = runTest {
        coEvery {
            api.searchRepositories(query.query, query.resultsPerPage)
        } returns fakeResponse

        val result = repository.searchRepositories(query)

        assertTrue(result.isSuccess)
        assertEquals(
            listOf(SearchResult.Repository(
                id = 1,
                displayName = "awesome-kotlin",
                starsCount = "100")
            ),
            result.getOrNull()
        )
    }

    @Test
    fun `returns failure when api throws IOException`() = runTest {
        coEvery {
            api.searchRepositories(query.query, query.resultsPerPage)
        } throws IOException("no internet")

        val result = repository.searchRepositories(query)

        assertTrue(result.isFailure)
        assertEquals("no internet", result.exceptionOrNull()?.message)
    }

    @Test
    fun `returns failure when api throws HttpException`() = runTest {
        val httpException = HttpException(
            Response.error<Any>(403, "Forbidden".toResponseBody())
        )
        coEvery {
            api.searchRepositories(query.query, query.resultsPerPage)
        } throws httpException

        val result = repository.searchRepositories(query)

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is HttpException)
    }

    @Test
    fun `returns empty list when api returns no items`() = runTest {
        coEvery {
            api.searchRepositories(query.query, query.resultsPerPage)
        } returns GitHubRepositoriesResponseDto()

        val result = repository.searchRepositories(query)

        assertTrue(result.isSuccess)
        assertEquals(emptyList<SearchResult.Repository>(), result.getOrNull())
    }
}