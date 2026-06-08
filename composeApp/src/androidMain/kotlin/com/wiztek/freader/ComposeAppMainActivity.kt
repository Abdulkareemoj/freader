package com.wiztek.freader

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.wiztek.freader.di.composeAppAndroidModule
import com.wiztek.freader.di.commonAppModule
import com.wiztek.freader.settings.SettingsManager
import com.wiztek.freader.settings.SettingsPersistence
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class ComposeAppMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        startKoin {
            androidContext(this@ComposeAppMainActivity)
            modules(commonAppModule, composeAppAndroidModule)
        }

        // Initialize Settings
        SettingsManager.init(get<SettingsPersistence>())

        setContent {
            App()
        }
    }
}
