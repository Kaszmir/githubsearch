package com.kaszmir.githubsearch.feature.autocomplete.di

import com.kaszmir.githubsearch.feature.autocomplete.data.repository.RepoRepositoryImpl
import com.kaszmir.githubsearch.feature.autocomplete.domain.repository.RepoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepoRepository(impl: RepoRepositoryImpl): RepoRepository
}