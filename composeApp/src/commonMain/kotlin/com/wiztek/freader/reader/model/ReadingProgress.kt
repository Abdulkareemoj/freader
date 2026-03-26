package com.wiztek.freader.reader.model

data class ReadingProgress(

    val bookId: String,

    val currentPage: Int,

    val totalPages: Int,

    val lastOpened: Long = System.currentTimeMillis()

) {

    val progressPercent: Float
        get() = currentPage.toFloat() / totalPages.toFloat()

}