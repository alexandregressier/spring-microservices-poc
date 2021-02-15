package dev.gressier.osp.meta.services.gateway

import dev.gressier.osp.commons.context.UserContext
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.*

private val log = KotlinLogging.logger {}

@EnableDiscoveryClient
@SpringBootApplication
class App {

    @Bean
    @Order(1)
    fun correlationIdFilter() = GlobalFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
        exchange.request.headers.getFirst(UserContext.Header.correlationId)?.let {
            log.debug("Correlation Pre Filter - Found OSP correlation ID = $it")
        } ?: run {
            val uuid = UUID.randomUUID()
            exchange.mutate().request(
                exchange.request.mutate().header("osp-correlation-id", "$uuid").build()
            ).build()
            log.debug("Correlation Pre Filter - Generated OSP correlation ID = $uuid")
        }
        chain.filter(exchange).then(Mono.fromRunnable {
            exchange.request.headers[UserContext.Header.correlationId]?.first()?.let {
                exchange.response.headers.add(UserContext.Header.correlationId, it)
                log.debug("Correlation Post Filter - Returned OSP correlation ID = $it")
            }
        })
    }
}

fun main(args: Array<String>) {
    runApplication<App>(*args)
}