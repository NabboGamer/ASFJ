plugins {
    java
}

dependencies {
    implementation(project(":firewall-api"))
    implementation(project(":firewall-core"))
    implementation("com.google.inject:guice:7.0.0")
    implementation("org.slf4j:slf4j-api:2.1.0-alpha1")
    implementation("ch.qos.logback:logback-classic:1.5.18")   
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks.test {
    useJUnitPlatform()
}
