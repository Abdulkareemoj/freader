package com.wiztek.freader

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.wiztek.freader.di.androidAppModule
import com.wiztek.freader.di.commonAppModule
import com.wiztek.freader.settings.SettingsManager
import com.wiztek.freader.settings.SettingsPersistence
import org.koin.android.ext.android.get
import org.koin.core.context.startKoin

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        startKoin {
            androidContext(this@MainActivity)
            modules(commonAppModule, androidAppModule)
        }

        // Initialize Settings
        SettingsManager.init(get<SettingsPersistence>())

        setContent {
            App()
        }
    }
}
