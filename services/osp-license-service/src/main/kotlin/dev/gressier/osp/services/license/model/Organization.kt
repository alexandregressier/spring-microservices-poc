package dev.gressier.osp.services.license.model

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import java.util.*

@RedisHash("organization")
data class Organization(
    @Id val id: UUID?,
    val name: String,
    val contactName: String,
    val contactEmail: String,
    val contactPhone: String,
)