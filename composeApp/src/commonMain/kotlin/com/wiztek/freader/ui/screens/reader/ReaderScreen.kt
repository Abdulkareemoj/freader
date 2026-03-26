package com.wiztek.freader.ui.screens.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.ui.components.SelectionToolbar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderScreen(
    book: LibraryBook,
    onBack: () -> Unit,
    onTOC: () -> Unit
) {
    var showControls by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }
    var fontSize by remember { mutableStateOf(18f) }
    var themeColor by remember { mutableStateOf(Color(0xFFFDF0D5)) } // Paper theme
    
    // Mock state for selection toolbar
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
                        IconButton(onClick = { /* TODO: Bookmarks */ }) {
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
                            onClick = { /* TODO: Next Page */ },
                            containerColor = BottomAppBarDefaults.bottomAppBarFabColor,
                            elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation()
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
                .background(themeColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { 
                    if (showSelectionToolbar) {
                        showSelectionToolbar = false
                    } else {
                        showControls = !showControls 
                    }
                }
        ) {
            // Main Reading Content
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
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.clickable { showSelectionToolbar = true } // Mock triggering selection
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.\\n\\nDuis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontSize = fontSize.sp,
                        lineHeight = (fontSize * 1.5).sp,
                        color = Color.Black.copy(alpha = 0.7f),
                        fontFamily = FontFamily.Serif
                    )
                )
                // Repeated content for scroll testing
                repeat(10) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = fontSize.sp,
                            lineHeight = (fontSize * 1.5).sp,
                            color = Color.Black.copy(alpha = 0.7f),
                            fontFamily = FontFamily.Serif
                        )
                    )
                }
            }
        }

        // Selection Toolbar Overlay
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            AnimatedVisibility(
                visible = showSelectionToolbar,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                SelectionToolbar(
                    onCopy = { showSelectionToolbar = false },
                    onHighlight = { showSelectionToolbar = false },
                    onNote = { showSelectionToolbar = false },
                    onShare = { showSelectionToolbar = false },
                    modifier = Modifier.padding(top = 100.dp)
                )
            }
        }

        // Font Settings Modal Bottom Sheet (or Dialog)
        if (showSettings) {
            ModalBottomSheet(
                onDismissRequest = { showSettings = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text("Appearance", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(24.dp))
                    
                    Text("Font Size", style = MaterialTheme.typography.labelLarge)
                    Slider(
                        value = fontSize,
                        onValueChange = { fontSize = it },
                        valueRange = 12f..32f,
                        steps = 10
                    )
                    
                    Spacer(Modifier.height(16.dp))
                    
                    Text("Theme", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ReaderThemeButton(Color.White, themeColor == Color.White) { themeColor = Color.White }
                        ReaderThemeButton(Color(0xFFFDF0D5), themeColor == Color(0xFFFDF0D5)) { themeColor = Color(0xFFFDF0D5) }
                        ReaderThemeButton(Color(0xFF121212), themeColor == Color(0xFF121212)) { themeColor = Color(0xFF121212) }
                    }
                }
            }
        }
    }
}

@Composable
fun ReaderThemeButton(color: Color, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(48.dp)
            .clickable { onClick() },
        shape = androidx.compose.foundation.shape.CircleShape,
        color = color,
        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        tonalElevation = 2.dp
    ) {}
}
