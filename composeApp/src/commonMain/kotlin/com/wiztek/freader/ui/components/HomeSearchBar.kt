package com.wiztek.freader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeSearchBar() {
    var query by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LibrarySearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = {},
            active = false,
            onActiveChange = {},
            placeholder = { Text("Search your favorite book") },
            modifier = Modifier.weight(1.0f)
        )

        IconButton(
            onClick = { /* TODO: Open filters */ },
            modifier = Modifier
                .size(56.dp)
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Icon(
                Icons.Default.Tune,
                contentDescription = "Filter",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
