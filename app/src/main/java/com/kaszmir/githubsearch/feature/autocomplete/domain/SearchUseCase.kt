package com.kaszmir.githubsearch.feature.autocomplete.domain

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchError
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchException
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import com.kaszmir.githubsearch.feature.autocomplete.domain.repository.RepoRepository
import com.kaszmir.githubsearch.feature.autocomplete.domain.repository.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val repoRepository: RepoRepository,
) {
    suspend operator fun invoke(query: String): Result<List<SearchResult>> = coroutineScope {
        val users = async { userRepository.searchUsers(query) }
        val repos = async { repoRepository.searchRepositories(query) }

        val userResults = users.await()
        val repoResults = repos.await()

        if(userResults.isFailure && repoResults.isFailure) {
            val cause = userResults.exceptionOrNull() ?: repoResults.exceptionOrNull()
            Result.failure(cause ?: SearchException(SearchError.Unknown()))
        } else {
            val userList = userResults.getOrElse { emptyList() }
            val repoList = repoResults.getOrElse { emptyList() }

            Result.success((userList + repoList)
                .sortedWith(
                    compareBy(
                        String.CASE_INSENSITIVE_ORDER
                    ) { it.displayName }
                )
            )
        }
    }
}