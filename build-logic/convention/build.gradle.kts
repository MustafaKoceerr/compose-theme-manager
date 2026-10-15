import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.mustafakoceerr.thememanager.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.thememanager.android.application.get().pluginId
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.thememanager.android.library.get().pluginId
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.thememanager.android.compose.get().pluginId
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("robolectric") {
            id = libs.plugins.thememanager.robolectric.get().pluginId
            implementationClass = "RobolectricConventionPlugin"
        }
        register("publish") {
            id = libs.plugins.thememanager.publish.get().pluginId
            implementationClass = "PublishConventionPlugin"
        }
    }
}
