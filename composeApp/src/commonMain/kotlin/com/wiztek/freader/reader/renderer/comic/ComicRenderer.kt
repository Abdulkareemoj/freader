package com.wiztek.freader.reader.renderer.comic

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Renderer for Comic files (CBZ/CBR).
 * Displays a list of image paths using a Pager.
 */
@Composable
fun ComicRenderer(
    pages: List<String>,
    currentPage: Int,
    onPageChanged: (Int) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = currentPage,
        pageCount = { pages.size }
    )

    // Sync pager state with model state
    LaunchedEffect(currentPage) {
        if (pagerState.currentPage != currentPage) {
            pagerState.scrollToPage(currentPage)
        }
    }

    // Sync model state with pager state
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                onPageChanged(page)
            }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        // Note: For this to load the image inside the CBZ, 
        // you would need a custom Coil Interceptor that handles the "cbz://" scheme.
        AsyncImage(
            model = pages[page],
            contentDescription = "Page ${page + 1}",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
    }
}
