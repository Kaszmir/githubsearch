package com.kaszmir.githubsearch.feature.autocomplete.presentation

import android.widget.Toast
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effects.collect { effect ->
                when(effect) {
                    is AutoCompleteUiEffect.OpenUrlFailed -> {
                        Toast.makeText(
                            context, "Can't open the URL", Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    AutoCompleteLayout(
        uiState = state,
        modifier = modifier,
        onAction = { viewModel.onAction(it) },
    )
}

@Composable
private fun AutoCompleteLayout(
    uiState: AutoCompleteState,
    onAction: (AutoCompleteAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier
        .fillMaxSize()
        .padding(horizontal = 12.dp)
    ) {
        SearchBar(
            query = uiState.query,
            onQueryChange = { onAction(AutoCompleteAction.QueryChanged(it)) },
            onClear = { onAction(AutoCompleteAction.OnClear) }
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
                onResultClicked = { onAction(AutoCompleteAction.ResultClicked(it)) }
            )
        }
    }
}

@Preview
@Composable
private fun AutoCompleteLayoutIdlePreview() {
    AutoCompleteLayout(
        uiState = AutoCompleteState(),
        onAction = {}
    )
}

@Preview
@Composable
private fun AutoCompleteLayoutLoadingPreview() {
    AutoCompleteLayout(
        uiState = AutoCompleteState(query = "test", isLoading = true),
        onAction = {}
    )
}

@Preview
@Composable
private fun AutoCompleteLayoutEmptyPreview() {
    AutoCompleteLayout(
        uiState = AutoCompleteState(query = "test", isLoading = false, searchResults = emptyList()),
        onAction = {}
    )
}

@Preview
@Composable
private fun AutoCompleteLayoutWithResultsPreview() {
    AutoCompleteLayout(
        uiState = AutoCompleteState(query = "test", isLoading = false, searchResults = searchResultList),
        onAction = {}
    )
}

@Preview
@Composable
private fun AutoCompleteLayoutErrorPreview() {
    AutoCompleteLayout(
        uiState = AutoCompleteState(query = "test", isLoading = false, searchResults = emptyList(), errorMessage = "No internet connection"),
        onAction = {}
    )
}

private val searchResultList = listOf(
    SearchResult.User(1, "test_user", "noone"),
    SearchResult.Repository(2, "test_repo", "1000", ""),
)