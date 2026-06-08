package com.wiztek.freader.reader.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentActivity
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.shared.publication.Publication

@Composable
fun ReadiumFragmentRenderer(
    publication: Publication,
    fragmentActivity: FragmentActivity,
    modifier: Modifier = Modifier
) {
    val containerId = android.view.View.generateViewId()

    AndroidView(
        modifier = modifier,
        factory = { context ->
            FragmentContainerView(context).apply {
                id = containerId
            }
        },
        update = { container ->
            val fragmentManager = fragmentActivity.supportFragmentManager
            if (fragmentManager.findFragmentById(containerId) == null) {
                fragmentManager.beginTransaction()
                    .replace(containerId, EpubNavigatorFragment::class.java, null)
                    .commit()
            }
        }
    )
}
