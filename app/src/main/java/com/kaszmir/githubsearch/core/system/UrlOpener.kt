package com.kaszmir.githubsearch.core.system

fun interface UrlOpener {
    /** Returns true if the URL was opened, false on failure. Never throws. */
    fun open(url: String): Boolean
}