plugins {
    id("java")
    id("com.github.ben-manes.versions") version "0.53.0"
    id("org.sonarqube") version "7.2.3.7755"
    id("jacoco")
    application
    checkstyle
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass = "hexlet.code.App"
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("info.picocli:picocli:4.7.7")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.8.9")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}

sonar {
    properties {
        property("sonar.projectKey", "RaM0x1010_java-project-71")
        property("sonar.organization", "ram0x1010")
    }
}

jacoco {
    toolVersion = "0.8.14"
    reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}