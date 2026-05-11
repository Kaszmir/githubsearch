package com.kaszmir.githubsearch.core.di

import com.kaszmir.githubsearch.core.system.UrlOpener
import com.kaszmir.githubsearch.core.system.UrlOpenerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UrlOpenerModule {
    @Binds
    abstract fun bindUrlOpener(impl: UrlOpenerImpl): UrlOpener
}