plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("org.jetbrains.compose") version "1.7.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
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
            baseName = "FeatureAuth"
            isStatic = true
            export("org.jetbrains.compose.foundation:foundation:1.7.3")
            export("org.jetbrains.compose.ui:ui:1.7.3")
            export("org.jetbrains.compose.material3:material3:1.7.3")
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":feature:core"))
                implementation(project(":domain"))
                
                // Compose dependencies
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.materialIconsExtended)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                
                // Coroutines
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                
                // Koin
                implementation("io.insert-koin:koin-core:3.5.0")
            }
        }
        
        val androidMain by getting {
            dependencies {
                // Android-specific dependencies if needed
                implementation("androidx.core:core-ktx:1.12.0")
                implementation("androidx.appcompat:appcompat:1.6.1")
                
                // Compose dependencies for Android
                implementation("androidx.compose.ui:ui:1.5.4")
                implementation("androidx.compose.ui:ui-tooling:1.5.4")
                implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
                implementation("androidx.compose.foundation:foundation:1.5.4")
                implementation("androidx.compose.material3:material3:1.1.2")
                implementation("androidx.compose.material:material-icons-extended:1.5.4")
            }
        }
        
        val androidUnitTest by getting {
            dependencies {
                // Android unit test dependencies if needed
                implementation("junit:junit:4.13.2")
                implementation("org.mockito:mockito-core:5.3.1")
            }
        }
        
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                // iOS-specific dependencies if needed
                implementation("org.jetbrains.compose.runtime:runtime:1.7.3")
            }
        }
        
        val commonTest by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-test:1.9.23")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
                implementation("org.jetbrains.kotlin:kotlin-test-junit:1.9.23")
            }
        }
        
        val iosTest by creating {
            dependsOn(commonTest)
            dependencies {
                // iOS test dependencies if needed
                implementation("org.jetbrains.kotlin:kotlin-test:1.9.23")
            }
        }
        
        val iosX64Main by getting {
            dependencies {
                // iOS-specific dependencies if needed
                implementation("org.jetbrains.compose.runtime:runtime:1.7.3")
            }
        }
        val iosArm64Main by getting {
            dependencies {
                // iOS-specific dependencies if needed
                implementation("org.jetbrains.compose.runtime:runtime:1.7.3")
            }
        }
        val iosSimulatorArm64Main by getting {
            dependencies {
                // iOS-specific dependencies if needed
                implementation("org.jetbrains.compose.runtime:runtime:1.7.3")
            }
        }
        val iosX64Test by getting {
            dependencies {
                // iOS test dependencies if needed
                implementation("org.jetbrains.compose.runtime:runtime:1.7.3")
            }
        }
        val iosArm64Test by getting {
            dependencies {
                // iOS test dependencies if needed
                implementation("org.jetbrains.compose.runtime:runtime:1.7.3")
            }
        }
        val iosSimulatorArm64Test by getting {
            dependencies {
                // iOS test dependencies if needed
                implementation("org.jetbrains.compose.runtime:runtime:1.7.3")
            }
        }

        iosX64Main.dependsOn(iosMain)
        iosArm64Main.dependsOn(iosMain)
        iosSimulatorArm64Main.dependsOn(iosMain)
        iosX64Test.dependsOn(iosTest)
        iosArm64Test.dependsOn(iosTest)
        iosSimulatorArm64Test.dependsOn(iosTest)
    }
}

android {
    namespace = "com.nmichail.groovy_kmp.feature.auth"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    
    sourceSets {
        getByName("main") {
            resources.srcDirs("src/commonMain/composeResources")
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
} 