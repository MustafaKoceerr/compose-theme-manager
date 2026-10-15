plugins {
    alias(libs.plugins.thememanager.android.library)
    alias(libs.plugins.thememanager.android.compose)
    alias(libs.plugins.thememanager.publish)
    alias(libs.plugins.thememanager.robolectric)
}

android {
    namespace = "com.mustafakoceerr.thememanager.compose"
}

dependencies {
    api(project(":theme-manager"))
    api(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    testImplementation(project(":theme-manager-testing"))
    testImplementation(libs.junit)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.truth)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
