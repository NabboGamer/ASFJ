plugins {
    java
}

dependencies {
    implementation(project(":firewall-api"))
    implementation("com.google.inject:guice:7.0.0")
    implementation("com.github.seancfoley:ipaddress:5.5.1")
    
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
}
