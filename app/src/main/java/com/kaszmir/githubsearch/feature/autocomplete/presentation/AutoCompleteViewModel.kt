package com.kaszmir.githubsearch.feature.autocomplete.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kaszmir.githubsearch.feature.autocomplete.domain.SearchUseCase
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.asSearchError
import com.kaszmir.githubsearch.feature.autocomplete.presentation.AutoCompleteState.Companion.DEBOUNCE_VALUE
import com.kaszmir.githubsearch.feature.autocomplete.presentation.AutoCompleteState.Companion.MIN_QUERY_LENGTH
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class AutoCompleteViewModel @Inject constructor(
    private val useCase: SearchUseCase
): ViewModel() {

    private var searchJob: Job? = null

    private val _uiState = MutableStateFlow(AutoCompleteState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<AutoCompleteUiEffect>()
    val effects = _effects.asSharedFlow()

    private val _queryFlow = MutableStateFlow("")

    init {
        observeQuery()
    }

    fun onAction(action: AutoCompleteAction) {
        when (action) {
            is AutoCompleteAction.QueryChanged -> onQueryChanged(action.query)
            is AutoCompleteAction.OnClear -> resetSearch("")
            is AutoCompleteAction.ResultClicked -> handleResultClick(action.searchResult)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeQuery() {
        _queryFlow
            .debounce(DEBOUNCE_VALUE)
            .filter { it.length >= MIN_QUERY_LENGTH }
            .onEach { query ->
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true) }
                    useCase(query = query)
                        .onSuccess { result ->
                            _uiState.update { it.copy(isLoading = false, searchResults = result) }
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    searchResults = null,
                                    errorMessage = error.asSearchError().toMessage(),
                                )
                            }
                        }
                }
            }.launchIn(viewModelScope)
    }

    private fun onQueryChanged(query: String) {
        if (query.length < MIN_QUERY_LENGTH) {
            resetSearch(query)
        } else {
            _queryFlow.value = query
            _uiState.update { it.copy(query = query, errorMessage = null) }
        }
    }

    private fun resetSearch(query: String) {
        searchJob?.cancel()
        _queryFlow.value = query
        _uiState.update {
            it.copy(
                query = query,
                searchResults = null,
                errorMessage = null,
                isLoading = false
            )
        }
    }

    private fun handleResultClick(searchResult: SearchResult) {
        viewModelScope.launch {
            _effects.emit(
                AutoCompleteUiEffect.OpenUrl(
                    url = searchResult.redirectUrl
                )
            )
        }
    }
}
