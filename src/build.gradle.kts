plugins {
    java
}

allprojects {
    group = "it.unibas.softwarefirewall"
    version = "0.1.0"

    repositories {
        mavenCentral()
        maven("https://jitpack.io")
    }
}

subprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}