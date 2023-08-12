pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    plugins {
        kotlin("jvm").version(extra["kotlin.version"] as String)
        id("org.jetbrains.compose").version(extra["compose.version"] as String)
    }
}

rootProject.name = "DevTools"
include("PluginView")
include("PluginHello")
include("PluginHex")
include("core-util")
include("resource")
include("PluginTcpClient")
