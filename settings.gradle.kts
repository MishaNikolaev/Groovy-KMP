enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

rootProject.name = "Groovy-KMP"

// App modules
include(":androidApp")
include(":iosApp")

// Core modules
include(":shared")
include(":domain")
include(":data:core")
include(":data:remote")
include(":data:local")
include(":data:repository")
include(":data:manager")

// Feature modules
include(":feature:core")
include(":feature:auth")
include(":feature:home")
include(":feature:player")
include(":feature:search")
include(":feature:profile")
include(":feature:favourite")
include(":feature:navigation")

// Feature modules
include(":feature:auth")
include(":feature:music")
include(":feature:player")
include(":feature:profile")

// Platform modules
include(":platform:android")
include(":platform:ios")

// UI modules
include(":ui:core")