package com.wiztek.freader.ui.components.reader

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.wiztek.freader.library.model.LibraryBook
import com.wiztek.freader.reader.ui.ReadiumFragmentRenderer
import org.readium.r2.shared.publication.Publication
import org.readium.r2.streamer.PublicationOpener
import org.readium.r2.streamer.parser.DefaultPublicationParser
import org.readium.r2.shared.util.asset.AssetRetriever
import org.readium.r2.shared.util.AbsoluteUrl
import org.readium.r2.shared.util.Url
import org.readium.r2.shared.util.getOrElse
import java.io.File

@Composable
actual fun PublicationReader(
    book: LibraryBook,
    modifier: Modifier,
    onProgressChanged: (Double, String?) -> Unit,
    onToggleControls: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) { context.findActivity() }
    
    var publication by remember { mutableStateOf<Publication?>(null) }

    // Logic to open the publication using Readium Streamer
    LaunchedEffect(book.filePath) {
        val assetRetriever = AssetRetriever(context.contentResolver)
        val publicationParser = DefaultPublicationParser(context, assetRetriever = assetRetriever)
        val publicationOpener = PublicationOpener(publicationParser)

        val file = File(book.filePath)
        val url = Url(file.toURI().toURL().toString()) as? AbsoluteUrl ?: return@LaunchedEffect
        
        val asset = assetRetriever.retrieve(url).getOrElse { return@LaunchedEffect }
        publication = publicationOpener.open(asset, allowUserInteraction = false).getOrNull()
    }

    publication?.let { pub ->
        if (activity != null) {
            ReadiumFragmentRenderer(
                publication = pub,
                fragmentActivity = activity,
                modifier = modifier
            )
        }
    }
}

private fun Context.findActivity(): FragmentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is FragmentActivity) return context
        context = context.baseContext
    }
    return null
}
