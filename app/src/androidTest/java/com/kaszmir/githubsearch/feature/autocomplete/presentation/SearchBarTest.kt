package com.kaszmir.githubsearch.feature.autocomplete.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsPlaceholderWhenQueryIsEmpty() {
        composeTestRule.setContent {
            MaterialTheme {
                SearchBar(query = "", onQueryChange = {}, onClear = {})
            }
        }

        composeTestRule.onNodeWithText("Search").assertIsDisplayed()
    }

    @Test
    fun hidesPlaceholderWhenQueryIsNotEmpty() {
        composeTestRule.setContent {
            MaterialTheme {
                SearchBar(query = "kotlin", onQueryChange = {}, onClear = {})
            }
        }

        composeTestRule.onNodeWithText("Search").assertDoesNotExist()
    }

    @Test
    fun showsTypedQuery() {
        composeTestRule.setContent {
            MaterialTheme {
                SearchBar(query = "kotlin", onQueryChange = {}, onClear = {})
            }
        }

        composeTestRule.onNodeWithText("kotlin").assertIsDisplayed()
    }

    @Test
    fun clearButtonIsNotClickableWhenQueryIsEmpty() {
        composeTestRule.setContent {
            MaterialTheme {
                SearchBar(query = "", onQueryChange = {}, onClear = {})
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear query").assertHasNoClickAction()

    }

    @Test
    fun clearButtonTriggersCallbackWhenClicked() {
        var cleared = false
        composeTestRule.setContent {
            MaterialTheme {
                SearchBar(query = "kotlin", onQueryChange = {}, onClear = { cleared = true })
            }
        }

        composeTestRule.onNodeWithContentDescription("Clear query").performClick()
        assertTrue(cleared)
    }

    @Test
    fun typingTriggersOnQueryChangeCallback() {
        var changedQuery = ""
        composeTestRule.setContent {
            MaterialTheme {
                SearchBar(query = "", onQueryChange = { changedQuery = it }, onClear = {})
            }
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("kotlin")

        assertEquals("kotlin", changedQuery)
    }
}