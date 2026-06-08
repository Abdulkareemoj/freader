package com.wiztek.freader.ui.screens.reader

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.library.model.Bookmark
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.model.ReadiumManifest
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import com.wiztek.freader.ui.components.reader.PublicationReader
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ReaderScreen(
    val book: LibraryBook
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinInject<ReaderScreenModel> { parametersOf(book.id) }
        val state by screenModel.state.collectAsState()
        
        var navigateToHref: ((String) -> Unit)? by remember { mutableStateOf(null) }

        ReaderScreenContent(
            book = state.book ?: book,
            manifest = state.manifest,
            bookmarks = state.bookmarks,
            onBack = { 
                navigator.pop()
            },
            onTOC = { href ->
                navigateToHref?.invoke(href)
            },
            onSaveProgress = { progress, locator ->
                screenModel.saveProgress(progress, locator)
            },
            onAddBookmark = { locator ->
                val existing = state.bookmarks.find { it.location == locator }
                if (existing != null) {
                    screenModel.removeBookmark(existing.id)
                } else {
                    screenModel.addBookmark(locator)
                }
            },
            setNavigationCallback = { callback ->
                navigateToHref = callback
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreenContent(
    book: LibraryBook,
    manifest: ReadiumManifest?,
    bookmarks: List<Bookmark>,
    onBack: () -> Unit,
    onTOC: (String) -> Unit,
    onSaveProgress: (Double, String?) -> Unit,
    onAddBookmark: (String) -> Unit,
    setNavigationCallback: (((String) -> Unit)?) -> Unit
) {
    var showControls by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }
    var showTOC by remember { mutableStateOf(false) }
    var currentLocatorJson by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                TopAppBar(
                    title = { Text(book.title, style = MaterialTheme.typography.titleMedium) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        val isBookmarked = currentLocatorJson != null && bookmarks.any { it.location == currentLocatorJson }
                        IconButton(onClick = { 
                            currentLocatorJson?.let { onAddBookmark(it) }
                        }) {
                            Icon(
                                if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = if (isBookmarked) "Remove Bookmark" else "Add Bookmark"
                            )
                        }
                        IconButton(onClick = { showSettings = true }) {
                            Icon(Icons.Default.TextFields, "Font Settings")
                        }
                    }
                )
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = showControls,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                BottomAppBar(
                    actions = {
                        IconButton(onClick = { showTOC = true }) {
                            Icon(Icons.AutoMirrored.Filled.List, "TOC")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            PublicationReader(
                book = book,
                modifier = Modifier.fillMaxSize(),
                onProgressChanged = { progress, locator -> 
                    currentLocatorJson = locator
                    onSaveProgress(progress, locator)
                },
                onToggleControls = { showControls = !showControls },
                setNavigationCallback = setNavigationCallback
            )
        }

        if (showSettings) {
            ReaderSettingsSheet(onDismiss = { showSettings = false })
        }

        if (showTOC) {
            ModalBottomSheet(onDismissRequest = { showTOC = false }) {
                var selectedTab by remember { mutableStateOf(0) }
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)) {
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Contents") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Bookmarks") }
                        )
                    }

                    when (selectedTab) {
                        0 -> {
                            if (manifest != null) {
                                LazyColumn {
                                    items(manifest.toc) { item ->
                                        ListItem(
                                            headlineContent = { Text(item.title ?: item.href) },
                                            modifier = Modifier.clickable {
                                                onTOC(item.href)
                                                showTOC = false
                                            }
                                        )
                                    }
                                }
                            } else {
                                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No table of contents available")
                                }
                            }
                        }
                        1 -> {
                            if (bookmarks.isNotEmpty()) {
                                LazyColumn {
                                    items(bookmarks) { bookmark ->
                                        ListItem(
                                            headlineContent = { Text(bookmark.label) },
                                            supportingContent = { 
                                                val date = Instant.fromEpochMilliseconds(bookmark.createdAt)
                                                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                                                Text("Saved at $date")
                                            },
                                            trailingContent = {
                                                IconButton(onClick = { onAddBookmark(bookmark.location) }) {
                                                    Icon(Icons.Default.Delete, "Remove")
                                                }
                                            },
                                            modifier = Modifier.clickable {
                                                onTOC(bookmark.location)
                                                showTOC = false
                                            }
                                        )
                                    }
                                }
                            } else {
                                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No bookmarks yet")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
