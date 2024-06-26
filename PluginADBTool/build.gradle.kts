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
    id("app.cash.sqldelight") version "2.0.1"
}

sqldelight {
    databases {
        create("ADBToolsDatabase") {
            packageName.set("com.kouqurong.plugin.database")
        }
    }
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.common)
    implementation(project(":PluginView"))
    implementation(libs.material3.desktop)
    implementation(libs.auto.service.annotations)
    implementation("app.cash.sqldelight:sqlite-driver:2.0.1")
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.1")
    kapt(libs.auto.service.processor)
    // https://mvnrepository.com/artifact/org.jetbrains.kotlinx/kotlinx-coroutines-swing
    implementation(libs.kotlinx.coroutines.swing)
    testImplementation(libs.junit)
}
