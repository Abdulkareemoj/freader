package com.wiztek.freader.settings

import kotlinx.serialization.Serializable

@Serializable
enum class ReaderTheme { SEPIA, PAPER, SOLARIZED }

@Serializable
data class ReaderSettings(
    val isDarkMode: Boolean = true,
    val fontScaling: Float = 1.15f,
    val pageTurnAnimation: Boolean = true,
    val theme: ReaderTheme = ReaderTheme.PAPER,
    val useTwoColumns: Boolean = true,
    val lineHeight: Float = 1.5f,
    val pageMargins: Float = 1.0f
)
