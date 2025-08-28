plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

kotlin {
    androidTarget()

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "DataCore"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.ktor.client.core)
                api(libs.ktor.client.content.negotiation)
                api(libs.ktor.serialization.kotlinx.json)
                api(libs.ktor.client.logging)
                
                // Serialization
                api(libs.kotlinx.serialization.json)
                
                // Coroutines
                api(libs.kotlinx.coroutines.core)
                
                // Koin
                api(libs.koin.core)
            }
        }
        
        val androidMain by getting {
            dependencies {
                api("io.ktor:ktor-client-okhttp:2.3.7")
                api("androidx.room:room-runtime:2.6.1")
                api("androidx.room:room-ktx:2.6.1")
            }
        }
        
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                api("io.ktor:ktor-client-darwin:2.3.7")
            }
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
    namespace = "com.nmichail.groovy_kmp.data.core"
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
