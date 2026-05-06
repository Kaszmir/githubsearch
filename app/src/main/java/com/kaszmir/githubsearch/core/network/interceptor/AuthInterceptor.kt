package com.kaszmir.githubsearch.core.network.interceptor

import com.kaszmir.githubsearch.BuildConfig
import jakarta.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

private const val tokenHeaderField = "x-subscription-token"

class AuthInterceptor @Inject constructor(): Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            request = chain.request().newBuilder()
                .addHeader(
                    name = tokenHeaderField,
                    value = "Bearer ${BuildConfig.apikey}"
                ).build()
        )
    }
}