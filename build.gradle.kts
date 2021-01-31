plugins {
    // Kotlin
    val kotlinVersion = "1.4.21"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("kapt") version kotlinVersion apply false
    kotlin("plugin.spring") version kotlinVersion apply false
    kotlin("plugin.jpa") version kotlinVersion apply false

    // Spring
    id("org.springframework.boot") version "2.4.3-SNAPSHOT" apply false
    id("io.spring.dependency-management") version "1.0.10.RELEASE" apply false
}