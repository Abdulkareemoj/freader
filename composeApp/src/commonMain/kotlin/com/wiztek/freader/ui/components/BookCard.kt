package com.wiztek.freader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.model.BookFormat

data class CardMenuAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun BookCard(
    book: LibraryBook,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    showProgress: Boolean = true,
    menuItems: List<CardMenuAction> = emptyList(),
    isSelected: Boolean = false,
    isSelectionMode: Boolean = false
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(8.dp))
            .let {
                if (onLongClick != null || isSelectionMode) {
                    it.combinedClickable(
                        onClick = onClick,
                        onLongClick = onLongClick
                    )
                } else {
                    it.clickable(onClick = onClick)
                }
            },
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(getPlaceholderColor(book.format)),
                    contentAlignment = Alignment.Center
                ) {
                    if (book.coverPath != null) {
                        AsyncImage(
                            model = book.coverPath,
                            contentDescription = "Cover for ${book.title}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = getFormatIcon(book.format),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                if (showProgress && book.progress > 0.0) {
                    LinearProgressIndicator(
                        progress = { book.progress.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }

                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = book.author ?: "Unknown",
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (isSelectionMode) {
                Icon(
                    imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                    contentDescription = if (isSelected) "Deselect" else "Select",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .let { if (isSelected) it.background(MaterialTheme.colorScheme.primary) else it },
                    tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else if (menuItems.isNotEmpty()) {
                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp).padding(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        offset = DpOffset(x = (-120).dp, y = 0.dp)
                    ) {
                        menuItems.forEach { action ->
                            DropdownMenuItem(
                                text = { Text(action.label) },
                                onClick = {
                                    menuExpanded = false
                                    action.onClick()
                                },
                                leadingIcon = {
                                    Icon(action.icon, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun getFormatIcon(format: BookFormat): ImageVector {
    return when (format) {
        BookFormat.EPUB -> Icons.Default.AutoStories
        BookFormat.PDF -> Icons.Default.PictureAsPdf
        BookFormat.CBZ, BookFormat.CBR -> Icons.Default.AutoFixHigh
        else -> Icons.Default.Description
    }
}

@Composable
fun getPlaceholderColor(format: BookFormat): Color {
    return when (format) {
        BookFormat.EPUB -> Color(0xFFE1BEE7)
        BookFormat.PDF -> Color(0xFFFFCCBC)
        BookFormat.CBZ, BookFormat.CBR -> Color(0xFFC8E6C9)
        else -> Color(0xFFCFD8DC)
    }
}
