package com.kaszmir.githubsearch.feature.autocomplete.domain.model

data class SearchQuery(
    val query: String,
    val resultsPerPage: Int = 25
)
