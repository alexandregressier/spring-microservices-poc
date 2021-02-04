package dev.gressier.osp.services.license.controller.client

import dev.gressier.osp.services.license.model.Organization
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.util.*

@Component
class OrganizationRestClient {

    @Autowired private lateinit var restTemplate: RestTemplate

    fun getOrganization(id: UUID): Organization? =
        restTemplate.getForObject("http://organization-service/organizations/{id}", Organization::class.java, id)
}