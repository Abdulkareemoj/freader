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
fun RecentBooksSection(
    books: List<LibraryBook>,
    onBookClick: (LibraryBook) -> Unit,
    onBookLongClick: ((LibraryBook) -> Unit)? = null,
    isSelectionMode: Boolean = false,
    isSelected: (LibraryBook) -> Boolean = { false },
    menuItems: (LibraryBook) -> List<CardMenuAction> = { emptyList() }
){
    Column {
        SectionHeader(title = "Recent Books")

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(books) { book ->
                BookCard(
                    book = book,
                    onClick = { onBookClick(book) },
                    onLongClick = onBookLongClick?.let { { it(book) } },
                    isSelectionMode = isSelectionMode,
                    isSelected = isSelected(book),
                    menuItems = menuItems(book),
                    modifier = Modifier.width(160.dp)
                )
            }
        }
    }
}
