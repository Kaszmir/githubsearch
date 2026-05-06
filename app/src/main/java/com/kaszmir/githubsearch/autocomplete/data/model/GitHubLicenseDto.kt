package com.kaszmir.githubsearch.autocomplete.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubLicenseDto(
    val key: String = "",
    val name: String = "",
    val url: String? = null,
    @SerialName("spdx_id") val spdxId: String = "",
    @SerialName("node_id") val nodeId: String = ""
)