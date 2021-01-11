import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
}

group = "dev.gressier.osp"
version = "0.1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

extra["springCloudVersion"] = "2020.0.0"

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Ops
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Spring Cloud Config
    implementation("org.springframework.cloud:spring-cloud-config-server")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "${java.sourceCompatibility}"
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootBuildImage>("bootBuildImage") {
    runImage = "gressier/${project.name}:${project.version}"
}