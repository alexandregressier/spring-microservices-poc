package dev.gressier.osp.services.license

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@ConfigurationPropertiesScan("dev.gressier.osp.services.license.config")
@EnableDiscoveryClient
@SpringBootApplication
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}