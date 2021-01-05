package dev.gressier.osp.services.license.controller

import dev.gressier.osp.services.license.model.License
import dev.gressier.osp.services.license.repository.LicenseRepository
import org.springframework.beans.factory.annotation.Autowired
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
@RequestMapping("/licenses")
class LicenseController {

    @Autowired private lateinit var repository: LicenseRepository
    @Autowired private lateinit var assembler: LicenseModelAssembler

    @PostMapping
    fun createLicense(@RequestBody license: License): ResponseEntity<EntityModel<License>> {
        val model = assembler.toModel(repository.save(license))
        return ResponseEntity
            .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(model)
    }

    @GetMapping
    fun getLicenses(): CollectionModel<EntityModel<License>> =
        CollectionModel.of(
            repository.findAll().map(assembler::toModel),
            linkTo(methodOn(LicenseController::class.java).getLicenses()).withSelfRel(),
        )

    @GetMapping("/{licenseId}")
    fun getLicense(@PathVariable licenseId: UUID): EntityModel<License> =
        repository.findById(licenseId).map(assembler::toModel)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

    @PutMapping("/{licenseId}")
    fun replaceLicense(
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
                        type = newLicense.type,
                    ))
            }.orElseGet {
                repository.save(newLicense.copy(id = licenseId))
            })
        return ResponseEntity
            .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(model)
    }

    @DeleteMapping("/{licenseId}")
    fun deleteEmployee(@PathVariable licenseId: UUID): ResponseEntity<EntityModel<License>> {
        repository.deleteById(licenseId)
        return ResponseEntity.noContent().build()
    }
}