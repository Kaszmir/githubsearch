package com.kaszmir.githubsearch.feature.autocomplete.data.repository

import com.kaszmir.githubsearch.feature.autocomplete.data.SearchApi
import com.kaszmir.githubsearch.feature.autocomplete.data.model.GitHubUserDto
import com.kaszmir.githubsearch.feature.autocomplete.data.model.GitHubUsersResponseDto
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
class UserRepositoryImplTest {

    private val api: SearchApi = mockk()

    // UnconfinedTestDispatcher because repos don't have
    // any debounce and time logic so this dispatcher usage is easier
    private val repository = UserRepositoryImpl(api, UnconfinedTestDispatcher())

    private val query = "test"
    private val resultsPerPage = 50

    private val fakeResponse = GitHubUsersResponseDto(
        totalCount = 1,
        incompleteResults = false,
        items = listOf(
            GitHubUserDto(
                id = 1,
                login = "testdev",
                avatarUrl = "https://avatar.url"
            )
        )
    )

    @Test
    fun `returns success with mapped results when api call succeeds`() = runTest {
        // Arrange
        coEvery { api.searchUsers(query, resultsPerPage) } returns fakeResponse

        // Act
        val result = repository.searchUsers(query)

        // Assert
        assertTrue(result.isSuccess)
        assertEquals(
            listOf(
                SearchResult.User(
                    id = 1,
                    displayName = "testdev",
                )
            ),
            result.getOrNull()
        )
    }

    @Test
    fun `returns SearchErrorNoConnection when api throws IOException`() = runTest {
        coEvery {
            api.searchUsers(query, resultsPerPage)
        } throws IOException("no internet")

        val result = repository.searchUsers(query)

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
            api.searchUsers(query, resultsPerPage)
        } throws httpException

        val result = repository.searchUsers(query)

        assertTrue(result.isFailure)
        assertEquals(
            SearchError.RateLimited,
            (result.exceptionOrNull() as SearchException).error
        )
    }

    @Test
    fun `returns empty list when api returns no items`() = runTest {
        coEvery {
            api.searchUsers(query, resultsPerPage)
        } returns GitHubUsersResponseDto()

        val result = repository.searchUsers(query)

        assertTrue(result.isSuccess)
        assertEquals(emptyList<SearchResult.User>(), result.getOrNull())
    }
}