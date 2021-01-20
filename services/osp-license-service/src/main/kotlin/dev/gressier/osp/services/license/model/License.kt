package dev.gressier.osp.services.license.model

import org.springframework.hateoas.RepresentationModel
import java.util.*
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id

@Entity
data class License(
    @Id val id: UUID?,
    val productName: String,
    val description: String,
    val comment: String?,
    @Enumerated(EnumType.STRING) val type: Type,
) : RepresentationModel<License>() {

    enum class Type { FREE, STANDARD, ENTERPRISE }
}