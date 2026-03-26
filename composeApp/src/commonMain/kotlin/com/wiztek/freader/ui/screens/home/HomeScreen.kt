package com.wiztek.freader.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.ui.ReaderScreen
import com.wiztek.freader.ui.components.*

@Composable
fun HomeScreen(
    books: List<LibraryBook>,
    onBookClick: (LibraryBook) -> Unit
) {
    val navigator = LocalNavigator.currentOrThrow

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            HomeCarousel(
                books = books,
                onBookClick = { book ->
                    navigator.push(ReaderScreen(book))
                },
                onSeeAllClick = { /* TODO */ }
            )
        }

        item {
            ContinueReadingSection(
                books = books,
                onBookClick = onBookClick
            )
        }

        item {
            CategoriesSection()
        }

        item {
            RecentBooksSection(
                books = books,
                onBookClick = onBookClick
            )
        }

        item {
            Spacer(Modifier.height(16.dp))
        }
    }
}
