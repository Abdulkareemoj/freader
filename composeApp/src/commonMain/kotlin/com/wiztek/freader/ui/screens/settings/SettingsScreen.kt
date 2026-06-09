package com.wiztek.freader.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wiztek.freader.settings.ReaderTheme
import com.wiztek.freader.settings.SettingsManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val navigator = LocalNavigator.currentOrThrow
    val settings by SettingsManager.settings.collectAsState()

    var showThemeSheet by remember { mutableStateOf(false) }
    var showComingSoonSnackbar by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(showComingSoonSnackbar) {
        showComingSoonSnackbar?.let {
            snackbarHostState.showSnackbar(it)
            showComingSoonSnackbar = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navigator.pop() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // APPEARANCE SECTION
            SettingsSectionHeader("APPEARANCE")

            SettingsToggleItem(
                title = "Dark Mode",
                subtitle = "Adjust the app's visual theme",
                icon = Icons.Default.DarkMode,
                checked = settings.isDarkMode,
                onCheckedChange = { SettingsManager.setDarkMode(it) }
            )

            SettingsSliderItem(
                title = "Font Scaling",
                value = settings.fontScaling,
                onValueChange = { SettingsManager.setFontScaling(it) },
                icon = Icons.Default.TextFields,
                displayValue = "${(settings.fontScaling * 100).toInt()}%",
                valueRange = 0.8f..2.0f
            )

            SettingsClickableItem(
                title = "Reader Theme",
                subtitle = themeLabel(settings.theme),
                icon = Icons.Default.Palette,
                onClick = { showThemeSheet = true }
            )

            // READER OPTIONS SECTION
            SettingsSectionHeader("READER OPTIONS")

            SettingsToggleItem(
                title = "Page Turn Animation",
                subtitle = "Use realistic curl effect",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                checked = settings.pageTurnAnimation,
                onCheckedChange = { SettingsManager.setPageTurnAnimation(it) }
            )

            SettingsToggleItem(
                title = "Two-Column Layout",
                subtitle = "Use dual columns in landscape",
                icon = Icons.Default.ViewColumn,
                checked = settings.useTwoColumns,
                onCheckedChange = { SettingsManager.setUseTwoColumns(it) }
            )

            SettingsSliderItem(
                title = "Line Height",
                value = settings.lineHeight,
                onValueChange = { SettingsManager.setLineHeight(it) },
                icon = Icons.Default.FormatLineSpacing,
                displayValue = "${(settings.lineHeight * 10).toInt() / 10.0}",
                valueRange = 1.0f..2.5f
            )

            SettingsSliderItem(
                title = "Page Margins",
                value = settings.pageMargins,
                onValueChange = { SettingsManager.setPageMargins(it) },
                icon = Icons.Default.HorizontalSplit,
                displayValue = "${(settings.pageMargins * 100).toInt()}%",
                valueRange = 0.5f..2.0f
            )

            // CLOUD SOURCES SECTION
            SettingsSectionHeader("CLOUD SOURCES")

            CloudSourceCard(
                name = "Google Drive",
                description = "Sync library and reading progress",
                icon = Icons.Default.Cloud,
                iconColor = Color(0xFF4285F4),
                onClick = { showComingSoonSnackbar = "Google Drive sync — coming soon" }
            )

            CloudSourceCard(
                name = "Dropbox",
                description = "Import books directly",
                icon = Icons.Default.Folder,
                iconColor = Color(0xFF0061FF),
                onClick = { showComingSoonSnackbar = "Dropbox import — coming soon" }
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "FReader v2.4.0 (Build 829)",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }

    if (showThemeSheet) {
        ReaderThemeSheet(
            currentTheme = settings.theme,
            onThemeSelected = { theme ->
                SettingsManager.setTheme(theme)
                showThemeSheet = false
            },
            onDismiss = { showThemeSheet = false }
        )
    }
}

private fun themeLabel(theme: ReaderTheme): String = when (theme) {
    ReaderTheme.SEPIA -> "Sepia"
    ReaderTheme.PAPER -> "Paper"
    ReaderTheme.SOLARIZED -> "Solarized"
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = Color(0xFFF05A28),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SettingsToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFF05A28)
            )
        )
    }
}

@Composable
fun SettingsClickableItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        TextButton(onClick = onClick) {
            Text(subtitle, color = Color(0xFFF05A28), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun SettingsSliderItem(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    displayValue: String,
    valueRange: ClosedFloatingPointRange<Float> = 0.8f..2.0f
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text(displayValue, style = MaterialTheme.typography.titleSmall, color = Color(0xFFF05A28), fontWeight = FontWeight.Bold)
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 40.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("A", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color(0xFFF05A28)
                )
            )
            Text("A", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderThemeSheet(
    currentTheme: ReaderTheme,
    onThemeSelected: (ReaderTheme) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                "Reader Theme",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )
            ReaderThemeOption(
                theme = ReaderTheme.SEPIA,
                label = "Sepia",
                color = Color(0xFFF5F5DC),
                isSelected = currentTheme == ReaderTheme.SEPIA,
                onClick = { onThemeSelected(ReaderTheme.SEPIA) }
            )
            ReaderThemeOption(
                theme = ReaderTheme.PAPER,
                label = "Paper White",
                color = Color.White,
                isSelected = currentTheme == ReaderTheme.PAPER,
                onClick = { onThemeSelected(ReaderTheme.PAPER) }
            )
            ReaderThemeOption(
                theme = ReaderTheme.SOLARIZED,
                label = "Solarized Dark",
                color = Color(0xFF002B36),
                isSelected = currentTheme == ReaderTheme.SOLARIZED,
                onClick = { onThemeSelected(ReaderTheme.SOLARIZED) }
            )
        }
    }
}

@Composable
private fun ReaderThemeOption(
    theme: ReaderTheme,
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onClick)
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(color)
                .border(if (isSelected) 2.dp else 1.dp, if (isSelected) Color(0xFFF05A28) else MaterialTheme.colorScheme.outline, CircleShape)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            label,
            modifier = Modifier.padding(vertical = 12.dp),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun CloudSourceCard(
    name: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = iconColor)
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            TextButton(onClick = onClick) {
                Text("Connect", color = Color(0xFFF05A28), fontWeight = FontWeight.Bold)
            }
        }
    }
}
