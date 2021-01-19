package dev.gressier.osp.meta.services.eureka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@EnableEurekaServer
@SpringBootApplication
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}