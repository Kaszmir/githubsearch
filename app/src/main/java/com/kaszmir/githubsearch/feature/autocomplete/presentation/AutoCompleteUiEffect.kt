package com.kaszmir.githubsearch.feature.autocomplete.presentation

sealed interface AutoCompleteUiEffect {
    data class OpenUrl(val url: String): AutoCompleteUiEffect
}