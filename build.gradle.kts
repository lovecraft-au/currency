import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.multiplatform.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.palantirGitVersion)
    alias(libs.plugins.maven.central.publish)
    id("maven-publish")
}

group = "au.lovecraft"

val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion().removePrefix("v")

kotlin {
    jvmToolchain(21)
    withSourcesJar()
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
        freeCompilerArgs.addAll(
            "-Xexpect-actual-classes",
            "-XXLanguage:+CustomEqualsInValueClasses"
        )
    }
    androidLibrary {
        namespace = "com.capy.budget.client.shared"
        compileSdk = 36
        minSdk = 29
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(projectDirPath)
                    }
                }
            }
        }
    }
    jvm()
    iosArm64()
    iosSimulatorArm64()
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.core)
            implementation(libs.decimal)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.serialization.json)
        }
        val jvmCommonMain by creating {
            dependsOn(commonMain.get())
        }
        androidMain.configure {
            dependsOn(jvmCommonMain)
        }
        jvmMain.configure {
            dependsOn(jvmCommonMain)
        }
        iosMain.configure {
            dependsOn(commonMain.get())
        }
        iosArm64Main {
            dependsOn(iosMain.get())
        }
        iosSimulatorArm64Main {
            dependsOn(iosMain.get())
        }
    }
}

mavenPublishing {
    publishToMavenCentral()
    if (System.getenv("GITHUB_ACTIONS") == "true") {
        signAllPublications()
    }

    coordinates(group.toString(), name, version.toString())

    pom {
        name.set("Currency")
        description.set("Kotlin Multiplatform library for representing international currencies with common financial operations.")
        inceptionYear.set("2025")
        url.set("https://github.com/lovecraft-au/currency")
        licenses {
            license {
                name.set("GNU Lesser General Public License")
                url.set("https://www.gnu.org/licenses/lgpl-3.0.html")
                distribution.set("https://www.gnu.org/licenses/lgpl-3.0.html")
            }
        }
        developers {
            developer {
                id.set("chris-hatton")
                name.set("Christopher Hatton")
                url.set("https://github.com/chris-hatton")
            }
            developer {
                id.set("darrencocco")
                name.set("Darren Cocco")
                url.set("https://github.com/darrencocco")
            }
        }
        scm {
            url.set("https://github.com/lovecraft-au/currency")
            connection.set("scm:git:git@github.com:lovecraft-au/currency.git")
            developerConnection.set("scm:git:ssh://git@github.com:lovecraft-au/currency.git")
        }
    }
}
