package dev.gressier.osp.services.license.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("osp")
@ConstructorBinding
data class Config(
    val serviceClientType: ServiceClientType = ServiceClientType.REST,
) {
    enum class ServiceClientType { DISCOVERY, REST, FEIGN }
}