package com.kaszmir.githubsearch.autocomplete.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.kaszmir.githubsearch.autocomplete.domain.model.SearchResult

@Composable
fun SearchResultsDropdown(
    loading: Boolean,
    errorMessage: String?,
    searchResults: List<SearchResult>,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(12.dp)
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .shadow(8.dp, shape)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                shape = shape
            )
    ) {
        if(errorMessage != null) {
            item { SearchErrorState(errorMessage) }
            return@LazyColumn
        }
        if(loading) {
            items(15) {
                SearchLoadingItem(isLoading = loading)
            }
        }else {
            items(searchResults) {
                when(it) {
                    is SearchResult.User -> SearchResultUserItem(
                        userName = it.displayName,
                        modifier = modifier
                    )
                    is SearchResult.Repository -> SearchResultRepoItem(
                        repoName = it.displayName,
                        repoScore = it.starsCount,
                        modifier = modifier
                    )
                }
            }
        }
    }
}