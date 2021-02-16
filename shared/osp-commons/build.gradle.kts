import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    // Kotlin
    kotlin("jvm")
    `java-library`

    // Spring
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "dev.gressier.osp"
version = "0.1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Spring Web
    implementation("org.springframework:spring-web")
    implementation("org.apache.tomcat.embed:tomcat-embed-core")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // Logging
    implementation("io.github.microutils:kotlin-logging:2.0.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "${java.sourceCompatibility}"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}