plugins {
    java
    application
    id("com.gradleup.shadow") version "9.0.0-beta17"
}

application {
    mainClass.set("it.unibas.softwarefirewall.firewallgui.Application")
}

dependencies {
    implementation(project(":firewall-api"))
    implementation(project(":firewall-core"))
    implementation(project(":client-simulator"))
    implementation("com.google.inject:guice:7.0.0")
    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("com.formdev:flatlaf:3.6")
    implementation("com.formdev:flatlaf-intellij-themes:3.6")
    implementation("com.formdev:flatlaf-extras:3.6")
    implementation("com.github.Dansoftowner:jSystemThemeDetector:3.6")
    
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks.shadowJar {
    archiveBaseName.set("ASFJ")
    archiveVersion.set("1.0.0")
    // Leave blank to remove the "-all" at the end of the jar name, added by default by ShadowJar
    archiveClassifier.set("")
    destinationDirectory.set(file("$buildDir/../../../bin"))
}