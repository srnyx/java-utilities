import xyz.srnyx.gradlegalaxy.data.config.JavaSetupConfig
import xyz.srnyx.gradlegalaxy.data.config.publishing.publishingSimpleConfig
import xyz.srnyx.gradlegalaxy.data.pom.DeveloperData
import xyz.srnyx.gradlegalaxy.data.pom.LicenseData
import xyz.srnyx.gradlegalaxy.utility.setupJava
import xyz.srnyx.gradlegalaxy.utility.setupPublishingEnv


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "3.2.0"
    id("com.gradleup.shadow") version "9.5.1"
}

setupJava(JavaSetupConfig(
    group = "xyz.srnyx",
    description = "General Java utility library for srnyx's projects",
    javaVersion = JavaVersion.VERSION_1_8))

repositories.mavenCentral()
dependencies {
    compileOnly("com.google.code.gson:gson:2.3.1") // Use this specific version for Spigot
    compileOnly("org.jetbrains:annotations:26.1.0")
}

setupPublishingEnv(publishingSimpleConfig(
    artifactId = "java-utilities",
    url = "https://java-utilities.srnyx.com",
    licenses = listOf(LicenseData.MIT),
    developers = listOf(DeveloperData.srnyx)))
