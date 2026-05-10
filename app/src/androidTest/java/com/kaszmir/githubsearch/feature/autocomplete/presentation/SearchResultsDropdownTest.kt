package com.kaszmir.githubsearch.feature.autocomplete.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchResultsDropdownTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsLoadingItemsWhenLoadingIsTrue() {
        composeTestRule.setContent {
            MaterialTheme {
                SearchResultsDropdown(
                    loading = true,
                    errorMessage = null,
                    searchResults = null
                )
            }
        }

        // at least one loading item
        composeTestRule.onAllNodesWithTag("loading_item").onFirst().assertExists()
    }

    @Test
    fun showsErrorMessageWhenErrorMessageIsNotNull() {
        composeTestRule.setContent {
            MaterialTheme {
                SearchResultsDropdown(
                    loading = false,
                    errorMessage = "network error",
                    searchResults = null
                )
            }
        }

        composeTestRule.onNodeWithText("network error").assertIsDisplayed()
    }

    @Test
    fun showsEmptyStateWhenSearchResultsIsEmptyList() {
        composeTestRule.setContent {
            MaterialTheme {
                SearchResultsDropdown(
                    loading = false,
                    errorMessage = null,
                    searchResults = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithText("No Results").assertIsDisplayed()
    }

    @Test
    fun showsUserItemsWhenSearchResultsContainsUsers() {
        val results = listOf(
            SearchResult.User(id = 1, displayName = "testdev", redirectUrl = "http://gototheweb.com")
        )
        composeTestRule.setContent {
            MaterialTheme {
                SearchResultsDropdown(
                    loading = false,
                    errorMessage = null,
                    searchResults = results
                )
            }
        }

        composeTestRule.onNodeWithText("testdev").assertIsDisplayed()
    }

    @Test
    fun showsRepoItemsWhenSearchResultsContainsRepositories() {
        val results = listOf(
            SearchResult.Repository(id = 1, displayName = "test-repository", starsCount = "100", redirectUrl = "http://gototheweb.com")
        )
        composeTestRule.setContent {
            MaterialTheme {
                SearchResultsDropdown(
                    loading = false,
                    errorMessage = null,
                    searchResults = results
                )
            }
        }

        composeTestRule.onNodeWithText("test-repository").assertIsDisplayed()
        composeTestRule.onNodeWithText("100").assertIsDisplayed()
    }

    @Test
    fun errorTakesPriorityOverLoading() {
        composeTestRule.setContent {
            MaterialTheme {
                SearchResultsDropdown(
                    loading = true,
                    errorMessage = "network error",
                    searchResults = null
                )
            }
        }

        composeTestRule.onNodeWithText("network error").assertIsDisplayed()
        composeTestRule.onAllNodesWithTag("loading_item").fetchSemanticsNodes().isEmpty()
    }

    @Test
    fun forwardsUserClickWithFullSearchResult() {
        val user = SearchResult.User(
            id = 1,
            displayName = "octocat",
            redirectUrl = "https://github.com/octocat",
        )
        var clicked: SearchResult? = null

        composeTestRule.setContent {
            MaterialTheme {
                SearchResultsDropdown(
                    loading = false,
                    errorMessage = null,
                    searchResults = listOf(user),
                    onResultClicked = { clicked = it },
                )
            }
        }

        composeTestRule.onNodeWithText("octocat").performClick()
        assertEquals(user, clicked)
    }

    @Test
    fun forwardsRepoClickWithFullSearchResult() {
        val repo = SearchResult.Repository(
            id = 2,
            displayName = "android",
            starsCount = "100",
            redirectUrl = "https://github.com/android/android",
        )
        var clicked: SearchResult? = null

        composeTestRule.setContent {
            MaterialTheme {
                SearchResultsDropdown(
                    loading = false,
                    errorMessage = null,
                    searchResults = listOf(repo),
                    onResultClicked = { clicked = it },
                )
            }
        }

        composeTestRule.onNodeWithText("android").performClick()
        assertEquals(repo, clicked)
    }
}