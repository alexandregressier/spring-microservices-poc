package dev.gressier.osp.services.license.controller.client

import dev.gressier.osp.services.license.model.Organization
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*

@Component
class OrganizationDiscoveryClient {

    @Autowired
    private lateinit var discoveryClient: DiscoveryClient

    fun getOrganization(id: UUID): Optional<Organization> =
        discoveryClient.getInstances("organization-service").stream()
            .findFirst()
            .map { RestTemplate().getForObject("${it.uri}/organizations/{id}", Organization::class.java, id) }
}