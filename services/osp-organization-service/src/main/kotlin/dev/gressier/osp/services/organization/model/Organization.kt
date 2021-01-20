package dev.gressier.osp.services.organization.model

import org.springframework.hateoas.RepresentationModel
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Organization(
    @Id val id: UUID?,
    val name: String,
    val contactName: String,
    val contactEmail: String,
    val contactPhone: String,
) : RepresentationModel<Organization>()