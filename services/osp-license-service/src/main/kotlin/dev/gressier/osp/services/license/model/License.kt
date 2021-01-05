package dev.gressier.osp.services.license.model

import org.springframework.hateoas.RepresentationModel
import java.util.*
import javax.persistence.*

@Entity
data class License(
    @Id @GeneratedValue val id: UUID?,
    val productName: String,
    val description: String,
    @Enumerated(EnumType.STRING) val type: Type,
) : RepresentationModel<License>() {

    enum class Type { FREE, STANDARD, ENTERPRISE }
}