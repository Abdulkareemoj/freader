package com.wiztek.freader.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.ui.components.BookCard
import kotlin.math.absoluteValue

@Composable
fun HomeCarousel(
    books: List<LibraryBook>,
    onBookClick: (LibraryBook) -> Unit,
    onSeeAllClick: () -> Unit = {}
) {
    if (books.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { books.size })

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Trending books",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(onClick = onSeeAllClick) {
                Text("See all")
            }
        }

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 32.dp),
            pageSpacing = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) { page ->
            val book = books[page]

            BookCard(
                book = book,
                onClick = { onBookClick(book) },
                showProgress = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        val pageOffset = (
                            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                        ).absoluteValue
                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}
