package com.kaszmir.githubsearch.feature.autocomplete.domain.repository

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult

interface RepoRepository {
    suspend fun searchRepositories(query: String): Result<List<SearchResult.Repository>>
}