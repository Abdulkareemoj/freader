package com.wiztek.freader.ui.screens.collections

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.model.LibraryCollection
import com.wiztek.freader.ui.components.EmptyStateView
import com.wiztek.freader.ui.components.getFormatIcon
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import coil3.compose.AsyncImage
import org.koin.compose.koinInject

data class CollectionTab(val name: String, val icon: ImageVector, val key: String)

private val collectionTabs = listOf(
    CollectionTab("All", Icons.Default.Bookmark, "all"),
    CollectionTab("Shared", Icons.Default.CloudSync, "shared"),
    CollectionTab("Device Only", Icons.Default.PhoneAndroid, "device")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(
    viewModel: CollectionsViewModel = koinInject()
) {
    val state by viewModel.state.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    var showCreateDialog by remember { mutableStateOf(false) }
    var collectionToDelete by remember { mutableStateOf<LibraryCollection?>(null) }
    var collectionToRename by remember { mutableStateOf<LibraryCollection?>(null) }

    val filteredCollections = remember(state.collections, state.selectedTab) {
        when (state.selectedTab) {
            1 -> emptyList()
            else -> state.collections
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PrimaryTabRow(
            selectedTabIndex = state.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {
            collectionTabs.forEachIndexed { index, tab ->
                Tab(
                    selected = state.selectedTab == index,
                    onClick = { viewModel.selectTab(index) },
                    icon = { Icon(tab.icon, null, modifier = Modifier.size(18.dp)) },
                    text = {
                        Text(
                            tab.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (state.selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.selectedTab == 1 -> {
                    SharedCollectionsPlaceholder()
                }
                filteredCollections.isEmpty() -> {
                    EmptyStateView(
                        title = "Your Collections are Empty",
                        message = "Tap the '+' button to create your first collection.",
                        icon = Icons.Default.Bookmark,
                        actionLabel = "Create Collection",
                        onActionClick = { showCreateDialog = true }
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredCollections, key = { it.collection.id }) { info ->
                            CollectionCard(
                                collection = info.collection,
                                bookCount = info.bookCount,
                                firstBooks = info.firstBooks,
                                onClick = {
                                    navigator.push(
                                        com.wiztek.freader.navigation.VoyagerScreen.CollectionDetails(info.collection)
                                    )
                                },
                                onDelete = { collectionToDelete = info.collection },
                                onRename = { collectionToRename = info.collection }
                            )
                        }
                    }
                }
            }

            if (state.selectedTab != 1) {
                FloatingActionButton(
                    onClick = { showCreateDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(24.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.Add, "Create new collection")
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateCollectionDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name ->
                viewModel.createCollection(name)
                showCreateDialog = false
            }
        )
    }

    collectionToDelete?.let { col ->
        AlertDialog(
            onDismissRequest = { collectionToDelete = null },
            title = { Text("Delete Collection") },
            text = { Text("Delete '${col.name}' and remove all book associations? The books themselves won't be deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteCollection(col.id)
                        collectionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { collectionToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    collectionToRename?.let { col ->
        RenameCollectionDialog(
            currentName = col.name,
            onConfirm = { newName ->
                viewModel.renameCollection(col.id, newName)
                collectionToRename = null
            },
            onDismiss = { collectionToRename = null }
        )
    }
}

@Composable
fun SharedCollectionsPlaceholder() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(
                Icons.Default.CloudSync,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Cloud Sync — Coming Soon",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Sync your collections across devices by connecting a cloud storage provider.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun CollectionCard(
    collection: LibraryCollection,
    bookCount: Int,
    firstBooks: List<LibraryBook>,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onRename: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(8.dp)
        ) {
            if (firstBooks.isNotEmpty()) {
                BookCoverStack(books = firstBooks)
            } else {
                Icon(
                    Icons.Default.Bookmark,
                    null,
                    modifier = Modifier.size(48.dp).align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.5f)
                )
            }

            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, "Collection options", tint = MaterialTheme.colorScheme.onSecondaryContainer)
                }
                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = { onRename(); showMenu = false },
                        leadingIcon = { Icon(Icons.Default.DriveFileRenameOutline, null, Modifier.size(18.dp)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = { onDelete(); showMenu = false },
                        leadingIcon = { Icon(Icons.Default.Delete, null, Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error) }
                    )
                }
            }

            Surface(
                modifier = Modifier.align(Alignment.BottomStart),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
            ) {
                Text(
                    "$bookCount book${if (bookCount != 1) "s" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Text(
            collection.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable { onClick() }
        )

        Text(
            "Created ${formatTimestamp(collection.createdAt)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
private fun BookCoverStack(books: List<LibraryBook>) {
    Box(modifier = Modifier.fillMaxSize().padding(4.dp)) {
        val displayBooks = books.take(4)
        displayBooks.forEachIndexed { index, book ->
            val offsetX = (index % 2) * 30
            val offsetY = (index / 2) * 30
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = offsetX.dp, top = offsetY.dp)
            ) {
                if (book.coverPath != null) {
                    AsyncImage(
                        model = book.coverPath,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .background(getCoverColor(index)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            getFormatIcon(book.format),
                            null,
                            Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

private fun getCoverColor(index: Int): Color = when (index) {
    0 -> Color(0xFFE1BEE7)
    1 -> Color(0xFFFFCCBC)
    2 -> Color(0xFFC8E6C9)
    else -> Color(0xFFCFD8DC)
}

private fun formatTimestamp(epochMs: Long): String {
    val instant = Instant.fromEpochMilliseconds(epochMs)
    val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${local.month.name.take(3)} ${local.dayOfMonth}, ${local.year}"
}

@Composable
fun CreateCollectionDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Collection") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onConfirm(name) }) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun RenameCollectionDialog(
    currentName: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename Collection") },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(onClick = { if (name.isNotBlank()) onConfirm(name) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
