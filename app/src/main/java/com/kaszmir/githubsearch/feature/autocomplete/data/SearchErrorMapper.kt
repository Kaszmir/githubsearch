package com.kaszmir.githubsearch.feature.autocomplete.data

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchError
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchException
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal fun Throwable.toSearchException(): SearchException = SearchException(
    when (this) {
        is SearchException -> return this
        is UnknownHostException, is SocketTimeoutException, is IOException ->
            SearchError.NoConnection
        is HttpException -> when (code()) {
            403, 429 -> SearchError.RateLimited
            in 500..599 -> SearchError.ServerError
            else -> SearchError.Unknown(this)
        }
        else -> SearchError.Unknown(this)
    }
)