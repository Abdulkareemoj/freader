package com.wiztek.freader.ui.screens.reader

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.ui.components.SelectionToolbar

data class ReaderScreen(
    val book: LibraryBook
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        ReaderScreenContent(
            book = book,
            onBack = { navigator.pop() },
            onTOC = { /* TODO: Implement TOC navigation */ }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreenContent(
    book: LibraryBook,
    onBack: () -> Unit,
    onTOC: () -> Unit
) {
    var showControls by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }
    
    var showSelectionToolbar by remember { mutableStateOf(false) }

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
                        IconButton(onClick = onTOC) {
                            Icon(Icons.AutoMirrored.Filled.List, "TOC")
                        }
                        Spacer(Modifier.weight(1f))
                        Text(
                            "Page 12 of 345",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    },
                    floatingActionButton = {
                        FloatingActionButton(
                            onClick = { /* TODO */ },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor
                        ) {
                            Icon(Icons.Default.ChevronRight, "Next Page")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { 
                    if (showSelectionToolbar) showSelectionToolbar = false
                    else showControls = !showControls 
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp)
                    .padding(top = if (showControls) padding.calculateTopPadding() else 24.dp)
                    .padding(bottom = if (showControls) padding.calculateBottomPadding() else 24.dp)
            ) {
                Text(
                    text = "Chapter 1: The Beginning",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { showSelectionToolbar = true }
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Sample content...",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = 18.sp,
                        lineHeight = 27.sp,
                        fontFamily = FontFamily.Serif
                    )
                )
            }
        }

        // Quick Settings Sheet
        if (showSettings) {
            ReaderSettingsSheet(onDismiss = { showSettings = false })
        }
    }
}
