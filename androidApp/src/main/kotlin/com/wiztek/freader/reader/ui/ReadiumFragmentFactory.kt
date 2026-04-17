package com.wiztek.freader.reader.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.Locator
import org.readium.r2.shared.publication.epub.EpubLayout

/**
 * A Factory that bridges the publication to the Navigator Fragment.
 */
class ReadiumFragmentFactory(
    private val publication: Publication,
    private val initialLocator: Locator? = null,
    private val listener: EpubNavigatorFragment.Listener? = null
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            EpubNavigatorFragment::class.java.name -> {
                val factory = EpubNavigatorFactory(
                    publication = publication,
                    configuration = EpubNavigatorFactory.Configuration(
                        defaults = EpubDefaults()
                    )
                )
                factory.createFragmentFactory(
                    initialLocator = initialLocator,
                    listener = listener,
                    initialPreferences = EpubPreferences()
                ).instantiate(classLoader, className)
            }
            else -> super.instantiate(classLoader, className)
        }
    }
}
