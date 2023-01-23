import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
}

group = "com.oneeyedmen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.seleniumhq.selenium:selenium-java:4.0.0")
    implementation("io.github.bonigarcia:webdrivermanager:5.2.3")
    implementation("com.madgag:animated-gif-lib:1.4")
    testImplementation(kotlin("test"))
    testImplementation("com.oneeyedmen:okeydoke:1.3.3")
    testImplementation("org.junit:junit-bom:5.9.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
}