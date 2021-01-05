package dev.gressier.osp.services.license.model

import org.springframework.hateoas.RepresentationModel
import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class License(
    @Id @GeneratedValue val id: UUID?,
    val productName: String,
    val description: String,
    val type: Type,
) : RepresentationModel<License>() {

    enum class Type { FREE, STANDARD, ENTERPRISE }
}

class LicenseNotFoundException(id: UUID) :
    RuntimeException("Could not find License $id")