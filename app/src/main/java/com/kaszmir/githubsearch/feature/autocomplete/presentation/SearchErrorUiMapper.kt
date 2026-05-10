package com.kaszmir.githubsearch.feature.autocomplete.presentation

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchError

internal fun SearchError.toMessage(): String = when (this) {
    SearchError.BadCredentials -> "Session expired"
    SearchError.NoConnection -> "No internet connection"
    SearchError.RateLimited  -> "GitHub API rate limit exceeded"
    SearchError.ServerError  -> "Server error, try again"
    is SearchError.Unknown   -> "Something went wrong"
}