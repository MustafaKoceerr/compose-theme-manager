import com.android.build.api.dsl.LibraryExtension
import com.mustafakoceerr.thememanager.buildlogic.JAVA_VERSION
import com.mustafakoceerr.thememanager.buildlogic.configureKotlin
import com.mustafakoceerr.thememanager.buildlogic.intVersion
import com.mustafakoceerr.thememanager.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/** Android library module with the project's SDK levels, Java target and explicit API mode. */
class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.library")

        extensions.configure<LibraryExtension> {
            compileSdk = libs.intVersion("compileSdk")
            defaultConfig {
                minSdk = libs.intVersion("minSdk")
            }
            compileOptions {
                sourceCompatibility = JAVA_VERSION
                targetCompatibility = JAVA_VERSION
            }
        }
        configureKotlin(explicitApi = true)
    }
}
