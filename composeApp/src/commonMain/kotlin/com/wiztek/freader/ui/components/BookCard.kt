package com.wiztek.freader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.AutoFixHigh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.model.BookFormat

@Composable
fun BookCard(
    book: LibraryBook,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showProgress: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(getPlaceholderColor(book.format)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getFormatIcon(book.format),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
            
            // Progress Bar
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
