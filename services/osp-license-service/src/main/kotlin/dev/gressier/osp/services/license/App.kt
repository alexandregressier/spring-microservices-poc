package dev.gressier.osp.services.license

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.web.client.RestTemplate

@EnableFeignClients
@EnableDiscoveryClient
@ConfigurationPropertiesScan("dev.gressier.osp.services.license.config")
@SpringBootApplication
class App {

    @Bean
    @LoadBalanced
    fun restTemplate() = RestTemplate()
}

fun main(args: Array<String>) {
    runApplication<App>(*args)
}