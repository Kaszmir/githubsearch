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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult

@Composable
fun AutoCompleteWidget(
    modifier: Modifier = Modifier,
    viewModel: AutoCompleteViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when(effect) {
                is AutoCompleteUiEffect.OpenUrl -> uriHandler.openUri(effect.url)
            }
        }
    }

    AutoCompleteLayout(
        modifier = modifier,
        uiState = state.value,
        queryTextChanged = { viewModel.onAction(AutoCompleteAction.QueryChanged(it)) },
        clearQueryClicked = { viewModel.onAction(AutoCompleteAction.OnClear) },
        onResultClicked = { viewModel.onAction(AutoCompleteAction.ResultClicked(it)) }
    )
}

@Composable
private fun AutoCompleteLayout(
    modifier: Modifier = Modifier,
    uiState: AutoCompleteState,
    queryTextChanged: (String) -> Unit,
    clearQueryClicked: () -> Unit,
    onResultClicked: (SearchResult) -> Unit
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

        AnimatedVisibility(
            visible = uiState.shouldShowDropDown,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            SearchResultsDropdown(
                loading = uiState.isLoading,
                errorMessage = uiState.errorMessage,
                searchResults = uiState.searchResults,
                modifier = Modifier.padding(bottom = 8.dp),
                onResultClicked = onResultClicked
            )
        }
    }
}