package dev.gressier.osp.services.license.controller

import dev.gressier.osp.services.license.model.License
import dev.gressier.osp.services.license.repository.LicenseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.hateoas.CollectionModel
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*

@RestController
@RequestMapping("/licenses")
class LicenseController {

    @Autowired
    private lateinit var repository: LicenseRepository

    @PostMapping
    fun createLicense(@RequestBody license: License): License =
        repository.save(license)

    @GetMapping
    fun getLicenses(): CollectionModel<EntityModel<License>> =
        CollectionModel.of(
            repository.findAll().map { license ->
                EntityModel.of(
                    license,
                    license.id?.let { linkTo(methodOn(LicenseController::class.java).getLicense(it)).withSelfRel() },
                    linkTo(methodOn(LicenseController::class.java).getLicenses()).withRel("all"),
                )
            },
            linkTo(methodOn(LicenseController::class.java).getLicenses()).withSelfRel(),
        )

    @GetMapping("/{licenseId}")
    fun getLicense(@PathVariable licenseId: UUID): EntityModel<License> =
        EntityModel.of(
            repository.findById(licenseId)
                .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) },

            linkTo(methodOn(LicenseController::class.java).getLicense(licenseId)).withSelfRel(),
            linkTo(methodOn(LicenseController::class.java).getLicenses()).withRel("all"),
        )

    @PutMapping("/{licenseId}")
    fun replaceLicense(@PathVariable licenseId: UUID, @RequestBody newLicense: License): License =
        repository.findById(licenseId).map {
            repository.save(
                License(
                    id = licenseId,
                    productName = newLicense.productName,
                    description = newLicense.description,
                    type = newLicense.type,
                )
            )
        }.orElseGet {
            repository.save(newLicense.copy(id = licenseId))
        }

    @DeleteMapping("/{licenseId}")
    fun deleteEmployee(@PathVariable licenseId: UUID): Unit =
        repository.deleteById(licenseId)
}