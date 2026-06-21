plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    android {
        namespace = "com.wiztek.freader.compose"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    jvm("desktop")

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.materialIconsExtended)
            
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenModel)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.koin)
            implementation(libs.koin.compose)
            
            implementation(libs.material.kolor)
            
            api(project(":shared"))
            
            implementation(libs.coil.compose)
            implementation(libs.coil.compose.core)
            implementation(libs.coil.mp)
            implementation(libs.koin.core.v421)
            implementation(libs.koin.compose)
                        implementation(libs.filekit.compose)
            implementation(libs.filekit.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.webview)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.appcompat)
                implementation(libs.readium.shared)
                implementation(libs.readium.streamer)
                implementation(libs.readium.navigator)
                implementation(libs.readium.adapter.pdfium)
                implementation(libs.koin.android.v421)
                implementation(libs.commons.compress)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.commons.compress)
                implementation(libs.kotlinx.coroutinesSwing)
                implementation(libs.kotlinx.datetime)
                
                // Ktor for local book streaming
                implementation(project.dependencies.platform("io.ktor:ktor-bom:2.3.12"))
                implementation("io.ktor:ktor-server-core")
                implementation("io.ktor:ktor-server-netty")
                implementation("io.ktor:ktor-server-cors")
                implementation("io.ktor:ktor-io")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "com.wiztek.freader.MainKt"
        jvmArgs += "--enable-native-access=ALL-UNNAMED"
    }
}
