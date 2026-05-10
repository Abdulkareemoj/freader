package com.wiztek.freader.settings

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

interface SettingsPersistence {
    fun saveSettings(settings: ReaderSettings)
    fun loadSettings(): ReaderSettings?
}

class SettingsPersistenceImpl(
    private val fileSystem: FileSystem,
    private val settingsPath: Path
) : SettingsPersistence {

    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true 
    }

    override fun saveSettings(settings: ReaderSettings) {
        try {
            val jsonString = json.encodeToString(settings)
            
            // Ensure parent directory exists
            settingsPath.parent?.let { parent ->
                if (!fileSystem.exists(parent)) {
                    fileSystem.createDirectories(parent)
                }
            }

            fileSystem.write(settingsPath) {
                writeUtf8(jsonString)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun loadSettings(): ReaderSettings? {
        return try {
            if (!fileSystem.exists(settingsPath)) return null
            val jsonString = fileSystem.read(settingsPath) {
                readUtf8()
            }
            json.decodeFromString<ReaderSettings>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
