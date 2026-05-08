package com.kaszmir.githubsearch.feature.autocomplete.data.model

import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubUserDto(
    val id: Long = 0,
    @SerialName("node_id") val nodeId: String = "",
    val login: String = "",
    @SerialName("avatar_url") val avatarUrl: String = "",
    @SerialName("html_url") val htmlUrl: String = "",
    val type: String = "",
    val score: Double = 0.0,
    @SerialName("site_admin") val siteAdmin: Boolean = false
)
fun GitHubUserDto.toDomain() = SearchResult.User(
    id = id,
    displayName = login,
)