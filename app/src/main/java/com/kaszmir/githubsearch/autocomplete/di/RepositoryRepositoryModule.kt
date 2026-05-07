package com.kaszmir.githubsearch.autocomplete.di

import com.kaszmir.githubsearch.autocomplete.data.repository.RepositoryRepositoryImpl
import com.kaszmir.githubsearch.autocomplete.domain.repository.RepositoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepositoryRepository(impl: RepositoryRepositoryImpl): RepositoryRepository
}