package com.kaszmir.githubsearch.feature.autocomplete.domain.repository

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult

interface UserRepository {
    suspend fun searchUsers(query: String): Result<List<SearchResult.User>>
}