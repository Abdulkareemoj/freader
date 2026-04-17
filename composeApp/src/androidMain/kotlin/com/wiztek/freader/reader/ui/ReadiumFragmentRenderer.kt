package com.wiztek.freader.reader.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentActivity
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.shared.publication.Publication

/**
 * A Composable that hosts a Readium Fragment inside a Compose UI.
 */
@Composable
fun ReadiumFragmentRenderer(
    publication: Publication,
    fragmentActivity: FragmentActivity, // Need this to manage the Fragment
    modifier: Modifier = Modifier
) {
    // We generate a unique ID for the container so FragmentManager can find it.
    val containerId = android.view.View.generateViewId()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            FragmentContainerView(context).apply {
                id = containerId
            }
        },
        update = { container ->
            // Use the activity's supportFragmentManager to add/replace the Readium fragment
            val fragmentManager = fragmentActivity.supportFragmentManager
            
            // Only add if not already added to avoid duplication on recomposition
            if (fragmentManager.findFragmentById(containerId) == null) {
                // This is a simplified setup, Readium's own TestApp 
                // uses a factory pattern to create these fragments.
                // For now, this establishes the container-to-fragment link.
                fragmentManager.beginTransaction()
                    .replace(containerId, EpubNavigatorFragment::class.java, null)
                    .commit()
            }
        }
    )
}
