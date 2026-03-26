package com.wiztek.freader.data.preview
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.model.BookFormat

val sampleBooks = listOf(
    LibraryBook(
        id = "1",
        title = "The Hobbit",
        author = "J.R.R. Tolkien",
        format = BookFormat.EPUB,
        filePath = "",
        coverPath = null
    ),
    LibraryBook(
        id = "2",
        title = "Clean Code",
        author = "Robert C. Martin",
        format = BookFormat.PDF,
        filePath = "",
        coverPath = null
    ),
    LibraryBook(
        id = "3",
        title = "Design Patterns",
        author = "Erich Gamma",
        format = BookFormat.PDF,
        filePath = "",
        coverPath = null
    ),
    LibraryBook(
        id = "4",
        title = "Amazing Spider-Man Vol 1",
        author = "Stan Lee",
        format = BookFormat.CBZ,
        filePath = "",
        coverPath = null,
        seriesName = "Amazing Spider-Man",
        volumeNumber = 1
    ),
    LibraryBook(
        id = "6",
        title = "Amazing Spider-Man Vol 2",
        author = "Stan Lee",
        format = BookFormat.CBZ,
        filePath = "",
        coverPath = null,
        seriesName = "Amazing Spider-Man",
        volumeNumber = 2
    ),
    LibraryBook(
        id = "5",
        title = "Berserk Vol. 1",
        author = "Kentaro Miura",
        format = BookFormat.CBR,
        filePath = "",
        coverPath = null,
        seriesName = "Berserk",
        volumeNumber = 1
    )
)
