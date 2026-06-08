package com.wiztek.freader.ui.screens.discover

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.koin.compose.koinInject

@Composable
fun ProcessingLibraryScreen(
    onFinish: () -> Unit,
    screenModel: DiscoverScreenModel = koinInject()
) {
    val state by screenModel.importState.collectAsState()

    if (state.showResult) {
        AlertDialog(
            onDismissRequest = {
                screenModel.dismissResult()
                onFinish()
            },
            title = { Text("Import Complete") },
            text = {
                if (state.failCount > 0) {
                    Text(
                        "Successfully imported ${state.successCount} of ${state.totalFiles} files.\n" +
                        "${state.failCount} file(s) failed to import."
                    )
                } else {
                    Text("All ${state.successCount} file(s) imported successfully!")
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    screenModel.dismissResult()
                    onFinish()
                }) {
                    Text("OK")
                }
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AutoStories,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .padding(8.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text = "Processing your Library",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (state.totalFiles > 0) "File ${state.processedFiles} of ${state.totalFiles}" else "Preparing...",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = state.currentFile,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Spacer(Modifier.height(48.dp))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { state.progress },
                    modifier = Modifier.size(100.dp),
                    strokeWidth = 8.dp,
                    strokeCap = StrokeCap.Round
                )
                Text(
                    text = "${(state.progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(Modifier.height(48.dp))
            
            Text(
                text = "This might take a moment depending on your library size.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}
