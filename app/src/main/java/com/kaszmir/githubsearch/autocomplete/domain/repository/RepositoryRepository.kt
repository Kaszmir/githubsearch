package com.kaszmir.githubsearch.autocomplete.domain.repository

import com.kaszmir.githubsearch.autocomplete.domain.model.SearchQuery
import com.kaszmir.githubsearch.autocomplete.domain.model.SearchResult

interface RepositoryRepository {
    suspend fun searchRepositories(query: SearchQuery): Result<List<SearchResult.Repository>>
}