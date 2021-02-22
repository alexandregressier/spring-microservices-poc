package dev.gressier.osp.services.license.controller

import dev.gressier.osp.commons.context.UserContextHolder
import dev.gressier.osp.services.license.config.Config
import dev.gressier.osp.services.license.config.Config.ServiceClientType.*
import dev.gressier.osp.services.license.controller.client.OrganizationDiscoveryClient
import dev.gressier.osp.services.license.controller.client.OrganizationFeignClient
import dev.gressier.osp.services.license.controller.client.OrganizationRestClient
import dev.gressier.osp.services.license.model.License
import dev.gressier.osp.services.license.model.Organization
import dev.gressier.osp.services.license.repository.LicenseRepository
import dev.gressier.osp.services.license.repository.OrganizationCacheRepository
import mu.KotlinLogging
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

private val log = KotlinLogging.logger {}

@RestController
@RequestMapping("/organizations/{organizationId}/licenses")
class LicenseController {

    @Autowired private lateinit var config: Config
    @Autowired private lateinit var licenseRepository: LicenseRepository
    @Autowired private lateinit var licenseModelAssembler: LicenseModelAssembler
    @Autowired private lateinit var organizationDiscoveryClient: OrganizationDiscoveryClient
    @Autowired private lateinit var organizationRestClient: OrganizationRestClient
    @Autowired private lateinit var organizationFeignClient: OrganizationFeignClient
    @Autowired private lateinit var organizationCacheRepository: OrganizationCacheRepository

    @Value("\${example.property:None}") private lateinit var comment: String

    @PostMapping
    fun createLicense(
        @PathVariable organizationId: UUID,
        @RequestBody license: License,
    ): ResponseEntity<EntityModel<License>> {
        val model = licenseModelAssembler.toModel(
            licenseRepository.save(
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
            licenseRepository.findAll()
                .map { it.copy(organization = findOrganization(organizationId)) }
                .map(licenseModelAssembler::toModel),
            linkTo(methodOn(LicenseController::class.java).getLicenses(organizationId)).withSelfRel(),
        )

    @GetMapping("/{licenseId}")
    fun getLicense(@PathVariable organizationId: UUID, @PathVariable licenseId: UUID): EntityModel<License> {
        UserContextHolder.context.correlationId?.let { log.debug("GET License - OSP correlation ID = $it") }
        return licenseRepository.findById(licenseId)
            .map { it.copy(organization = findOrganization(organizationId)) }
            .map(licenseModelAssembler::toModel)
            .orElseThrow { throw ResponseStatusException(
                HttpStatus.NOT_FOUND, "License with id `$licenseId` not found"
            ) }
    }

    @PutMapping("/{licenseId}")
    fun replaceLicense(
        @PathVariable organizationId: UUID,
        @PathVariable licenseId: UUID,
        @RequestBody newLicense: License,
    ): ResponseEntity<EntityModel<License>> {
        val model = licenseModelAssembler.toModel(
            licenseRepository.findById(licenseId).map {
                licenseRepository.save(
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
                licenseRepository.save(newLicense.copy(id = licenseId))
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
        if (!licenseRepository.existsById(licenseId)) throw ResponseStatusException(
            HttpStatus.NOT_FOUND, "License with id `$licenseId` not found"
        )
        licenseRepository.deleteById(licenseId)
        return ResponseEntity.noContent().build()
    }

    private fun findOrganization(id: UUID): Organization? =
        organizationCacheRepository.findById(id)
            .map { it.apply { log.debug("Using cached Organization with ID = $id") } }
            .orElseGet {
                when (config.serviceClientType) {
                    DISCOVERY -> organizationDiscoveryClient.getOrganization(id)
                    REST -> organizationRestClient.getOrganization(id)
                    FEIGN -> organizationFeignClient.getOrganization(id)
                }
                    ?.apply(organizationCacheRepository::save)
            }
}