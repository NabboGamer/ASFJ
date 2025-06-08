plugins {
    application
}

dependencies {
    implementation(project(":firewall-api"))
    implementation(project(":firewall-core"))
    implementation("com.google.inject:guice:7.0.0")
    
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("it.unibas.softwarefirewall.clientsimulator.ClientSimulatorMain")
}

tasks.test {
    useJUnitPlatform()
}
