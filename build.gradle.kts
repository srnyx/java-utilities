import xyz.srnyx.gradlegalaxy.data.pom.DeveloperData
import xyz.srnyx.gradlegalaxy.data.pom.LicenseData
import xyz.srnyx.gradlegalaxy.utility.setupJava
import xyz.srnyx.gradlegalaxy.utility.setupPublishing


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "1.1.3"
    id("io.github.goooler.shadow") version "8.1.7"
}

repositories.mavenCentral()
dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("com.google.code.gson:gson:2.3.1")
}

setupJava("xyz.srnyx", "1.0.0", "General Java utility library for srnyx's projects", JavaVersion.VERSION_1_8)
setupPublishing(
    artifactId = "java-utilities",
    url = "https://java-utilities.srnyx.com",
    licenses = listOf(LicenseData.MIT),
    developers = listOf(DeveloperData.srnyx))
