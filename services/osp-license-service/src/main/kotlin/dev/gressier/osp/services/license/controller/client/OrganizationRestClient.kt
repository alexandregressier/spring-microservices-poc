package dev.gressier.osp.services.license.controller.client

import dev.gressier.osp.services.license.context.UserContextHolder
import dev.gressier.osp.services.license.model.Organization
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.ratelimiter.annotation.RateLimiter
import io.github.resilience4j.retry.annotation.Retry
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*

private val log = KotlinLogging.logger {}

@Component
class OrganizationRestClient {

    @Autowired private lateinit var restTemplate: RestTemplate

    @Retry(name = "#root.methodName", fallbackMethod = "buildBlankOrganization")
    @CircuitBreaker(name = "#root.methodName")
    @RateLimiter(name = "#root.methodName")
    @Bulkhead(name = "#root.methodName")
    fun getOrganization(id: UUID): Organization? {
        UserContextHolder.context.correlationId?.let { log.debug("GET Organization - OSP correlation ID = $it") }
        return restTemplate.getForObject("http://organization-service/organizations/{id}", Organization::class.java, id)
    }

    fun buildBlankOrganization(id: UUID, t: Throwable): Organization? =
        Organization(
            id = UUID(0, 0),
            name = "(Unavailable)",
            contactName = "No organization information is available at the moment",
            contactEmail = "",
            contactPhone = "",
        )
}