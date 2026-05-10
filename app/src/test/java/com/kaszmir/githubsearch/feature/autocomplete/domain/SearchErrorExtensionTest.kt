package com.kaszmir.githubsearch.feature.autocomplete.domain

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchError
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchException
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.asSearchError
import junit.framework.TestCase.assertEquals
import org.junit.Test

class SearchErrorExtensionTest {
    @Test
    fun `unwraps SearchException`() = assertEquals(
        SearchError.RateLimited,
        SearchException(SearchError.RateLimited).asSearchError(),
    )
    @Test fun `wraps unknown throwable as Unknown with cause`() {
        val raw = IllegalStateException("boom")
        val error = raw.asSearchError() as SearchError.Unknown
        assertEquals(raw, error.cause)
    }
}