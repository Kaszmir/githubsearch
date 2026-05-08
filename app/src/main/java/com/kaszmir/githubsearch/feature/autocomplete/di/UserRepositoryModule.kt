package com.kaszmir.githubsearch.feature.autocomplete.di

import com.kaszmir.githubsearch.feature.autocomplete.data.repository.UserRepositoryImpl
import com.kaszmir.githubsearch.feature.autocomplete.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UserRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}