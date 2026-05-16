package com.wiztek.freader.ui.screens.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.FormatTextdirectionLToR
import androidx.compose.material.icons.automirrored.filled.FormatTextdirectionRToL
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.ZipImageModel
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicReaderScreen(
    viewModel: ComicReaderViewModel,
    onBack: () -> Unit,
    onNavigateToBook: (LibraryBook) -> Unit = {}
) {
    val uiState by viewModel.state.collectAsState()
    // val seriesBooks by viewModel.seriesBooks.collectAsState() // Unused for now but available if needed

    var showUI by remember { mutableStateOf(true) }
    var isRtl by remember { mutableStateOf(false) } 
    var showSettings by remember { mutableStateOf(false) }
    var showNextChapterDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val pageCount = uiState.pages.size
    val pagerState = rememberPagerState(
        initialPage = uiState.initialPage,
        pageCount = { pageCount }
    )

    // Sync pagerState with initialPage when it changes (e.g., after loading)
    LaunchedEffect(uiState.initialPage) {
        if (uiState.initialPage != pagerState.currentPage && pageCount > 0) {
            pagerState.scrollToPage(uiState.initialPage)
        }
    }

    LaunchedEffect(pagerState.currentPage, pageCount) {
        if (pageCount > 0 && pagerState.currentPage == pageCount - 1) {
            showUI = true 
            showNextChapterDialog = true
        }
    }

    LaunchedEffect(pagerState.currentPage) {
        if (uiState.pages.isNotEmpty()) {
            viewModel.saveProgress(pagerState.currentPage)
        }
    }
    
    val clickInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.error != null) {
            Text(
                text = "Error: ${uiState.error}",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center).padding(16.dp)
            )
        } else {
            CompositionLocalProvider(
                LocalLayoutDirection provides 
                    if (isRtl) androidx.compose.ui.unit.LayoutDirection.Rtl 
                    else androidx.compose.ui.unit.LayoutDirection.Ltr
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = clickInteractionSource,
                            indication = null
                        ) { showUI = !showUI },
                    pageSpacing = 8.dp,
                    beyondViewportPageCount = 1
                ) { pageIndex ->
                    val pagePath = uiState.pages.getOrNull(pageIndex)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (pagePath != null) {
                            Surface(
                                modifier = Modifier.fillMaxSize().padding(if(showUI) 40.dp else 0.dp),
                                color = Color.DarkGray,
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    AsyncImage(
                                        model = ZipImageModel(uiState.book.filePath, pagePath),
                                        contentDescription = "Page ${pageIndex + 1}",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = showUI,
            enter = slideInVertically(initialOffsetY = { -it }),
            exit = slideOutVertically(targetOffsetY = { -it })
        ) {
            TopAppBar(
                title = { 
                    Column {
                        Text(uiState.book.title.substringBeforeLast(" Vol"), style = MaterialTheme.typography.titleMedium)
                        Text("Volume ${uiState.book.volumeNumber ?: 1}", style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isRtl = !isRtl }) {
                        Icon(
                            if (isRtl) Icons.AutoMirrored.Filled.FormatTextdirectionRToL
                            else Icons.AutoMirrored.Filled.FormatTextdirectionLToR,
                            "Toggle RTL"
                        )
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Default.Settings, "Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }

        AnimatedVisibility(
            visible = showUI,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text("${pagerState.currentPage + 1}", style = MaterialTheme.typography.bodySmall, color = Color.White)
                    Slider(
                        value = pagerState.currentPage.toFloat(),
                        onValueChange = { page -> 
                            if (page.toInt() != pagerState.currentPage) {
                                scope.launch { pagerState.animateScrollToPage(page.toInt()) }
                            }
                        },
                        valueRange = 0f..(pageCount - 1).coerceAtLeast(0).toFloat(),
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text("$pageCount", style = MaterialTheme.typography.bodySmall, color = Color.White)
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val nextBook = viewModel.getNextBook()
                    val prevBook = viewModel.getPrevBook()

                    TextButton(
                        onClick = { 
                            if (prevBook != null) {
                                onNavigateToBook(prevBook)
                            }
                        },
                        enabled = prevBook != null
                    ) {
                        Icon(Icons.Default.SkipPrevious, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Prev Chapter")
                    }
                    
                    TextButton(
                        onClick = { 
                            if (nextBook != null) {
                                onNavigateToBook(nextBook)
                            }
                        },
                        enabled = nextBook != null
                    ) {
                        Text("Next Chapter")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.SkipNext, null)
                    }
                }
            }
        }

        if (showNextChapterDialog) {
            val nextBook = viewModel.getNextBook()
            AlertDialog(
                onDismissRequest = { showNextChapterDialog = false },
                title = { Text(if (nextBook != null) "Chapter Complete" else "End of Comic") },
                text = { 
                    Text(
                        if (nextBook != null) "Would you like to read the next volume: ${nextBook.title}?"
                        else "${uiState.book.title} is complete. What would you like to do next?"
                    ) 
                },
                confirmButton = {
                    if (nextBook != null) {
                        TextButton(
                            onClick = { 
                                onNavigateToBook(nextBook)
                                showNextChapterDialog = false
                            }
                        ) { Text("Read Next") }
                    } else {
                        TextButton(
                            onClick = { 
                                onBack() 
                                showNextChapterDialog = false
                            }
                        ) { Text("Go to Series Details") }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showNextChapterDialog = false
                    }) { Text(if (nextBook != null) "Not Now" else "Back to Library") }
                }
            )
        }
    }
}
