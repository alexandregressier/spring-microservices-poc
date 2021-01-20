package dev.gressier.osp.services.organization.controller

import dev.gressier.osp.services.organization.model.Organization
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class OrganizationModelAssembler : RepresentationModelAssembler<Organization, EntityModel<Organization>> {

    override fun toModel(organization: Organization): EntityModel<Organization> =
        EntityModel.of(
            organization,
            listOfNotNull(
                organization.id?.let { linkTo(methodOn(OrganizationController::class.java).getOrganization(it)).withSelfRel() },
                linkTo(methodOn(OrganizationController::class.java).getOrganizations()).withRel("all"),
            )
        )
}