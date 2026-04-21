package com.wiztek.freader.settings

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class ReaderTheme { SEPIA, PAPER, SOLARIZED }

data class ReaderSettings(
    val isDarkMode: Boolean = true,
    val fontScaling: Float = 1.15f,
    val pageTurnAnimation: Boolean = true,
    val theme: ReaderTheme = ReaderTheme.PAPER
)

object SettingsManager {
    private val _settings = MutableStateFlow(ReaderSettings())
    val settings: StateFlow<ReaderSettings> = _settings.asStateFlow()

    fun toggleDarkMode() {
        _settings.update { it.copy(isDarkMode = !it.isDarkMode) }
    }

    fun setDarkMode(enabled: Boolean) {
        _settings.update { it.copy(isDarkMode = enabled) }
    }

    fun setFontScaling(scale: Float) {
        _settings.update { it.copy(fontScaling = scale) }
    }

    fun setPageTurnAnimation(enabled: Boolean) {
        _settings.update { it.copy(pageTurnAnimation = enabled) }
    }

    fun setTheme(theme: ReaderTheme) {
        _settings.update { it.copy(theme = theme) }
    }
}
