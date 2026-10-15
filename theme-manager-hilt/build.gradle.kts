plugins {
    alias(libs.plugins.thememanager.android.library)
    alias(libs.plugins.thememanager.publish)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.mustafakoceerr.thememanager.hilt"
}

dependencies {
    api(project(":theme-manager"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
