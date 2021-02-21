package dev.gressier.osp.services.organization.message

import dev.gressier.osp.commons.context.UserContextHolder
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.messaging.Source
import org.springframework.messaging.support.MessageBuilder
import java.util.*

private val log = KotlinLogging.logger {}

data class OrganizationChangeMessage(
    val type: String,
    val change: Change,
    val organizationId: UUID,
    val correlationId: String,
) {
    enum class Change { CREATE, REPLACE, DELETE }
}

@EnableBinding(Source::class)
class OrganizationMessageSource {

    @Autowired private lateinit var source: Source

    fun source(change: OrganizationChangeMessage.Change, organizationId: UUID) {
        UserContextHolder.context.correlationId?.let {
            log.debug("Producing message - Change $change for Organization with ID = $organizationId ")
            source.output().send(
                MessageBuilder.withPayload(
                    OrganizationChangeMessage(
                        type = "OrganizationChangeMessage",
                        change = change,
                        organizationId = organizationId,
                        correlationId = it,
                    )
                ).build()
            )
        }
    }
}