pluginManagement {
  repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/central")
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  includeBuild("build-logic")
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0" }

dependencyResolutionManagement {
  repositories {
    mavenCentral()
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/central")
    google()
    gradlePluginPortal()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }
}

rootProject.name = "DevTools"

include("PluginView")

include("PluginHex")

include("PluginTcpClient")

include("PluginTcpServer")

include("PluginBitSet")

include("PluginSignIn")

include("PluginISO8583")

include("ShardBitSet")

include("ShardUtils")
