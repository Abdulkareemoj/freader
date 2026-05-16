package com.wiztek.freader.ui.components.reader

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wiztek.freader.library.model.LibraryBook

/**
 * A platform-agnostic wrapper for the reading experience.
 * - Android: Hosts Readium Kotlin VisualNavigator
 * - Desktop: Hosts a WebView with Readium TS-toolkit
 * - iOS: Hosts Readium Swift Navigator
 */
@Composable
expect fun PublicationReader(
    book: LibraryBook,
    modifier: Modifier = Modifier,
    onProgressChanged: (Double, String?) -> Unit,
    onToggleControls: () -> Unit,
    setNavigationCallback: (((String) -> Unit)?) -> Unit = {}
)
