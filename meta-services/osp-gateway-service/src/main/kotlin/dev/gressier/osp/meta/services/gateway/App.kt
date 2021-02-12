package dev.gressier.osp.meta.services.gateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}