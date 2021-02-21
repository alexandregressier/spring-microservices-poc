package dev.gressier.osp.services.license.message

import mu.KotlinLogging
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
import java.util.*

private val log = KotlinLogging.logger {}

data class OrganizationChangeMessage(
    val type: String,
    val change: OrganizationChangeMessage.Change,
    val organizationId: UUID,
    val correlationId: String,
) {
    enum class Change { CREATE, REPLACE, DELETE }
}

@EnableBinding(Sink::class)
class OrganizationMessageSink {

    @StreamListener(Sink.INPUT)
    fun sink(message: OrganizationChangeMessage) {
        message.apply { log.debug("Consuming message - Change $change for Organization with ID = $organizationId ") }
    }
}