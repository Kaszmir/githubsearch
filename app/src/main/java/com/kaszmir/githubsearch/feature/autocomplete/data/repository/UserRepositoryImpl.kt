package com.kaszmir.githubsearch.feature.autocomplete.data.repository

import com.kaszmir.githubsearch.core.di.IoDispatcher
import com.kaszmir.githubsearch.feature.autocomplete.data.SearchApi
import com.kaszmir.githubsearch.feature.autocomplete.data.model.toDomain
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchQuery
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import com.kaszmir.githubsearch.feature.autocomplete.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: SearchApi,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
): UserRepository {
    override suspend fun searchUsers(query: SearchQuery): Result<List<SearchResult.User>> {
        return withContext(dispatcher) {
            try {
                Result.success(
                    api.searchUsers(
                        query = query.query,
                        resultPerPage = query.resultsPerPage
                    ).toDomain()
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}