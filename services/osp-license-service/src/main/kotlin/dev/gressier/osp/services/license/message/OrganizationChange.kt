package dev.gressier.osp.services.license.message

import dev.gressier.osp.services.license.message.OrganizationChangeMessage.Change.DELETE
import dev.gressier.osp.services.license.message.OrganizationChangeMessage.Change.REPLACE
import dev.gressier.osp.services.license.repository.OrganizationCacheRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.cloud.stream.messaging.Sink
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

@EnableBinding(Sink::class)
class OrganizationMessageSink {

    @Autowired
    private lateinit var organizationCacheRepository: OrganizationCacheRepository

    @StreamListener(Sink.INPUT)
    fun sink(message: OrganizationChangeMessage) {
        message.apply {
            log.debug("Consuming message - Change $change for Organization with ID = $organizationId")
            if (change == REPLACE || change == DELETE)
                organizationCacheRepository.apply {
                    if (existsById(organizationId)) {
                        deleteById(organizationId)
                        log.debug("Invalidating Organization with ID = $organizationId from cache")
                    }
                }
        }
    }
}