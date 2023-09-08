import org.jetbrains.compose.desktop.application.dsl.TargetFormat

buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        gradlePluginPortal()
    }

    dependencies {
        classpath(libs.spotless.gradle)
    }
}

allprojects {
    repositories {
        mavenCentral()
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
    }

    apply(plugin = "com.diffplug.spotless")

    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        // configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        kotlin {
            // by default the target is every '.kt' and '.kts` file in the java sourcesets
            target("**/*.kt")
            targetExclude("$buildDir/**/*.kt")
            targetExclude("bin/**/*.kt")
            ktfmt() // has its own section below
        }
        kotlinGradle {
            target("*.gradle.kts") // default target for kotlinGradle
            ktlint() // or ktfmt() or prettier()
        }
    }

    tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
        dependsOn("spotlessApply")
    }
}

plugins {
    kotlin("jvm")
    alias(libs.plugins.jetbrains.compose)
}

group = "com.kouqurong.devtools"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(project(":PluginView"))
    implementation(project(":PluginHex"))
    implementation(project(":PluginTcpClient"))
    implementation(project(":PluginTcpServer"))
    implementation(project(":PluginBitSet"))
    implementation(project(":PluginSignIn"))
    implementation(project(":resource"))
    // https://mvnrepository.com/artifact/org.jetbrains.compose.material3/material3-desktop
    implementation(libs.material3.desktop)
    // https://mvnrepository.com/artifact/org.jetbrains.compose.material/material-icons-extended-desktop
    implementation(libs.material.icons.extended.desktop)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "DevTools"
            packageVersion = "1.0.0"

            macOS {
                iconFile.set(project.file("icon.svg"))
            }

            windows {
                iconFile.set(project.file("icon.svg"))
            }
        }
    }
}
