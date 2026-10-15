import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register

/**
 * Publishes the release variant (with sources) of an Android library as `<group>:<project name>:<version>`.
 * Group and version are set in the root build script; JitPack builds this via `publishToMavenLocal`.
 */
class PublishConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        apply(plugin = "maven-publish")

        extensions.configure<LibraryExtension> {
            publishing {
                singleVariant("release") {
                    withSourcesJar()
                }
            }
        }
        extensions.configure<PublishingExtension> {
            publications {
                register<MavenPublication>("release") {
                    artifactId = project.name
                    afterEvaluate { from(components.getByName("release")) }
                }
            }
        }
    }
}
