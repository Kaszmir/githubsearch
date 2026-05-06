package com.kaszmir.githubsearch.autocomplete.domain

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
    suspend operator fun invoke(query: SearchQuery): List<SearchResult> = coroutineScope {
        val users = async { userRepository.searchUsers(query) }
        val repos = async { repoRepository.searchRepositories(query) }

        val userResults = users.await().getOrElse { emptyList() }
        val repoResults = repos.await().getOrElse { emptyList() }

        (userResults + repoResults).sortedBy { it.displayName }
    }
}