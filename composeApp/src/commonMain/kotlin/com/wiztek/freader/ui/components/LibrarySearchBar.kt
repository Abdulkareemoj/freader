package com.wiztek.freader.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrarySearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit = {},
    active: Boolean = false,
    onActiveChange: (Boolean) -> Unit = {},
    placeholder: @Composable () -> Unit = { Text("Search") },
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {}
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                expanded = active,
                onExpandedChange = onActiveChange,
                placeholder = placeholder,
            )
        },
        expanded = active,
        onExpandedChange = onActiveChange,
        modifier = modifier.fillMaxWidth()
    ) {
        content()
    }
}
