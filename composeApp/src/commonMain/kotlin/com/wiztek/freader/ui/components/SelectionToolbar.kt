package com.wiztek.freader.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SelectionToolbar(
    onCopy: () -> Unit,
    onHighlight: () -> Unit,
    onNote: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
        tonalElevation = 8.dp,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(onClick = onCopy) {
                Icon(Icons.Default.ContentCopy, "Copy", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onHighlight) {
                Icon(Icons.Default.FormatPaint, "Highlight", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onNote) {
                Icon(Icons.AutoMirrored.Filled.NoteAdd, "Note", modifier = Modifier.size(20.dp))
            }
            VerticalDivider(modifier = Modifier.height(24.dp).padding(horizontal = 4.dp))
            IconButton(onClick = onShare) {
                Icon(Icons.Default.Share, "Share", modifier = Modifier.size(20.dp))
            }
        }
    }
}
