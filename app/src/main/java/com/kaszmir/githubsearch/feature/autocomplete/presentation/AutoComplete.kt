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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult

@Composable
fun AutoCompleteWidget(
    modifier: Modifier = Modifier,
    viewModel: AutoCompleteViewModel = hiltViewModel(),
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel.effects, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    is AutoCompleteUiEffect.OpenUrl -> uriHandler.openUri(effect.url)
                }
            }
        }
    }

    AutoCompleteLayout(
        uiState = state.value,
        modifier = modifier,
        queryTextChanged = { viewModel.onAction(AutoCompleteAction.QueryChanged(it)) },
        clearQueryClicked = { viewModel.onAction(AutoCompleteAction.OnClear) },
        onResultClicked = { viewModel.onAction(AutoCompleteAction.ResultClicked(it)) }
    )
}

@Composable
private fun AutoCompleteLayout(
    uiState: AutoCompleteState,
    modifier: Modifier = Modifier,
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