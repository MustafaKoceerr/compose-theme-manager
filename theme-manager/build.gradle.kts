plugins {
    alias(libs.plugins.thememanager.android.library)
    alias(libs.plugins.thememanager.publish)
}

android {
    namespace = "com.mustafakoceerr.thememanager"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    api(libs.kotlinx.coroutines.android)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
}
