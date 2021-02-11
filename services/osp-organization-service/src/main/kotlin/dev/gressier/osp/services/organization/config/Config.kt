package dev.gressier.osp.services.organization.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("osp")
@ConstructorBinding
data class Config(
    val getOrganizationTimeOutOnceIn: Int?,
)