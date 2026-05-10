package com.kaszmir.githubsearch.feature.autocomplete.domain.model

sealed class SearchResult {
    abstract val displayName: String
    abstract val redirectUrl: String
    data class User(
        val id: Long,
        override val displayName: String,
        override val redirectUrl: String
    ): SearchResult()

    data class Repository(
        val id: Long,
        override val displayName: String,
        val starsCount: String,
        override val redirectUrl: String
    ): SearchResult()
}
