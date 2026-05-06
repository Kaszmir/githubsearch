package com.kaszmir.githubsearch.autocomplete.data

import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApi {
    @GET("search/users")
    suspend fun searchUsers(
        @Query("q") query: String,
        @Query("per_page") resultPerPage: Int
    ): GitHubUsersResponseDto

    @GET("serach/repositories")
    suspend fun searchRepositories(
        @Query("q") query: String,
        @Query("per_page") resultPerPage: Int
    ): GitHubRepositoriesResponseDto
}
