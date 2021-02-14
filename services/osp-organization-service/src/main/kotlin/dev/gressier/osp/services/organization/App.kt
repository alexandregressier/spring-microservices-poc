package dev.gressier.osp.services.organization

import dev.gressier.osp.commons.context.UserContextFilter
import dev.gressier.osp.services.organization.config.Config
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean

@EnableDiscoveryClient
@EnableConfigurationProperties(Config::class)
@SpringBootApplication
class App {

    @Bean
    fun userContextFilter() = UserContextFilter()
}

fun main(args: Array<String>) {
    runApplication<App>(*args)
}