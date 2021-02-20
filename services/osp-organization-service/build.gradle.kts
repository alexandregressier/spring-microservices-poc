import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    // Kotlin
    kotlin("jvm")
    kotlin("kapt")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")

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

extra["springCloudVersion"] = "2020.0.1-SNAPSHOT"
extra["keycloakVersion"] = "12.0.3"

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // OSP Commons
    implementation(project(":shared:osp-commons"))

    // Ops
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Spring Cloud Config
    implementation("org.springframework.cloud:spring-cloud-starter-config")

    // Spring Cloud Discovery
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("com.github.ben-manes.caffeine:caffeine:2.8.8")

    // Spring Security
    implementation("org.springframework.boot:spring-boot-starter-security")

    // Keycloak
    implementation("org.keycloak:keycloak-spring-boot-starter")

    // Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-hateoas")

    // SQL
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")

    // Development
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // Logging
    implementation("io.github.microutils:kotlin-logging:2.0.4")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("org.keycloak.bom:keycloak-adapter-bom:${property("keycloakVersion")}")
    }
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

allOpen {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}