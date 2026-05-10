package com.kaszmir.githubsearch.feature.autocomplete.domain.model

sealed interface SearchError {
    data object NoConnection : SearchError
    data object RateLimited : SearchError
    data object ServerError : SearchError
    data class Unknown(val cause: Throwable? = null) : SearchError
}

class SearchException(val error: SearchError) : Throwable(
    cause = (error as? SearchError.Unknown)?.cause
)

fun Throwable.asSearchError(): SearchError =
    (this as? SearchException)?.error ?: SearchError.Unknown(this)