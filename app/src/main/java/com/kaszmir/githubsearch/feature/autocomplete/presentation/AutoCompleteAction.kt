package com.kaszmir.githubsearch.feature.autocomplete.presentation

sealed interface AutoCompleteAction {
    data class QueryChanged(val query: String): AutoCompleteAction
    data object OnClear: AutoCompleteAction
}