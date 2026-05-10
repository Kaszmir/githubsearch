package com.kaszmir.githubsearch.feature.autocomplete.presentation

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult

data class AutoCompleteState(
    val query: String = "",
    val searchResults: List<SearchResult>? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val shouldShowDropDown: Boolean
        get() = query.length >= MIN_QUERY_LENGTH


    companion object {
        const val MIN_QUERY_LENGTH = 3
        const val DEBOUNCE_VALUE = 300L
    }
}