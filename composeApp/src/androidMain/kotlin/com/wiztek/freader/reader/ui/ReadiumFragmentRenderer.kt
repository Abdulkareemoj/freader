package com.wiztek.freader.reader.ui

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentActivity
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.Locator

import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.util.AbsoluteUrl
import android.net.Uri
import android.content.Intent
import org.readium.r2.navigator.input.InputListener
import org.readium.r2.navigator.input.TapEvent
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

/**
 * A Composable that hosts a Readium Fragment inside a Compose UI.
 */
@OptIn(ExperimentalReadiumApi::class)
@Composable
fun ReadiumFragmentRenderer(
    publication: Publication,
    fragmentActivity: FragmentActivity, // Need this to manage the Fragment
    onProgressChanged: (Double, String?) -> Unit,
    onToggleControls: () -> Unit,
    modifier: Modifier = Modifier
) {
    // We generate a unique ID for the container so FragmentManager can find it.
    val containerId = remember { android.view.View.generateViewId() }

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
                val listener = object : EpubNavigatorFragment.Listener {
                    @OptIn(ExperimentalReadiumApi::class)
                    override fun onExternalLinkActivated(url: AbsoluteUrl) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
                            fragmentActivity.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }

                fragmentManager.fragmentFactory = ReadiumFragmentFactory(
                    publication = publication,
                    listener = listener
                )

                fragmentManager.beginTransaction()
                    .replace(containerId, EpubNavigatorFragment::class.java, null)
                    .commitNow()
                
                // Set up progress reporting and input listener
                val navigatorFragment = fragmentManager.findFragmentById(containerId) as? EpubNavigatorFragment
                navigatorFragment?.let { navigator ->
                    navigator.lifecycleScope.launch {
                        navigator.currentLocator.collectLatest { locator ->
                            val progress = locator.locations.totalProgression ?: 0.0
                            onProgressChanged(progress, locator.toJSON().toString())
                        }
                    }

                    navigator.addInputListener(object : InputListener {
                        override fun onTap(event: TapEvent): Boolean {
                            onToggleControls()
                            return true
                        }
                    })
                }
            }
        }
    )
}
