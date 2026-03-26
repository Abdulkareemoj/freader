package com.wiztek.freader.data.models

data class Book(

    val id: String,
    val title: String,
    val author: String,
    val filePath: String,
    val coverPath: String?,
    val progress: Float,
    val lastOpened: Long

)