package dev.gressier.osp.services.license.controller.client

import dev.gressier.osp.services.license.model.Organization
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import java.util.*

@FeignClient("organization-service")
interface OrganizationFeignClient {

    @GetMapping("organizations/{id}")
    fun getOrganization(@PathVariable id: UUID): Organization
}