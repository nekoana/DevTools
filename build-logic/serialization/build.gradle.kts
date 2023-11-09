plugins { `kotlin-dsl` }

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
// java {
//    sourceCompatibility = JavaVersion.VERSION_17
//    targetCompatibility = JavaVersion.VERSION_17
// }
// tasks.withType<KotlinCompile>().configureEach {
//    kotlinOptions {
//        jvmTarget = JavaVersion.VERSION_17.toString()
//    }
// }

dependencies { implementation(libs.kotlin.plugin.serilization) }

gradlePlugin {
  plugins {
    register("kotlinSerializationPlugin") {
      id = "kotlin.serialization.plugin"
      implementationClass = "KotlinSerializationPlugin"
    }
  }
}
