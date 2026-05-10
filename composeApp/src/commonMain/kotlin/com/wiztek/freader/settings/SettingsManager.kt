package com.wiztek.freader.settings

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object SettingsManager {
    private var persistence: SettingsPersistence? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val _settings = MutableStateFlow(ReaderSettings())
    val settings: StateFlow<ReaderSettings> = _settings.asStateFlow()

    fun init(persistence: SettingsPersistence) {
        this.persistence = persistence
        persistence.loadSettings()?.let { loaded ->
            _settings.value = loaded
        }
    }

    private fun save() {
        persistence?.saveSettings(_settings.value)
    }

    fun toggleDarkMode() {
        _settings.update { it.copy(isDarkMode = !it.isDarkMode) }
        save()
    }

    fun setDarkMode(enabled: Boolean) {
        _settings.update { it.copy(isDarkMode = enabled) }
        save()
    }

    fun setFontScaling(scale: Float) {
        _settings.update { it.copy(fontScaling = scale) }
        save()
    }

    fun setPageTurnAnimation(enabled: Boolean) {
        _settings.update { it.copy(pageTurnAnimation = enabled) }
        save()
    }

    fun setTheme(theme: ReaderTheme) {
        _settings.update { it.copy(theme = theme) }
        save()
    }
}
