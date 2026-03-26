package com.wiztek.freader.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppScreen(
    val title: String,
    val icon: ImageVector
) {
    Home("Home", Icons.Default.Home),
    Library("Library", Icons.AutoMirrored.Filled.MenuBook),
    Discover("Discover", Icons.Default.Explore),
    Collections("Collections", Icons.Default.Folder),
    Stats("Stats", Icons.Default.BarChart),

    Settings("Settings", Icons.Default.Settings),
    About("About", Icons.Default.Info)
}
