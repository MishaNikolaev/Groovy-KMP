import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.compose") version "1.6.10"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
            export("org.jetbrains.compose.foundation:foundation:1.6.10")
            export("org.jetbrains.compose.ui:ui:1.6.10")
            export("org.jetbrains.compose.material3:material3:1.6.10")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("org.jetbrains.compose.runtime:runtime:1.6.10")
                api("org.jetbrains.compose.foundation:foundation:1.6.10")
                api("org.jetbrains.compose.material3:material3:1.6.10")
                api("org.jetbrains.compose.ui:ui:1.6.10")
                implementation("org.jetbrains.compose.material:material-icons-extended:1.6.10")
                implementation("org.jetbrains.compose.components:components-resources:1.6.10")

                // Koin core
                implementation(libs.koin.core)

                // Ktor
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation("io.ktor:ktor-client-logging:2.3.7")
                // kotlinx.serialization
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                // SQLDelight
                implementation("app.cash.sqldelight:runtime:2.0.1")
                implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")
                // Coroutines
                implementation(libs.kotlinx.coroutines.core)

                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.materialIconsExtended)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("moe.tlaster:precompose:1.5.10")
                implementation("media.kamel:kamel-image:0.5.0")
                
                // Domain module only - remove data module dependencies
                implementation(project(":domain"))
                
                // Auth module - needed for navigation
                implementation(project(":presentation:screen:auth"))


            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        val androidMain by getting {
            dependencies {

                //ExoPlayer
                implementation("androidx.media3:media3-exoplayer:1.2.1")
                implementation("androidx.media3:media3-ui:1.2.1")

                implementation("io.coil-kt:coil-compose:2.4.0")
                implementation("io.ktor:ktor-client-okhttp:2.3.7")
                implementation("androidx.room:room-runtime:2.6.1")
                implementation("androidx.room:room-ktx:2.6.1")
                // Koin Android
                implementation("io.insert-koin:koin-android:3.5.0")
                // Android SDK
                implementation("androidx.core:core-ktx:1.12.0")
                implementation("androidx.appcompat:appcompat:1.6.1")
                implementation("androidx.media:media:1.7.0")
                implementation("androidx.palette:palette-ktx:1.0.0")
            }
        }
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:2.3.7")
                implementation("media.kamel:kamel-image:0.5.0")
            }
        }
        val iosTest by creating {
            dependsOn(commonTest)
        }
        val iosX64Main by getting {
            dependencies {
                implementation("media.kamel:kamel-image:0.5.0")
            }
        }
        val iosArm64Main by getting {
            dependencies {
                implementation("media.kamel:kamel-image:0.5.0")
            }
        }
        val iosSimulatorArm64Main by getting {
            dependencies {
                implementation("media.kamel:kamel-image:0.5.0")
            }
        }
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
    namespace = "com.nmichail.groovy_kmp"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
