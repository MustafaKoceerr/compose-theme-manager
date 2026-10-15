import com.android.build.api.dsl.ApplicationExtension
import com.mustafakoceerr.thememanager.buildlogic.JAVA_VERSION
import com.mustafakoceerr.thememanager.buildlogic.configureKotlin
import com.mustafakoceerr.thememanager.buildlogic.intVersion
import com.mustafakoceerr.thememanager.buildlogic.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

/** Android application module with the project's SDK levels and Java target. */
class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "com.android.application")

        extensions.configure<ApplicationExtension> {
            compileSdk = libs.intVersion("compileSdk")
            defaultConfig {
                minSdk = libs.intVersion("minSdk")
                targetSdk = libs.intVersion("targetSdk")
            }
            compileOptions {
                sourceCompatibility = JAVA_VERSION
                targetCompatibility = JAVA_VERSION
            }
        }
        configureKotlin(explicitApi = false)
    }
}
