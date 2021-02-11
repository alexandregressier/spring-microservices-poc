package dev.gressier.osp.services.license.controller.client

import dev.gressier.osp.services.license.model.Organization
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*

private val log = KotlinLogging.logger {}

@Component
class OrganizationRestClient {

    @Autowired private lateinit var restTemplate: RestTemplate

    @CircuitBreaker(name = "#root.methodName")
    fun getOrganization(id: UUID): Organization? {
        return restTemplate.getForObject("http://organization-service/organizations/{id}", Organization::class.java, id)
    }
}