package dev.gressier.osp.meta.services.gateway

import com.nimbusds.jwt.JWTParser
import com.nimbusds.jwt.SignedJWT
import dev.gressier.osp.commons.context.UserContext
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.context.annotation.Bean
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
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
        exchange.request.headers.getFirst(UserContext.Header.CORRELATION_ID)?.let {
            log.debug("Correlation Pre Filter - Found OSP correlation ID = $it")
        } ?: run {
            val uuid = UUID.randomUUID()
            exchange.mutate().request(
                exchange.request.mutate().header("osp-correlation-id", "$uuid").build()
            ).build()
            log.debug("Correlation Pre Filter - Generated OSP correlation ID = $uuid")
        }
        chain.filter(exchange).then(Mono.fromRunnable {
            exchange.request.headers[UserContext.Header.CORRELATION_ID]?.first()?.let {
                exchange.response.headers.add(UserContext.Header.CORRELATION_ID, it)
                log.debug("Correlation Post Filter - Returned OSP correlation ID = $it")
            }
        })
    }

    @Bean
    @Order(2)
    fun usernameFilter() = GlobalFilter { exchange: ServerWebExchange, chain: GatewayFilterChain ->
        exchange.request.headers.getFirst(HttpHeaders.AUTHORIZATION)?.let {
            kotlin.runCatching {
                Regex("Bearer (\\S+)").find(it)?.destructured?.let { (accessToken) ->
                    (JWTParser.parse(accessToken) as? SignedJWT)?.run {
                        payload.toJSONObject()["preferred_username"] as? String
                            ?: throw ClassCastException("`preferred_username` is not a string")
                    } ?: throw Exception("Access token is unsigned")
                } ?: throw Exception("`Authorization` HTTP header is not prefixed with `Bearer `")
            }.fold(
                { username -> log.debug("Username Pre Filter - Authenticated request from $username") },
                { log.warn("Username Pre Filter - Unable to read username from access token", it) },
            )
        }
        chain.filter(exchange)
    }
}

fun main(args: Array<String>) {
    runApplication<App>(*args)
}