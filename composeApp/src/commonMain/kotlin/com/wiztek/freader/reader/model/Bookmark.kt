package com.wiztek.freader.reader.model

data class Bookmark(

    val bookId: String,

    val pageIndex: Int,

    val createdAt: Long = System.currentTimeMillis()

)