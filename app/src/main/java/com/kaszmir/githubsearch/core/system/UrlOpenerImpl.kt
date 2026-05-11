package com.kaszmir.githubsearch.core.system

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UrlOpenerImpl @Inject constructor(
    @ApplicationContext private val context: Context
)
    : UrlOpener {
    override fun open(url: String): Boolean = runCatching {
        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }.isSuccess
}