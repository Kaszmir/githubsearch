package com.kaszmir.githubsearch.autocomplete.presentation

import androidx.lifecycle.ViewModel
import com.kaszmir.githubsearch.autocomplete.domain.SearchUseCase
import com.kaszmir.githubsearch.autocomplete.domain.model.SearchResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class AutoCompleteState(
    val query: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface AutoCompleteAction {
    data class QueryChanged(val query: String): AutoCompleteAction
    data object OnClear: AutoCompleteAction
}

@HiltViewModel
class AutoCompleteViewModel @Inject constructor(
    private val useCase: SearchUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(AutoCompleteState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: AutoCompleteAction) {
        when(action) {
            is AutoCompleteAction.QueryChanged -> {
                _uiState.update { it.copy(query = action.query) }
            }
            is AutoCompleteAction.OnClear -> {
                _uiState.update { it.copy(query = "") }
            }
        }
    }
}