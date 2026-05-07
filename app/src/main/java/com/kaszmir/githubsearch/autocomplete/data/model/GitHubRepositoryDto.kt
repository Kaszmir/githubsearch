package com.kaszmir.githubsearch.autocomplete.data.model

import com.kaszmir.githubsearch.autocomplete.domain.model.SearchResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubRepositoryDto(
    val id: Long = 0,
    @SerialName("node_id") val nodeId: String = "",
    val name: String = "",
    @SerialName("full_name") val fullName: String = "",
    val owner: GitHubUserDto? = null,
    val private: Boolean = false,
    @SerialName("html_url") val htmlUrl: String = "",
    val description: String? = null,
    val fork: Boolean = false,
    @SerialName("created_at") val createdAt: String = "",
    @SerialName("updated_at") val updatedAt: String = "",
    @SerialName("pushed_at") val pushedAt: String = "",
    val homepage: String? = null,
    val size: Int = 0,
    @SerialName("stargazers_count") val stargazersCount: Int = 0,
    @SerialName("watchers_count") val watchersCount: Int = 0,
    val language: String? = null,
    @SerialName("forks_count") val forksCount: Int = 0,
    @SerialName("open_issues_count") val openIssuesCount: Int = 0,
    @SerialName("default_branch") val defaultBranch: String = "",
    val score: Double = 0.0,
    val archived: Boolean = false,
    val disabled: Boolean = false,
    val visibility: String = "",
    val license: GitHubLicenseDto? = null
)
fun GitHubRepositoryDto.toDomain() =
    SearchResult.Repository(id = id, displayName = name, score = "$score")