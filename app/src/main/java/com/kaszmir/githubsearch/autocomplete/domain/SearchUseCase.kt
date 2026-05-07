package com.kaszmir.githubsearch.autocomplete.domain

import android.util.Log
import com.kaszmir.githubsearch.autocomplete.domain.model.SearchQuery
import com.kaszmir.githubsearch.autocomplete.domain.model.SearchResult
import com.kaszmir.githubsearch.autocomplete.domain.repository.RepositoryRepository
import com.kaszmir.githubsearch.autocomplete.domain.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val repoRepository: RepositoryRepository,
) {
    suspend operator fun invoke(query: SearchQuery): Result<List<SearchResult>> = coroutineScope {
        Log.d("lolTag", "invoke: ehehehhe")
        val users = async { userRepository.searchUsers(query) }
        val repos = async { repoRepository.searchRepositories(query) }

        val userResults = users.await()
        val repoResults = repos.await()

        if(userResults.isFailure && repoResults.isFailure) {
            Result.failure(userResults.exceptionOrNull() ?: Exception("Both requests failed"))
        } else {
            val userList = userResults.getOrElse { emptyList() }
            val repoList = repoResults.getOrElse { emptyList() }
            Result.success((userList + repoList).sortedBy { it.displayName })
        }
    }
}