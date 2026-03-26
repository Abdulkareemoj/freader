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
import com.wiztek.freader.library.model.LibraryBook
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicReaderScreen(
    book: LibraryBook,
    onBack: () -> Unit
) {
    var showUI by remember { mutableStateOf(true) }
    var isRtl by remember { mutableStateOf(false) } 
    var showSettings by remember { mutableStateOf(false) }
    var showNextChapterDialog by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val pageCount = 24
    val pagerState = rememberPagerState(pageCount = { pageCount })

    LaunchedEffect(pagerState.currentPage, pageCount) {
        if (pagerState.currentPage == pageCount - 1) {
            showUI = true 
            showNextChapterDialog = true
        }
    }
    
    val clickInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
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
            ) { page ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Surface(
                        modifier = Modifier.fillMaxSize().padding(if(showUI) 40.dp else 0.dp),
                        color = Color.DarkGray,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                "Page ${page + 1}",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineLarge
                            )
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
                        Text(book.title.substringBeforeLast(" Vol"), style = MaterialTheme.typography.titleMedium)
                        Text("Volume ${book.volumeNumber ?: 1}", style = MaterialTheme.typography.bodySmall)
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
                    TextButton(onClick = { }) {
                        Icon(Icons.Default.SkipPrevious, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Prev Chapter")
                    }
                    
                    TextButton(onClick = { }) {
                        Text("Next Chapter")
                        Spacer(Modifier.width(8.dp))
                        Icon(Icons.Default.SkipNext, null)
                    }
                }
            }
        }

        if (showNextChapterDialog) {
            AlertDialog(
                onDismissRequest = { showNextChapterDialog = false },
                title = { Text("End of Comic") },
                text = { Text("${book.title} is complete. What would you like to do next?") },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            onBack() 
                            showNextChapterDialog = false
                        }
                    ) { Text("Go to Series Details") }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showNextChapterDialog = false
                    }) { Text("Back to Library") }
                }
            )
        }
    }
}
