package com.yourapp.ui.components.reader

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderToolbar(
    onBack: () -> Unit,
    onChapters: () -> Unit,
    onFontSettings: () -> Unit,
    onThemeSettings: () -> Unit,
    onMore: () -> Unit
) {
    TopAppBar(
        title = { Text("Reading") },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }
        },
        actions = {
            IconButton(
                onClick = onChapters
            ) {
                Icon(Icons.AutoMirrored.Filled.List, null)
            }

            IconButton(
                onClick = onFontSettings
            ) {
                Icon(Icons.Default.TextFields, null)
            }

            IconButton(
                onClick = onThemeSettings
            ) {
                Icon(Icons.Default.DarkMode, null)
            }

            IconButton(
                onClick = onMore
            ) {
                Icon(Icons.Default.MoreVert, null)
            }
        }
    )
}
