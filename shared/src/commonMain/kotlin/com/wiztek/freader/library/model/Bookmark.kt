package com.wiztek.freader.library.model

import kotlinx.datetime.Clock

data class Bookmark(
    val id: String,
    val bookId: String,
    val location: String, // Readium Locator as JSON
    val label: String,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)
