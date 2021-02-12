package dev.gressier.osp.services.license.controller

import dev.gressier.osp.services.license.config.Config
import dev.gressier.osp.services.license.config.Config.ServiceClientType.*
import dev.gressier.osp.services.license.controller.client.OrganizationDiscoveryClient
import dev.gressier.osp.services.license.controller.client.OrganizationFeignClient
import dev.gressier.osp.services.license.controller.client.OrganizationRestClient
import dev.gressier.osp.services.license.model.License
import dev.gressier.osp.services.license.model.Organization
import dev.gressier.osp.services.license.repository.LicenseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.IanaLinkRelations
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/organizations/{organizationId}/licenses")
class LicenseController {

    @Autowired private lateinit var config: Config
    @Autowired private lateinit var repository: LicenseRepository
    @Autowired private lateinit var assembler: LicenseModelAssembler
    @Autowired private lateinit var organizationDiscoveryClient: OrganizationDiscoveryClient
    @Autowired private lateinit var organizationRestClient: OrganizationRestClient
    @Autowired private lateinit var organizationFeignClient: OrganizationFeignClient

    @Value("\${example.property:None}") private lateinit var comment: String

    @PostMapping
    fun createLicense(
        @PathVariable organizationId: UUID,
        @RequestBody license: License,
    ): ResponseEntity<EntityModel<License>> {
        val model = assembler.toModel(
            repository.save(
                license.copy(
                    id = UUID.randomUUID(),
                    comment = license.comment ?: comment,
                    organizationId = organizationId,
                )
            ).copy(organization = findOrganization(organizationId))
        )
        return ResponseEntity
            .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(model)
    }

    @GetMapping
    fun getLicenses(@PathVariable organizationId: UUID): CollectionModel<EntityModel<License>> =
        CollectionModel.of(
            repository.findAll()
                .map { it.copy(organization = findOrganization(organizationId)) }
                .map(assembler::toModel),
            linkTo(methodOn(LicenseController::class.java).getLicenses(organizationId)).withSelfRel(),
        )

    @GetMapping("/{licenseId}")
    fun getLicense(@PathVariable organizationId: UUID, @PathVariable licenseId: UUID): EntityModel<License> =
        repository.findById(licenseId)
            .map { it.copy(organization = findOrganization(organizationId)) }
            .map(assembler::toModel)
            .orElseThrow { throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "License with id `$licenseId` not found"
            ) }

    @PutMapping("/{licenseId}")
    fun replaceLicense(
        @PathVariable organizationId: UUID,
        @PathVariable licenseId: UUID,
        @RequestBody newLicense: License,
    ): ResponseEntity<EntityModel<License>> {
        val model = assembler.toModel(
            repository.findById(licenseId).map {
                repository.save(
                    License(
                        id = licenseId,
                        productName = newLicense.productName,
                        description = newLicense.description,
                        comment = newLicense.comment ?: comment,
                        type = newLicense.type,
                        organizationId = it.organizationId,
                    )
                ).copy(organization = findOrganization(organizationId))
            }.orElseGet {
                repository.save(newLicense.copy(id = licenseId))
                    .copy(organization = findOrganization(organizationId))
            })
        return ResponseEntity
            .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(model)
    }

    @DeleteMapping("/{licenseId}")
    fun deleteLicense(
        @PathVariable organizationId: UUID,
        @PathVariable licenseId: UUID,
    ): ResponseEntity<EntityModel<License>> {
        if (!repository.existsById(licenseId)) throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "License with id `$licenseId` not found"
        )
        repository.deleteById(licenseId)
        return ResponseEntity.noContent().build()
    }

    private fun findOrganization(id: UUID): Organization? =
        when (config.serviceClientType) {
            DISCOVERY -> organizationDiscoveryClient.getOrganization(id)
            REST -> organizationRestClient.getOrganization(id)
            FEIGN -> organizationFeignClient.getOrganization(id)
        }
}