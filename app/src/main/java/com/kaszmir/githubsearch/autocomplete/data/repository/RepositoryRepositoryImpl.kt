package com.kaszmir.githubsearch.autocomplete.data.repository

import com.kaszmir.githubsearch.autocomplete.data.SearchApi
import com.kaszmir.githubsearch.autocomplete.data.model.toDomain
import com.kaszmir.githubsearch.autocomplete.domain.model.SearchQuery
import com.kaszmir.githubsearch.autocomplete.domain.model.SearchResult
import com.kaszmir.githubsearch.autocomplete.domain.repository.RepositoryRepository
import com.kaszmir.githubsearch.core.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryRepositoryImpl @Inject constructor(
    private val api: SearchApi,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
): RepositoryRepository {
    override suspend fun searchRepositories(query: SearchQuery): Result<List<SearchResult.Repository>> {
        return withContext(dispatcher) {
            try {
                Result.success(
                    api.searchRepositories(
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
