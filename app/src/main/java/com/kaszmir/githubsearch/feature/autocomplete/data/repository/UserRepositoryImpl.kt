package com.kaszmir.githubsearch.feature.autocomplete.data.repository

import com.kaszmir.githubsearch.core.di.IoDispatcher
import com.kaszmir.githubsearch.feature.autocomplete.data.SearchApi
import com.kaszmir.githubsearch.feature.autocomplete.data.model.toDomain
import com.kaszmir.githubsearch.feature.autocomplete.data.resultsPerPage
import com.kaszmir.githubsearch.feature.autocomplete.data.toSearchException
import com.kaszmir.githubsearch.feature.autocomplete.domain.model.SearchResult
import com.kaszmir.githubsearch.feature.autocomplete.domain.repository.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class UserRepositoryImpl @Inject constructor(
    private val api: SearchApi,
    @param:IoDispatcher private val dispatcher: CoroutineDispatcher
): UserRepository {
    override suspend fun searchUsers(query: String): Result<List<SearchResult.User>> {
        return withContext(dispatcher) {
            try {
                Result.success(
                    api.searchUsers(
                        query = query,
                        resultPerPage = resultsPerPage
                    ).toDomain()
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e.toSearchException())
            }
        }
    }
}