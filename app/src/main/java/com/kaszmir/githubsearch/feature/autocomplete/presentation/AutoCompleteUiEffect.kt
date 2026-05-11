package com.kaszmir.githubsearch.feature.autocomplete.presentation

sealed interface AutoCompleteUiEffect {
    data object OpenUrlFailed: AutoCompleteUiEffect
}