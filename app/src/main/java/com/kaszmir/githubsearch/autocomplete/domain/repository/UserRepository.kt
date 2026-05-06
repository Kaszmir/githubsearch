package com.kaszmir.githubsearch.autocomplete.domain.repository

import com.kaszmir.githubsearch.autocomplete.domain.model.SearchQuery
import com.kaszmir.githubsearch.autocomplete.domain.model.SearchResult

interface UserRepository {
    suspend fun searchUsers(query: SearchQuery): Result<List<SearchResult.User>>
}