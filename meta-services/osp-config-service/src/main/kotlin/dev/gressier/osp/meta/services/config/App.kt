package dev.gressier.osp.meta.services.config

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@EnableConfigServer
@SpringBootApplication
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}