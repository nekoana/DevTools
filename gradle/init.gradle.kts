
val ktfmtVersion = "0.46"

initscript {
    val spotlessVersion = "6.22.0"

    repositories {
        mavenCentral()
    }

    dependencies {
        classpath("com.diffplug.spotless:spotless-plugin-gradle:$spotlessVersion")
    }
}

rootProject {
    subprojects {
        apply<com.diffplug.gradle.spotless.SpotlessPlugin>()
        extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            kotlin {
                // by default the target is every '.kt' and '.kts` file in the java sourcesets
                target("**/src/main/**/*.kt")
                targetExclude("**/build/**/*.kt")
                targetExclude("**/spotless/**")
                licenseHeaderFile(rootProject.file("spotless/copyright.kt"), "(package|import|open|module) ")
                ktfmt(ktfmtVersion)
            }
            format("kts") {
                target("**/*.kts")
                targetExclude("**/build/**/*.kts")
                // Look for the first line that doesn't have a block comment (assumed to be the license)
                licenseHeaderFile(rootProject.file("spotless/copyright.kts"), "(^(?![\\/ ]\\*).*$)")
            }
            format("xml") {
                target("**/*.xml")
                targetExclude("**/build/**/*.xml")
                // Look for the first XML tag that isn't a comment (<!--) or the xml declaration (<?xml)
                licenseHeaderFile(rootProject.file("spotless/copyright.xml"), "(<[^!?])")
            }
        }
    }
}
