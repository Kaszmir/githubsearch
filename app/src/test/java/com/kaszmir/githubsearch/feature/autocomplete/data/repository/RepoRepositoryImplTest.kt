package com.kaszmir.githubsearch.feature.autocomplete.data.repository

import com.kaszmir.githubsearch.feature.autocomplete.data.SearchApi
import com.kaszmir.githubsearch.feature.autocomplete.data.model.GitHubRepositoriesResponseDto
import com.kaszmir.githubsearch.feature.autocomplete.data.model.GitHubRepositoryDto
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchError
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchException
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
class RepoRepositoryImplTest {

    private val api: SearchApi = mockk()

    // UnconfinedTestDispatcher because repos don't have
    // any debounce and time logic so this dispatcher usage is easier
    private val repository = RepoRepositoryImpl(api, UnconfinedTestDispatcher())

    private val query = "kotlin"
    private val resultsPerPage = 50

    private val fakeResponse = GitHubRepositoriesResponseDto(
        totalCount = 1,
        incompleteResults = false,
        items = listOf(
            GitHubRepositoryDto(
                id = 1,
                name = "awesome-kotlin",
                stargazersCount = 100,
                htmlUrl = "http://gototheweb.com"
            )
        )
    )

    @Test
    fun `returns success with mapped results when api call succeeds`() = runTest {
        coEvery {
            api.searchRepositories(query, resultsPerPage)
        } returns fakeResponse

        val result = repository.searchRepositories(query)

        assertTrue(result.isSuccess)
        assertEquals(
            listOf(SearchResult.Repository(
                id = 1,
                displayName = "awesome-kotlin",
                starsCount = "100",
                redirectUrl = "http://gototheweb.com"
                )
            ),
            result.getOrNull()
        )
    }

    @Test
    fun `returns SearchErrorNoConnection when api throws IOException`() = runTest {
        coEvery {
            api.searchRepositories(query, resultsPerPage)
        } throws IOException("no internet")

        val result = repository.searchRepositories(query)

        assertTrue(result.isFailure)
        assertEquals(
            SearchError.NoConnection,
            (result.exceptionOrNull() as SearchException).error
        )
    }

    @Test
    fun `returns SearchErrorRateLimited when api throws HttpException`() = runTest {
        val httpException = HttpException(
            Response.error<Any>(403, "Forbidden".toResponseBody())
        )
        coEvery {
            api.searchRepositories(query, resultsPerPage)
        } throws httpException

        val result = repository.searchRepositories(query)

        assertTrue(result.isFailure)
        assertEquals(
            SearchError.RateLimited,
            (result.exceptionOrNull() as SearchException).error
        )
    }

    @Test
    fun `returns empty list when api returns no items`() = runTest {
        coEvery {
            api.searchRepositories(query, resultsPerPage)
        } returns GitHubRepositoriesResponseDto()

        val result = repository.searchRepositories(query)

        assertTrue(result.isSuccess)
        assertEquals(emptyList<SearchResult.Repository>(), result.getOrNull())
    }
}