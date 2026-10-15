plugins {
    alias(libs.plugins.thememanager.android.application)
    alias(libs.plugins.thememanager.android.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
}

android {
    namespace = "com.mustafakoceerr.thememanager.sample"

    defaultConfig {
        applicationId = "com.mustafakoceerr.thememanager.sample"
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":theme-manager-compose"))
    implementation(project(":theme-manager-hilt"))

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    testImplementation(project(":theme-manager-testing"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
}
