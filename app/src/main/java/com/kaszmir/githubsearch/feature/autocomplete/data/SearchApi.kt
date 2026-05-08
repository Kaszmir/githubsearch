package com.kaszmir.githubsearch.feature.autocomplete.data

import com.kaszmir.githubsearch.feature.autocomplete.data.model.GitHubRepositoriesResponseDto
import com.kaszmir.githubsearch.feature.autocomplete.data.model.GitHubUsersResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("per_page") resultPerPage: Int
    ): GitHubUsersResponseDto

    @GET("search/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("per_page") resultPerPage: Int
    ): GitHubRepositoriesResponseDto
}
