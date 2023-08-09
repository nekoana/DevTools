import org.jetbrains.compose.desktop.application.dsl.TargetFormat

buildscript {
    repositories {
        maven("https://maven.aliyun.com/repository/public")
        maven("https://maven.aliyun.com/repository/central")
        gradlePluginPortal()
    }
}


plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
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
    implementation(project(":resource"))
    // https://mvnrepository.com/artifact/org.jetbrains.compose.material3/material3-desktop
    implementation(libs.material3.desktop)

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
        }
    }
}
