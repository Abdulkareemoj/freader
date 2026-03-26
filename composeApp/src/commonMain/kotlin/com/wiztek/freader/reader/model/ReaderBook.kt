package com.wiztek.freader.reader.model

data class ReaderBook(

    val id: String,

    val title: String,

    val author: String? = null,

    val description: String? = null,

    val filePath: String,

    val coverPath: String? = null,

    val format: BookFormat,

    val pageCount: Int? = null,

    val addedAt: Long = System.currentTimeMillis()
)