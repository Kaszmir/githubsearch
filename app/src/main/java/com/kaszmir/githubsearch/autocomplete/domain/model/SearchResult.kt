package com.kaszmir.githubsearch.autocomplete.domain.model

sealed class SearchResult {
    abstract val displayName: String

    data class User(
        val id: Long,
        override val displayName: String,
        val pictureUrl: String
    ): SearchResult()

    data class Repository(
        val id: Long,
        override val displayName: String,
        val starsCount: String
    ): SearchResult()
}
