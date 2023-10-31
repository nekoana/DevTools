plugins {
  kotlin("jvm")
  kotlin("kapt")
  id("org.jetbrains.compose")
}

dependencies {
  // Note, if you develop a library, you should use compose.desktop.common.
  // compose.desktop.currentOs should be used in launcher-sourceSet
  // (in a separate module for demo project and in testMain).
  // With compose.desktop.common you will also lose @Preview functionality
  implementation(compose.desktop.common)
  implementation(project(":PluginView"))
  implementation(project(":ShardBitSet"))
  implementation(project(":ShardUtils"))
  implementation(libs.material3.desktop)
  implementation(libs.auto.service.annotations)
  kapt(libs.auto.service.processor)
}
