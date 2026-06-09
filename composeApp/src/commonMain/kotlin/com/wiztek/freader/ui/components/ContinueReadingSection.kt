package com.wiztek.freader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wiztek.freader.library.model.LibraryBook

@Composable
fun ContinueReadingSection(
    books: List<LibraryBook>,
    title: String = "Continue Reading",
    onBookClick: (LibraryBook) -> Unit,
    onSeeAllClick: (() -> Unit)? = null,
    onBookLongClick: ((LibraryBook) -> Unit)? = null,
    isSelectionMode: Boolean = false,
    isSelected: (LibraryBook) -> Boolean = { false },
    menuItems: (LibraryBook) -> List<CardMenuAction> = { emptyList() }
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            if (onSeeAllClick != null) {
                TextButton(onClick = onSeeAllClick) {
                    Text("See all")
                }
            }
        }

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
