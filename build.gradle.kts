import org.jetbrains.compose.desktop.application.dsl.TargetFormat

buildscript {
  repositories {
    mavenCentral()
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/central")
    gradlePluginPortal()
  }

  dependencies { classpath(libs.spotless.gradle) }
}

allprojects {
  apply<com.diffplug.gradle.spotless.SpotlessPlugin>()

  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      // by default the target is every '.kt' and '.kts` file in the java sourcesets
      target("**/*.kt")
      targetExclude("**/build/**/*.kt")
      targetExclude("**/spotless/**")
      licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(package|import|open|module) ")
      ktfmt()
    }
    kotlinGradle {
      target("**/*.kts") // default target for kotlinGradle
      targetExclude("**/build/**/*.kts")
      //            licenseHeaderFile(rootProject.file("spotless/copyright.kts"), "(^(?![\\/
      // ]\\*).*$)")
      ktfmt()
    }
    //        format("xml") {
    //            target("**/*.xml")
    //            targetExclude("**/build/**/*.xml")
    //            // Look for the first XML tag that isn't a comment (<!--) or the xml declaration
    // (<?xml)
    //            licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
    //        }
  }

  tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).configureEach {
    dependsOn("spotlessApply")
  }
}

plugins {
  alias(libs.plugins.jetbrains.compose)
  alias(libs.plugins.kotlin.jvm)
}

group = "com.kouqurong.devtools"

version = "1.0-SNAPSHOT"

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
  implementation(project(":PluginISO8583"))
  // https://mvnrepository.com/artifact/org.jetbrains.compose.material3/material3-desktop
  implementation(libs.material3.desktop)
  // https://mvnrepository.com/artifact/org.jetbrains.compose.material/material-icons-extended-desktop
  implementation(libs.material.icons.extended.desktop)
  implementation(libs.bundles.androix.datastore)
}

compose.desktop {
  application {
    mainClass = "com.kouqurong.devtools.MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
      packageName = "DevTools"
      packageVersion = "1.0.0"

      macOS { iconFile.set(project.file("icon.svg")) }

      windows { iconFile.set(project.file("icon.ico")) }
    }
  }
}
