import org.jetbrains.compose.desktop.application.dsl.TargetFormat

buildscript {
  repositories {
    mavenCentral()
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/central")
    gradlePluginPortal()
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
  implementation(project(":PluginHttpFileServer"))
  implementation(project(":PluginQRCode"))
  implementation(project(":PluginADBTool"))
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
