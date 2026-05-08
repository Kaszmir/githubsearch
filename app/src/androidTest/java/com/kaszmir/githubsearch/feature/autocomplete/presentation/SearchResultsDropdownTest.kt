package com.kaszmir.githubsearch.feature.autocomplete.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
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
            SearchResult.User(id = 1, displayName = "kotlindev", pictureUrl = "")
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

        composeTestRule.onNodeWithText("kotlindev").assertIsDisplayed()
    }

    @Test
    fun showsRepoItemsWhenSearchResultsContainsRepositories() {
        val results = listOf(
            SearchResult.Repository(id = 1, displayName = "awesome-kotlin", starsCount = "100")
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

        composeTestRule.onNodeWithText("awesome-kotlin").assertIsDisplayed()
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
}