// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.google.hilt) apply false
    alias(libs.plugins.compose.compiler) apply false
    id("com.github.ben-manes.versions") version "0.51.0"
    id("nl.littlerobots.version-catalog-update") version "0.8.4"
}

// Configure Java/Kotlin toolchain for subprojects to prefer Java 17 for compilation.
// This helps ensure Kotlin/Java compile tasks run against Java 17 even if the
// system JAVA_HOME points to a different JDK. Note: evaluation of Kotlin DSL
// scripts still uses the JVM running Gradle and may require setting JAVA_HOME
// or using a compatible Gradle JVM.
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

subprojects {
    // Configure Java toolchain
    plugins.withId("org.jetbrains.kotlin.jvm") {
        tasks.withType(KotlinCompile::class.java).configureEach {
            kotlinOptions.jvmTarget = "17"
        }
    }

    // Apply Java toolchain where the Java plugin is present
    plugins.withType(org.gradle.api.plugins.JavaPlugin::class.java) {
        extensions.configure(org.gradle.api.plugins.JavaPluginExtension::class.java) {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
            }
        }
    }

    // Ensure JavaCompile tasks target Java 17
    tasks.withType(org.gradle.api.tasks.compile.JavaCompile::class.java).configureEach {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}