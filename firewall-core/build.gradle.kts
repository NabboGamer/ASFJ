plugins {
    java
}

dependencies {
    implementation(project(":firewall-api"))
    implementation("com.google.inject:guice:7.0.0")
}
