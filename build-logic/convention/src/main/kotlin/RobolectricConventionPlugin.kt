import com.android.build.api.dsl.LibraryExtension
import com.mustafakoceerr.thememanager.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

/**
 * Robolectric unit tests for an Android library module: test options and the Robolectric/AndroidX Test dependencies.
 * Apply only to modules that run Robolectric tests; `isIncludeAndroidResources` makes Gradle expect tests.
 */
class RobolectricConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.withPlugin("com.android.library") {
            extensions.configure<LibraryExtension> {
                testOptions {
                    unitTests.isIncludeAndroidResources = true
                    unitTests.all {
                        // Robolectric's SDK 36 runtime reflects into JDK internals.
                        it.jvmArgs("--add-opens=java.base/jdk.internal.access=ALL-UNNAMED")
                    }
                }
            }
        }
        dependencies {
            add("testImplementation", libs.findLibrary("robolectric").get())
            add("testImplementation", libs.findLibrary("androidx-test-core").get())
            add("testImplementation", libs.findLibrary("androidx-test-ext-junit").get())
        }
    }
}
