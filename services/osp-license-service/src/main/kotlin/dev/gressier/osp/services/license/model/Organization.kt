package dev.gressier.osp.services.license.model

import org.springframework.hateoas.RepresentationModel
import java.util.*
import javax.persistence.Id

data class Organization(
    @Id val id: UUID?,
    val name: String,
    val contactName: String,
    val contactEmail: String,
    val contactPhone: String,
) : RepresentationModel<Organization>()