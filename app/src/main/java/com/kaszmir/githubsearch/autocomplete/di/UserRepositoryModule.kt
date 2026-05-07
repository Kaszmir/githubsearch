package com.kaszmir.githubsearch.autocomplete.di

import com.kaszmir.githubsearch.autocomplete.data.repository.UserRepositoryImpl
import com.kaszmir.githubsearch.autocomplete.domain.repository.UserRepository
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