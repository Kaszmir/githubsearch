package com.kaszmir.githubsearch.feature.autocomplete.domain.repository

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchQuery
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult


interface RepositoryRepository {
    suspend fun searchRepositories(query: SearchQuery): Result<List<SearchResult.Repository>>
}