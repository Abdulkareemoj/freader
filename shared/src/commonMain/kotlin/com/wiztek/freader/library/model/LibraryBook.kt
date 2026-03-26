package com.wiztek.freader.library.model

import com.wiztek.freader.reader.model.BookFormat
import kotlin.time.Clock

data class LibraryBook(
    val id: String,
    val title: String,
    val author: String?,
    val format: BookFormat,
    val filePath: String,
    val coverPath: String?,
    val seriesName: String? = null,
    val volumeNumber: Int? = null,
    val progress: Double = 0.0,
    val addedAt: Long = System.currentTimeMillis()
)
