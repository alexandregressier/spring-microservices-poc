package dev.gressier.osp.services.license.controller

import dev.gressier.osp.services.license.model.License
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn
import org.springframework.stereotype.Component

@Component
class LicenseModelAssembler : RepresentationModelAssembler<License, EntityModel<License>> {

    override fun toModel(license: License): EntityModel<License> =
        EntityModel.of(
            license,
            listOfNotNull(
                license.id?.let { linkTo(methodOn(LicenseController::class.java).getLicense(it)).withSelfRel() },
                linkTo(methodOn(LicenseController::class.java).getLicenses()).withRel("all"),
            )
        )
}