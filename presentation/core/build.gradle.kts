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
            baseName = "PresentationCore"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":domain"))
                implementation(project(":data:repository"))
                
                // Compose
                api("org.jetbrains.compose.runtime:runtime:1.6.10")
                api("org.jetbrains.compose.foundation:foundation:1.6.10")
                api("org.jetbrains.compose.material3:material3:1.6.10")
                api("org.jetbrains.compose.ui:ui:1.6.10")
                implementation("org.jetbrains.compose.material:material-icons-extended:1.6.10")
                
                // Koin
                implementation(libs.koin.core)
                
                // Coroutines
                implementation(libs.kotlinx.coroutines.core)
            }
        }
        
        val androidMain by getting {
            dependencies {
                // Android-specific dependencies if needed
            }
        }
        
        val iosMain by creating {
            dependsOn(commonMain)
        }
        
        val commonTest by getting {
            dependencies {
                // Test dependencies if needed
            }
        }
        
        val iosTest by creating {
            dependsOn(commonTest)
        }
        
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting

        iosX64Main.dependsOn(iosMain)
        iosArm64Main.dependsOn(iosMain)
        iosSimulatorArm64Main.dependsOn(iosMain)
        iosX64Test.dependsOn(iosTest)
        iosArm64Test.dependsOn(iosTest)
        iosSimulatorArm64Test.dependsOn(iosTest)
    }
}

android {
    namespace = "com.nmichail.groovy_kmp.presentation.core"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
} 