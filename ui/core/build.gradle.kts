plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "UiCore"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.compose.runtime:runtime:1.6.10")
                api("org.jetbrains.compose.foundation:foundation:1.6.10")
                api("org.jetbrains.compose.material3:material3:1.6.10")
                api("org.jetbrains.compose.ui:ui:1.6.10")
            }
        }
    }
}

android {
    namespace = "com.nmichail.groovy_kmp.ui.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}