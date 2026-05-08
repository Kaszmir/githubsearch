package com.kaszmir.githubsearch.feature.autocomplete.domain

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchQuery
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import com.kaszmir.githubsearch.feature.autocomplete.domain.repository.RepositoryRepository
import com.kaszmir.githubsearch.feature.autocomplete.domain.repository.UserRepository
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SearchUseCaseTest {

    private val userRepository: UserRepository = mockk()
    private val repoRepository: RepositoryRepository = mockk()
    private val useCase = SearchUseCase(userRepository, repoRepository)

    private val query = SearchQuery(query = "test")
    private val fakeUsers = listOf(
        SearchResult.User(id = 1, displayName = "badTestUser", pictureUrl = "")
    )
    private val fakeRepositories = listOf(
        SearchResult.Repository(id = 2, displayName = "awesomeTestRepo", starsCount = "1000")
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
    fun `returns only repos when users request fails`() = runTest {
        coEvery { userRepository.searchUsers(query) } returns Result.failure(Exception("error"))
        coEvery { repoRepository.searchRepositories(query) } returns Result.success(fakeRepositories)

        val result = useCase(query)

        assertTrue(result.isSuccess)
        assertEquals(fakeRepositories, result.getOrNull())
    }

    @Test
    fun `returns only users when repos request fails`() = runTest {
        coEvery { userRepository.searchUsers(query) } returns Result.success(fakeUsers)
        coEvery { repoRepository.searchRepositories(query) } returns Result.failure(Exception("error"))

        val result = useCase(query)

        assertTrue(result.isSuccess)
        assertEquals(fakeUsers, result.getOrNull())
    }

    @Test
    fun `returns failure when both requests fail`() = runTest {
        coEvery { userRepository.searchUsers(query) } returns Result.failure(Exception("users error"))
        coEvery { repoRepository.searchRepositories(query) } returns Result.failure(Exception("repos error"))

        val result = useCase(query)

        assertTrue(result.isFailure)
        assertEquals("users error", result.exceptionOrNull()?.message)
    }

}