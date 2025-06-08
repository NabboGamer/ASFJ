plugins {
    application
}

dependencies {
    implementation(project(":firewall-api"))
    implementation(project(":firewall-core"))
    implementation("com.google.inject:guice:7.0.0")
    implementation("com.formdev:flatlaf:3.4")
}

application {
    mainClass.set("it.unibas.softwarefirewall.gui.Applicazione")
}
