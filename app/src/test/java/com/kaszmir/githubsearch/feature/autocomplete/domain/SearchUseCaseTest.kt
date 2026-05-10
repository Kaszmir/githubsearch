package com.kaszmir.githubsearch.feature.autocomplete.domain

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchError
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchException
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.asSearchError
import com.kaszmir.githubsearch.feature.autocomplete.domain.repository.RepoRepository
import com.kaszmir.githubsearch.feature.autocomplete.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SearchUseCaseTest {

    private val userRepository: UserRepository = mockk()
    private val repoRepository: RepoRepository = mockk()
    private val useCase = SearchUseCase(userRepository, repoRepository)

    private val query = "test"
    private val fakeUsers = listOf(
        SearchResult.User(
            id = 1,
            displayName = "badTestUser",
            redirectUrl = "http://gototheweb.com"
        )
    )
    private val fakeRepositories = listOf(
        SearchResult.Repository(
            id = 2,
            displayName = "awesomeTestRepo",
            starsCount = "1000",
            redirectUrl = "http://gototheweb.com"
        )
    )

    @Test
    fun `returns sorted combined results when both requests succeed`() = runTest {
        coEvery { userRepository.searchUsers(query) } returns Result.success(fakeUsers)
        coEvery { repoRepository.searchRepositories(query) } returns Result.success(fakeRepositories)

        val result = useCase(query)

        assertTrue(result.isSuccess)
        assertEquals(
            listOf("awesomeTestRepo", "badTestUser"),
            result.getOrNull()?.map { it.displayName }
        )
    }

    @Test
    fun `sorts results case-insensitively across users and repos`() = runTest {
        val users = listOf(
            SearchResult.User(id = 1, displayName = "cherry", redirectUrl = "http://gototheweb.com"),
            SearchResult.User(id = 2, displayName = "Banana", redirectUrl = "http://gototheweb.com"),
        )
        val repos = listOf(
            SearchResult.Repository(id = 10, displayName = "apple", starsCount = "1", redirectUrl = "http://gototheweb.com"),
            SearchResult.Repository(id = 11, displayName = "Zoo",   starsCount = "1", redirectUrl = "http://gototheweb.com"),
        )
        coEvery { userRepository.searchUsers(query) } returns Result.success(users)
        coEvery { repoRepository.searchRepositories(query) } returns Result.success(repos)

        val result = useCase(query)

        assertEquals(
            listOf("apple", "Banana", "cherry", "Zoo"),
            result.getOrNull()?.map { it.displayName },
        )
    }

    @Test
    fun `returns only repos when users request fails`() = runTest {
        coEvery { userRepository.searchUsers(query) } returns Result.failure(SearchException(SearchError.NoConnection))
        coEvery { repoRepository.searchRepositories(query) } returns Result.success(fakeRepositories)

        val result = useCase(query)

        assertTrue(result.isSuccess)
        assertEquals(fakeRepositories, result.getOrNull())
    }

    @Test
    fun `returns only users when repos request fails`() = runTest {
        coEvery { userRepository.searchUsers(query) } returns Result.success(fakeUsers)
        coEvery { repoRepository.searchRepositories(query) } returns Result.failure(SearchException(SearchError.NoConnection))

        val result = useCase(query)

        assertTrue(result.isSuccess)
        assertEquals(fakeUsers, result.getOrNull())
    }

    @Test
    fun `returns failure when both requests fail`() = runTest {
        coEvery { userRepository.searchUsers(query) } returns
                Result.failure(SearchException(SearchError.NoConnection))
        coEvery { repoRepository.searchRepositories(query) } returns
                Result.failure(SearchException(SearchError.RateLimited))

        val result = useCase(query)

        assertTrue(result.isFailure)
        assertEquals(SearchError.NoConnection, result.exceptionOrNull()?.asSearchError())
    }

}