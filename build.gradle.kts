import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.data.pom.DeveloperData
import xyz.srnyx.gradlegalaxy.data.pom.LicenseData
import xyz.srnyx.gradlegalaxy.utility.setupJava
import xyz.srnyx.gradlegalaxy.utility.setupPublishing


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "2.0.2"
    id("com.gradleup.shadow") version "8.3.9"
}

setupJava(JavaSetupConfig(
    group = "xyz.srnyx",
    version = "2.0.0",
    description = "General Java utility library for srnyx's projects",
    javaVersion = JavaVersion.VERSION_1_8))

repositories.mavenCentral()
dependencies {
    compileOnly("org.jetbrains", "annotations", "26.0.2")
    compileOnly("com.google.code.gson", "gson", "2.3.1") // Use this specific version for Spigot
}

setupPublishing(
    artifactId = "java-utilities",
    url = "https://java-utilities.srnyx.com",
    licenses = listOf(LicenseData.MIT),
    developers = listOf(DeveloperData.srnyx))
