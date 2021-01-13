package dev.gressier.osp.services.license

import dev.gressier.osp.services.license.model.License
import dev.gressier.osp.services.license.repository.LicenseRepository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

private val log = KotlinLogging.logger {}

@Configuration
class LoadDatabase {

    @Value("\${example.property:None}") private lateinit var comment: String

    @Bean
    fun initDatabase(repository: LicenseRepository) = CommandLineRunner {
        log.info { "Preloading ${repository.save(
            License(
                id = UUID.fromString("bf0fbeac-19ba-452e-8510-32d0251c02f7"),
                productName = "IntelliJ IDEA",
                description = "The Capable & Ergonomic Java IDE",
                comment = comment,
                type = License.Type.ENTERPRISE,
            ))}"
        }
        log.info { "Preloading ${repository.save(
            License(
                id = UUID.fromString("03318621-8d08-4446-9e6c-8f5ef8146e65"),
                productName = "Sketch",
                description = "The digital design toolkit",
                comment = comment,
                type = License.Type.STANDARD,
            ))}"
        }
    }
}