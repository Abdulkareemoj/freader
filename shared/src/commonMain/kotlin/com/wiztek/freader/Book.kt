package com.wiztek.freader

enum class BookFormat {
    EPUB, PDF, TXT
}

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val format: BookFormat,
    val coverUrl: String? = null
)
