package com.wiztek.freader.ui.screens.reader

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wiztek.freader.settings.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsSheet(onDismiss: () -> Unit) {
    val settings by SettingsManager.settings.collectAsState()

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Reader Settings", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(24.dp))

            // Font Scaling
            Text("Font Scaling", style = MaterialTheme.typography.labelLarge)
            Slider(
                value = settings.fontScaling,
                onValueChange = { SettingsManager.setFontScaling(it) },
                valueRange = 0.8f..2.0f
            )
            
            Spacer(Modifier.height(16.dp))

            // Page Turn Animation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Page Turn Animation", style = MaterialTheme.typography.labelLarge)
                Switch(
                    checked = settings.pageTurnAnimation,
                    onCheckedChange = { SettingsManager.setPageTurnAnimation(it) }
                )
            }
        }
    }
}
