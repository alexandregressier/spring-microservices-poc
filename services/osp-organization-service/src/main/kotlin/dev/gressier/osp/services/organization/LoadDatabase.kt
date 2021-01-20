package dev.gressier.osp.services.organization

import dev.gressier.osp.services.organization.model.Organization
import dev.gressier.osp.services.organization.repository.OrganizationRepository
import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

private val log = KotlinLogging.logger {}

@Configuration
class LoadDatabase {

    @Bean
    fun initDatabase(repository: OrganizationRepository) = CommandLineRunner {
        log.info { "Preloading ${repository.save(
            Organization(
                id = UUID.fromString("564ee6da-dfc2-4aa0-976f-a49a5650f57d"),
                name = "G Inc.",
                contactName = "Alexandre Gressier",
                contactEmail = "alexandre.gressier@example.com",
                contactPhone = "+33677889900",
            ))}"
        }
        log.info { "Preloading ${repository.save(
            Organization(
                id = UUID.fromString("4e979e21-3b14-488c-80d3-2fcacbf38755"),
                name = "Foobar LLC",
                contactName = "John Doe",
                contactEmail = "jdoe@example.net",
                contactPhone = "+1-202-555-0100",
            ))}"
        }
    }
}