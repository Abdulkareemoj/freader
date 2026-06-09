package com.wiztek.freader.ui.screens.discover

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.library.repository.LibraryRepository
import com.wiztek.freader.navigation.VoyagerScreen
import com.wiztek.freader.reader.model.BookFormat
import com.wiztek.freader.ui.components.BookCard
import com.wiztek.freader.ui.components.SectionHeader
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerMode
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

data class CategoryDef(
    val name: String,
    val icon: ImageVector,
    val format: BookFormat?
)

private val categories = listOf(
    CategoryDef("eBooks", Icons.Default.AutoStories, BookFormat.EPUB),
    CategoryDef("PDFs", Icons.Default.PictureAsPdf, BookFormat.PDF),
    CategoryDef("Comics", Icons.Default.AutoFixHigh, BookFormat.CBZ),
    CategoryDef("All Books", Icons.Default.MenuBook, null)
)

@Composable
fun DiscoverScreen(
    screenModel: DiscoverScreenModel
) {
    val scope = rememberCoroutineScope()
    val navigator = LocalNavigator.currentOrThrow
    val repository = koinInject<LibraryRepository>()
    val importState by screenModel.importState.collectAsState()
    val recentImports by screenModel.recentImports.collectAsState()

    val allBooks by repository.getAllBooks().collectAsState(initial = emptyList())
    val validBooks = remember(allBooks) {
        allBooks.filter { it.filePath.isNotBlank() }
    }

    val fileLauncher = rememberFilePickerLauncher(
        type = PickerType.File(extensions = listOf("epub", "pdf", "txt", "cbz", "cbr", "mobi")),
        mode = PickerMode.Multiple()
    ) { files ->
        if (files != null && files.isNotEmpty()) {
            screenModel.importFiles(files)
            navigator.push(VoyagerScreen.ProcessingLibrary)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    onClick = { scope.launch { fileLauncher.launch() } },
                    modifier = Modifier.weight(1f).height(140.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FileUpload, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                            Spacer(Modifier.height(6.dp))
                            Text("Import Files", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                            Text("EPUB, PDF, CBZ", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        }
                    }
                }
                Card(
                    onClick = { scope.launch { fileLauncher.launch() } },
                    modifier = Modifier.weight(1f).height(140.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FolderOpen, null, Modifier.size(36.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(Modifier.height(6.dp))
                            Text("Browse Folder", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSecondaryContainer, fontWeight = FontWeight.Bold)
                            Text("Import multiple", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }

        item {
            SectionHeader(title = "Browse by Format")
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.take(2).forEach { cat ->
                    FormatCategoryCard(
                        category = cat,
                        bookCount = if (cat.format != null) validBooks.count { it.format == cat.format } else validBooks.size,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val filtered = if (cat.format != null) validBooks.filter { it.format == cat.format } else validBooks
                            navigator.push(VoyagerScreen.BookGrid(cat.name, filtered))
                        }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                categories.drop(2).forEach { cat ->
                    FormatCategoryCard(
                        category = cat,
                        bookCount = if (cat.format != null) validBooks.count { it.format == cat.format } else validBooks.size,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val filtered = if (cat.format != null) validBooks.filter { it.format == cat.format } else validBooks
                            navigator.push(VoyagerScreen.BookGrid(cat.name, filtered))
                        }
                    )
                }
            }
        }

        if (recentImports.isNotEmpty()) {
            item {
                SectionHeader(title = "Recently Imported")
            }

            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(end = 16.dp)
                ) {
                    items(recentImports) { book ->
                        BookCard(
                            book = book,
                            onClick = { navigator.push(VoyagerScreen.Reader(book)) },
                            modifier = Modifier.width(140.dp),
                            showProgress = false
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

@Composable
fun FormatCategoryCard(
    category: CategoryDef,
    bookCount: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(Modifier.fillMaxSize().padding(16.dp)) {
            Column {
                Icon(category.icon, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                Text(category.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(
                    "$bookCount book${if (bookCount != 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
