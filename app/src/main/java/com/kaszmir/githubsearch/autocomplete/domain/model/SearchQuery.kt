package com.kaszmir.githubsearch.autocomplete.domain.model

data class SearchQuery(
    val query: String,
    val resultsPerPage: Int = 25
)
