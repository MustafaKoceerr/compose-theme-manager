package com.mustafakoceerr.thememanager.buildlogic

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal val JAVA_VERSION = JavaVersion.VERSION_17

/** Configures AGP's built-in Kotlin. */
internal fun Project.configureKotlin(explicitApi: Boolean) {
    extensions.configure<KotlinAndroidProjectExtension> {
        if (explicitApi) explicitApi()
        compilerOptions {
            jvmTarget.set(JvmTarget.fromTarget(JAVA_VERSION.toString()))
        }
    }
}
