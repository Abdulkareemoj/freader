package com.wiztek.freader.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wiztek.freader.library.model.LibraryBook

@Composable
fun ContinueReadingSection(

    books: List<LibraryBook>,

    onBookClick: (LibraryBook) -> Unit

) {

    Column {

        SectionHeader(
            title = "Continue Reading"
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {

            items(books) { book ->

                BookCard(
                    book = book,
                    onClick = { onBookClick(book) },
                    modifier = Modifier.width(160.dp)
                )

            }

        }

    }

}