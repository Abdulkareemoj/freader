package com.wiztek.freader.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.navigation.VoyagerScreen
import com.wiztek.freader.reader.model.BookFormat
import com.wiztek.freader.ui.components.getFormatIcon

data class SearchCategory(
    val name: String,
    val icon: ImageVector,
    val format: BookFormat?,
    val color: Color
)

private val searchCategories = listOf(
    SearchCategory("eBooks", Icons.Default.AutoStories, BookFormat.EPUB, Color(0xFFE3F2FD)),
    SearchCategory("PDFs", Icons.Default.PictureAsPdf, BookFormat.PDF, Color(0xFFFFCCBC)),
    SearchCategory("Comics", Icons.Default.AutoFixHigh, BookFormat.CBZ, Color(0xFFC8E6C9)),
    SearchCategory("All Books", Icons.Default.MenuBook, null, Color(0xFFE8EAF6))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    screenModel: SearchScreenModel = org.koin.compose.koinInject()
) {
    val navigator = LocalNavigator.currentOrThrow
    val query by screenModel.query.collectAsState()
    val searchResults by screenModel.searchResults.collectAsState()
    val recentSearches by screenModel.recentSearches.collectAsState()
    val isSearching by screenModel.isSearching.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") },
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { screenModel.onQueryChange(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search books, authors, series...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { screenModel.clearSearch() }) {
                            Icon(Icons.Default.Clear, "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp)
            )

            if (isSearching) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (query.isNotEmpty()) {
                SearchResultsContent(
                    results = searchResults,
                    query = query,
                    onBookClick = { navigator.push(VoyagerScreen.BookDetails(it)) }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    if (recentSearches.isNotEmpty()) {
                        item {
                            RecentSearchesSection(
                                searches = recentSearches,
                                onSearchClick = { screenModel.searchByQuery(it) },
                                onRemove = { screenModel.removeRecentSearch(it) },
                                onClearAll = { screenModel.clearRecentSearches() }
                            )
                        }
                    }

                    item {
                        Column {
                            Text(
                                "BROWSE FORMATS",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(12.dp))
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                searchCategories.forEach { cat ->
                                    SearchCategoryCard(
                                        category = cat,
                                        modifier = Modifier.width(160.dp).height(72.dp),
                                        onClick = {
                                            screenModel.searchByFormat(cat.format)
                                            if (cat.format != null) {
                                                screenModel.searchByQuery(cat.name)
                                            } else {
                                                screenModel.searchByQuery("")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultsContent(
    results: List<LibraryBook>,
    query: String,
    onBookClick: (LibraryBook) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Text(
                "${results.size} result${if (results.size != 1) "s" else ""} for \"$query\"",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        if (results.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, null, Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                        Spacer(Modifier.height(12.dp))
                        Text("No results found", style = MaterialTheme.typography.titleMedium)
                        Text("Try a different search term", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(results) { book ->
                SearchResultItem(book = book, onClick = { onBookClick(book) })
            }
        }
    }
}

@Composable
fun SearchResultItem(book: LibraryBook, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                getFormatIcon(book.format),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author ?: "Unknown",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (book.seriesName != null) {
                    Text(
                        text = book.seriesName + if (book.volumeNumber != null) " #${book.volumeNumber}" else "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (book.progress > 0.0) {
                Text(
                    text = "${(book.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.ChevronRight, null, Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun RecentSearchesSection(
    searches: List<String>,
    onSearchClick: (String) -> Unit,
    onRemove: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "RECENT SEARCHES",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            TextButton(onClick = onClearAll) {
                Text("Clear all")
            }
        }
        Spacer(Modifier.height(8.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            searches.forEach { search ->
                InputChip(
                    selected = false,
                    onClick = { onSearchClick(search) },
                    label = { Text(search) },
                    trailingIcon = {
                        IconButton(onClick = { onRemove(search) }, modifier = Modifier.size(16.dp)) {
                            Icon(Icons.Default.Close, "Remove", modifier = Modifier.size(14.dp))
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}

@Composable
fun SearchCategoryCard(
    category: SearchCategory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = category.color.copy(alpha = 0.5f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(category.icon, null, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(12.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
