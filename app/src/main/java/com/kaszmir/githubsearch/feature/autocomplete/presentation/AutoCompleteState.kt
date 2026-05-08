package com.kaszmir.githubsearch.feature.autocomplete.presentation

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult

data class AutoCompleteState(
    val query: String = "",
    val searchResults: List<SearchResult>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)