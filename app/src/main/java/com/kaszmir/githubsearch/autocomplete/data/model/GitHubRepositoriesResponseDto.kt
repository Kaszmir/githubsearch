package com.kaszmir.githubsearch.autocomplete.data.model

import com.kaszmir.githubsearch.autocomplete.domain.model.SearchResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepositoriesResponseDto(
    @SerialName("total_count") val totalCount: Int = 0,
    @SerialName("incomplete_results") val incompleteResults: Boolean = false,
    val items: List<GitHubRepositoryDto> = emptyList()
)
fun GitHubRepositoriesResponseDto.toDomain(): List<SearchResult.Repository> =
    items.map { it.toDomain() }
