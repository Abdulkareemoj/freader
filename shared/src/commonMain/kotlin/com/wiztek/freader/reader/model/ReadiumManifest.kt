package com.wiztek.freader.reader.model

import kotlinx.serialization.Serializable

@Serializable
data class ReadiumManifest(
    val metadata: Metadata,
    val readingOrder: List<ManifestLink>,
    val resources: List<ManifestLink> = emptyList(),
    val toc: List<ManifestLink> = emptyList()
)

@Serializable
data class Metadata(
    val title: String
)

@Serializable
data class ManifestLink(
    val href: String,
    val type: String? = null,
    val title: String? = null,
    val children: List<ManifestLink> = emptyList()
)
