/*
 * Copyright 2023 The Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
  implementation(project(":ShardUtils"))
  implementation(libs.material3.desktop)
  implementation(libs.auto.service.annotations)
  kapt(libs.auto.service.processor)
  // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-swing
  implementation(libs.kotlinx.coroutines.swing)
}
