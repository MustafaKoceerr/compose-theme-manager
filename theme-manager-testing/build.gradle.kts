plugins {
    alias(libs.plugins.thememanager.android.library)
    alias(libs.plugins.thememanager.publish)
}

android {
    namespace = "com.mustafakoceerr.thememanager.testing"
}

dependencies {
    api(project(":theme-manager"))

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
}
