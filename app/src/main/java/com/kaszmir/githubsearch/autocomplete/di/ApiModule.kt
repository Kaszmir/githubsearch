package com.kaszmir.githubsearch.autocomplete.di

import com.kaszmir.githubsearch.autocomplete.data.SearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideSearchApi(retrofit: Retrofit): SearchApi = retrofit.create(SearchApi::class.java)
}