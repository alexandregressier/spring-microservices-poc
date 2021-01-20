package dev.gressier.osp.services.organization.controller

import dev.gressier.osp.services.organization.model.Organization
import dev.gressier.osp.services.organization.repository.OrganizationRepository
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
@RequestMapping("/organizations")
class OrganizationController {

    @Autowired private lateinit var repository: OrganizationRepository
    @Autowired private lateinit var assembler: OrganizationModelAssembler

    @PostMapping
    fun createOrganization(@RequestBody organization: Organization): ResponseEntity<EntityModel<Organization>> {
        val model = assembler.toModel(repository.save(organization.copy(id = UUID.randomUUID())))
        return ResponseEntity
            .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(model)
    }

    @GetMapping
    fun getOrganizations(): CollectionModel<EntityModel<Organization>> =
        CollectionModel.of(
            repository.findAll().map(assembler::toModel),
            linkTo(methodOn(OrganizationController::class.java).getOrganizations()).withSelfRel(),
        )

    @GetMapping("/{organizationId}")
    fun getOrganization(@PathVariable organizationId: UUID): EntityModel<Organization> =
        repository.findById(organizationId).map(assembler::toModel)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

    @PutMapping("/{organizationId}")
    fun replaceOrganization(
        @PathVariable organizationId: UUID,
        @RequestBody newOrganization: Organization,
    ): ResponseEntity<EntityModel<Organization>> {

        val model = assembler.toModel(
            repository.findById(organizationId).map {
                repository.save(
                    Organization(
                        id = organizationId,
                        name = newOrganization.name,
                        contactName = newOrganization.contactName,
                        contactEmail = newOrganization.contactEmail,
                        contactPhone = newOrganization.contactPhone,
                    ))
            }.orElseGet {
                repository.save(newOrganization.copy(id = organizationId))
            })
        return ResponseEntity
            .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(model)
    }

    @DeleteMapping("/{organizationId}")
    fun deleteEmployee(@PathVariable organizationId: UUID): ResponseEntity<EntityModel<Organization>> {
        if (!repository.existsById(organizationId)) throw ResponseStatusException(HttpStatus.NOT_FOUND)
        repository.deleteById(organizationId)
        return ResponseEntity.noContent().build()
    }
}