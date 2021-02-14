package dev.gressier.osp.services.license

import dev.gressier.osp.commons.context.UserContextFilter
import dev.gressier.osp.services.license.config.Config
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.core.registry.EntryAddedEvent
import io.github.resilience4j.core.registry.EntryRemovedEvent
import io.github.resilience4j.core.registry.EntryReplacedEvent
import io.github.resilience4j.core.registry.RegistryEventConsumer
import io.github.resilience4j.retry.Retry
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.web.client.RestTemplate

private val log = KotlinLogging.logger {}

@EnableFeignClients
@EnableDiscoveryClient
@EnableConfigurationProperties(Config::class)
@SpringBootApplication
class App {

    @Bean
    fun userContextFilter() = UserContextFilter()

    @LoadBalanced
    @Bean
    fun restTemplate() = RestTemplate()

    // Resilience4j

    @Profile("dev")
    @Bean
    fun customCircuitBreakerRegistryEventConsumer(): RegistryEventConsumer<CircuitBreaker> =
        object : RegistryEventConsumer<CircuitBreaker> {
            override fun onEntryAddedEvent(entryAddedEvent: EntryAddedEvent<CircuitBreaker>) =
                entryAddedEvent.addedEntry.eventPublisher.onEvent { log.debug("$it") }

            override fun onEntryRemovedEvent(entryRemoveEvent: EntryRemovedEvent<CircuitBreaker>) {}
            override fun onEntryReplacedEvent(entryReplacedEvent: EntryReplacedEvent<CircuitBreaker>) {}
        }

    @Profile("dev")
    @Bean
    fun customRetryRegistryEventConsumer(): RegistryEventConsumer<Retry> =
        object : RegistryEventConsumer<Retry> {
            override fun onEntryAddedEvent(entryAddedEvent: EntryAddedEvent<Retry>) =
                entryAddedEvent.addedEntry.eventPublisher.onEvent { log.debug("$it") }

            override fun onEntryRemovedEvent(entryRemoveEvent: EntryRemovedEvent<Retry>) {}
            override fun onEntryReplacedEvent(entryReplacedEvent: EntryReplacedEvent<Retry>) {}
        }
}

fun main(args: Array<String>) {
    runApplication<App>(*args)
}