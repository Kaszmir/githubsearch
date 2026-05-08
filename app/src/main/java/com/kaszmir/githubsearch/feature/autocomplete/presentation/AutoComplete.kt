package com.kaszmir.githubsearch.feature.autocomplete.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AutoCompleteWidget(
    viewModel: AutoCompleteViewModel = hiltViewModel(),
    modifier: Modifier
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    AutoCompleteLayout(
        uiState = state.value,
        queryTextChanged = { viewModel.onAction(AutoCompleteAction.QueryChanged(it)) },
        clearQueryClicked = { viewModel.onAction(AutoCompleteAction.OnClear) },
        modifier = modifier
    )
}

@Composable
private fun AutoCompleteLayout(
    uiState: AutoCompleteState,
    queryTextChanged: (String) -> Unit,
    clearQueryClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp)
    ) {
        SearchBar(
            query = uiState.query,
            onQueryChange = queryTextChanged,
            onClear = clearQueryClicked
        )

        val showResults = uiState.query.isNotEmpty()

        AnimatedVisibility(
            visible = showResults,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SearchResultsDropdown(
                loading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                searchResults = uiState.searchResults,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}