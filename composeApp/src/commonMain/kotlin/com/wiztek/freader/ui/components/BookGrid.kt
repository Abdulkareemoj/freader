package com.wiztek.freader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wiztek.freader.library.model.LibraryBook

@Composable
fun BookGrid(

    books: List<LibraryBook>,

    onBookClick: (LibraryBook) -> Unit

) {

    LazyVerticalGrid(

        columns = GridCells.Adaptive(140.dp),

        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(16.dp),

        verticalArrangement = Arrangement.spacedBy(20.dp),

        horizontalArrangement = Arrangement.spacedBy(16.dp)

    ) {

        items(books) { book ->

            BookCard(
                book = book,
                onClick = { onBookClick(book) }
            )

        }

    }

}