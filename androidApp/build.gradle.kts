plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("kotlin-kapt")
}

android {
    namespace = "com.nmichail.groovy_kmp.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.nmichail.groovy_kmp.android"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "SERVER_HOST", "\"10.0.2.2\"")
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.12"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            buildConfigField("String", "SERVER_HOST", "\"192.168.0.10\"")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(projects.domain)
    
    // Data modules - add directly since they were removed from shared
    implementation(projects.data.core)
    implementation(projects.data.remote)
    implementation(projects.data.local)
    implementation(projects.data.repository)
    implementation(projects.data.manager)
    
    // Feature modules
    implementation(projects.feature.core)
    implementation(projects.feature.auth)
    
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    debugImplementation(libs.compose.ui.tooling)
    // Koin Android
    implementation("io.insert-koin:koin-android:3.5.0")
    // Ktor OkHttp engine
    implementation("io.ktor:ktor-client-okhttp:2.3.7")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    // Palette for color extraction
    implementation("androidx.palette:palette-ktx:1.0.0")
    // MediaSession for music controls
    implementation("androidx.media:media:1.7.0")
    implementation("androidx.media3:media3-session:1.2.1")

    implementation("androidx.palette:palette-ktx:1.0.0")
}