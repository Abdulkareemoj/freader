package com.wiztek.freader.reader.ui

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentActivity
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.pdf.PdfNavigatorFragment
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.Publication.Profile
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.data.ReadError
import org.readium.r2.shared.util.mediatype.MediaType
import org.json.JSONObject
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.util.AbsoluteUrl
import android.net.Uri
import android.content.Intent
import org.readium.r2.navigator.input.InputListener
import org.readium.r2.navigator.input.TapEvent
import kotlinx.coroutines.flow.collectLatest
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalReadiumApi::class)
@Composable
fun ReadiumFragmentRenderer(
    publication: Publication,
    fragmentActivity: FragmentActivity,
    onProgressChanged: (Double, String?) -> Unit,
    onToggleControls: () -> Unit,
    modifier: Modifier = Modifier,
    initialLocator: Locator? = null,
    setNavigationCallback: (((String) -> Unit)?) -> Unit = {}
) {
    val containerId = remember { android.view.View.generateViewId() }
    var navigatorFragment by remember { mutableStateOf<androidx.fragment.app.Fragment?>(null) }
    var fragmentError by remember { mutableStateOf<String?>(null) }
    var retryCount by remember { mutableStateOf(0) }

    LaunchedEffect(navigatorFragment, setNavigationCallback) {
        val nav = navigatorFragment
        if (nav != null) {
            setNavigationCallback { href ->
                val locator = if (href.startsWith("{")) {
                    try {
                        Locator.fromJSON(JSONObject(href))
                    } catch (_: Exception) { null }
                } else {
                    val url = Url(href) ?: return@setNavigationCallback
                    val link = publication.linkWithHref(url)
                    link?.let { publication.locatorFromLink(it) }
                        ?: Locator(href = url, mediaType = MediaType.HTML)
                }
                locator?.let {
                    when (nav) {
                        is EpubNavigatorFragment -> nav.go(it, animated = true)
                        is PdfNavigatorFragment<*, *> -> nav.go(it, animated = true)
                    }
                }
            }
        } else {
            setNavigationCallback(null)
        }
    }

    if (fragmentError != null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text(
                text = fragmentError ?: "Unknown error",
                modifier = Modifier.padding(32.dp),
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            FragmentContainerView(ctx).apply {
                id = containerId
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(
                    android.graphics.Color.parseColor("#FFF5F5F5")
                )
            }
        },
        update = { container ->
            val fm = fragmentActivity.supportFragmentManager
            val existing = fm.findFragmentById(containerId)

            when {
                existing != null && navigatorFragment == null -> {
                    navigatorFragment = existing
                    setupNavigatorListeners(existing, onProgressChanged, onToggleControls)
                }
                existing != null -> {
                    // already set up, nothing to do
                }
                else -> {
                    val isPdf = publication.metadata.conformsTo.contains(Profile.PDF) ||
                        publication.readingOrder.all { it.mediaType?.matches(MediaType.PDF) == true }

                    android.util.Log.d("FreaderFragmentRend", "Adding ${if (isPdf) "PDF" else "EPUB"} navigator")

                    val listener = object : EpubNavigatorFragment.Listener {
                        override fun onResourceLoadFailed(href: Url, error: ReadError) {
                            android.util.Log.w("FreaderFragmentRend", "Resource load failed: $href $error")
                        }
                        override fun onExternalLinkActivated(url: AbsoluteUrl) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
                            fragmentActivity.startActivity(intent)
                        }
                    }

                    val pdfListener = object : PdfNavigatorFragment.Listener {
                        override fun onResourceLoadFailed(href: Url, error: ReadError) {
                            android.util.Log.w("FreaderFragmentRend", "PDF resource load failed: $href $error")
                        }
                    }

                    fm.fragmentFactory = ReadiumFragmentFactory(
                        publication = publication,
                        initialLocator = initialLocator,
                        listener = listener,
                        pdfListener = pdfListener
                    )

                    val fragmentClass = if (isPdf) {
                        PdfNavigatorFragment::class.java
                    } else {
                        EpubNavigatorFragment::class.java
                    }

                    val retry = retryCount
                    Handler(Looper.getMainLooper()).post {
                        try {
                            fm.beginTransaction()
                                .replace(containerId, fragmentClass, null)
                                .commit()
                            fm.executePendingTransactions()

                            val nav = fm.findFragmentById(containerId)
                            if (nav != null) {
                                navigatorFragment = nav
                                setupNavigatorListeners(nav, onProgressChanged, onToggleControls)
                                android.util.Log.d("FreaderFragmentRend", "Navigator fragment attached")
                            } else if (retry < 3) {
                                retryCount = retry + 1
                                Handler(Looper.getMainLooper()).postDelayed({
                                    try {
                                        fm.beginTransaction()
                                            .replace(containerId, fragmentClass, null)
                                            .commitNow()
                                        val nav2 = fm.findFragmentById(containerId)
                                        if (nav2 != null) {
                                            navigatorFragment = nav2
                                            setupNavigatorListeners(nav2, onProgressChanged, onToggleControls)
                                        } else {
                                            fragmentError = "Failed to create reader view"
                                        }
                                    } catch (e: Exception) {
                                        fragmentError = "Fragment error: ${e.message}"
                                    }
                                }, 500)
                            } else {
                                fragmentError = "Failed to create reader view"
                            }
                        } catch (e: Exception) {
                            android.util.Log.e("FreaderFragmentRend", "Fragment transaction failed", e)
                            fragmentError = "Fragment error: ${e.message}"
                        }
                    }
                }
            }
        }
    )
}

private fun setupNavigatorListeners(
    navigator: androidx.fragment.app.Fragment,
    onProgressChanged: (Double, String?) -> Unit,
    onToggleControls: () -> Unit
) {
    when (navigator) {
        is EpubNavigatorFragment -> {
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
        is PdfNavigatorFragment<*, *> -> {
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
