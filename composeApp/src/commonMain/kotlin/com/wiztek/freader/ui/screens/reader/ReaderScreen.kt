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
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.model.ReadiumManifest
import org.koin.core.parameter.parametersOf
import com.wiztek.freader.ui.components.reader.PublicationReader

data class ReaderScreen(
    val book: LibraryBook
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = koinScreenModel<ReaderScreenModel> { parametersOf(book.id) }
        val state by screenModel.state.collectAsState()
        
        var navigateToHref: ((String) -> Unit)? by remember { mutableStateOf(null) }

        ReaderScreenContent(
            book = state.book ?: book,
            manifest = state.manifest,
            onBack = { 
                navigator.pop()
            },
            onTOC = { href ->
                navigateToHref?.invoke(href)
            },
            onSaveProgress = { progress, locator ->
                screenModel.saveProgress(progress, locator)
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
    onBack: () -> Unit,
    onTOC: (String) -> Unit,
    onSaveProgress: (Double, String?) -> Unit,
    setNavigationCallback: (((String) -> Unit)?) -> Unit
) {
    var showControls by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }
    var showTOC by remember { mutableStateOf(false) }

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
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Default.BookmarkBorder, "Bookmark")
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
                    onSaveProgress(progress, locator)
                },
                onToggleControls = { showControls = !showControls },
                setNavigationCallback = setNavigationCallback
            )
        }

        if (showSettings) {
            ReaderSettingsSheet(onDismiss = { showSettings = false })
        }

        if (showTOC && manifest != null) {
            ModalBottomSheet(onDismissRequest = { showTOC = false }) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("Table of Contents", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(16.dp))
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
                }
            }
        }
    }
}
