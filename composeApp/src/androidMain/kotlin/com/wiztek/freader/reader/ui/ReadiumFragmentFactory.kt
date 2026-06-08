package com.wiztek.freader.reader.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import org.readium.r2.navigator.epub.EpubNavigatorFragment
import org.readium.r2.navigator.epub.EpubNavigatorFactory
import org.readium.r2.navigator.epub.EpubDefaults
import org.readium.r2.navigator.epub.EpubPreferences
import org.readium.r2.shared.publication.Publication
import org.readium.r2.shared.publication.Locator
import org.readium.r2.navigator.pdf.PdfNavigatorFragment
import org.readium.r2.navigator.pdf.PdfNavigatorFactory
import org.readium.adapter.pdfium.navigator.PdfiumEngineProvider
import org.readium.adapter.pdfium.navigator.PdfiumPreferences
import org.readium.r2.shared.ExperimentalReadiumApi

/**
 * A Factory that bridges the publication to the Navigator Fragment.
 */
@OptIn(ExperimentalReadiumApi::class)
class ReadiumFragmentFactory(
    private val publication: Publication,
    private val initialLocator: Locator? = null,
    private val listener: EpubNavigatorFragment.Listener? = null,
    private val pdfListener: PdfNavigatorFragment.Listener? = null
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
            PdfNavigatorFragment::class.java.name -> {
                val factory = PdfNavigatorFactory(
                    publication = publication,
                    pdfEngineProvider = PdfiumEngineProvider()
                )
                factory.createFragmentFactory(
                    initialLocator = initialLocator,
                    listener = pdfListener,
                    initialPreferences = PdfiumPreferences()
                ).instantiate(classLoader, className)
            }
            else -> super.instantiate(classLoader, className)
        }
    }
}
