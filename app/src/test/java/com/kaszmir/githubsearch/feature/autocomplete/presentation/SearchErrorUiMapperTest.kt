package com.kaszmir.githubsearch.feature.autocomplete.presentation

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchError
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SearchErrorUiMapperTest {
    @Test
    fun `BadCredentials has user message`() =
        assertEquals("Session expired", SearchError.BadCredentials.toMessage())
    @Test
    fun `NoConnection has user message`() =
        assertEquals("No internet connection", SearchError.NoConnection.toMessage())
    @Test fun `RateLimited has user message`() =
        assertEquals("GitHub API rate limit exceeded", SearchError.RateLimited.toMessage())
    @Test fun `ServerError has user message`() =
        assertEquals("Server error, try again", SearchError.ServerError.toMessage())
    @Test fun `Unknown has user message`() =
        assertEquals("Something went wrong", SearchError.Unknown().toMessage())
}