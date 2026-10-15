// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt.android) apply false
}

// Published coordinates, JitPack multi-module format: com.github.<user>.<repo>:<module>:<version>.
// Set here rather than in gradle.properties: JitPack passes -Pgroup/-Pversion, which would override
// gradle.properties values and publish under different coordinates.
allprojects {
    group = "com.github.MustafaKoceerr.compose-theme-manager"
    version = "2.0.0"
}
