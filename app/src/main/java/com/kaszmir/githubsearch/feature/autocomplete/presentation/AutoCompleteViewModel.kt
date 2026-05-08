package com.kaszmir.githubsearch.feature.autocomplete.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaszmir.githubsearch.feature.autocomplete.domain.SearchUseCase
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchQuery
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class AutoCompleteViewModel @Inject constructor(
    private val useCase: SearchUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(AutoCompleteState())
    val uiState = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")

    init {
        observeQuery()
    }

    fun onAction(action: AutoCompleteAction) {
        when(action) {
            is AutoCompleteAction.QueryChanged -> {
                _queryFlow.value = action.query
                _uiState.update { it.copy(query = action.query, errorMessage = null) }
            }
            is AutoCompleteAction.OnClear -> {
                _uiState.update { it.copy(query = "", searchResults = null, errorMessage = null) }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeQuery() {
        _queryFlow
            .debounce(300)
            .filter { it.length >= 3 }
            .flatMapLatest { query ->
                flow<Unit> {
                    _uiState.update { it.copy(isLoading = true) }
                    useCase(SearchQuery(query = query))
                        .onSuccess { result ->
                            _uiState.update { it.copy(isLoading = false, searchResults = result) }
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    searchResults = null,
                                    errorMessage = error.message
                                )
                            }
                        }
                }
            }.launchIn(viewModelScope)
    }
}