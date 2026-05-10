package com.kaszmir.githubsearch.feature.autocomplete.presentation

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult

sealed interface AutoCompleteAction {
    data class QueryChanged(val query: String): AutoCompleteAction
    data object OnClear: AutoCompleteAction
    data class ResultClicked(val searchResult: SearchResult): AutoCompleteAction
}