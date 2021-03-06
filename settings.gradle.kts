rootProject.name = "optima-stock-platform"

include(
    "shared:osp-commons",

    "meta-services:osp-config-service",
    "meta-services:osp-eureka-service",
    "meta-services:osp-gateway-service",

    "services:osp-organization-service",
    "services:osp-license-service"
)

// Spring Snapshot Repository
pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        maven { url = uri("https://repo.spring.io/snapshot") }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.springframework.boot") {
                useModule("org.springframework.boot:spring-boot-gradle-plugin:${requested.version}")
            }
        }
    }
}