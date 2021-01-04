package dev.gressier.osp.services.license.controller

import dev.gressier.osp.services.license.model.License
import dev.gressier.osp.services.license.repository.LicenseRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/licenses")
class LicenseController {

    @Autowired
    private lateinit var repository: LicenseRepository

    @PostMapping
    fun createLicense(@RequestBody license: License): License =
        repository.save(license)

    @GetMapping
    fun getLicense(): List<License> =
        repository.findAll()

    @GetMapping("/{licenseId}")
    fun getLicense(@PathVariable licenseId: UUID): Optional<License> =
        repository.findById(licenseId)

    @PutMapping("/{licenseId}")
    fun replaceLicense(@PathVariable licenseId: UUID, @RequestBody newLicense: License): License =
        repository.findById(licenseId).map {
            repository.save(
                License(
                    id = licenseId,
                    productName = newLicense.productName,
                    description = newLicense.description,
                    type = newLicense.type,
                ))
        }.orElseGet {
            repository.save(newLicense.copy(id = licenseId))
        }

    @DeleteMapping("/{licenseId}")
    fun deleteEmployee(@PathVariable licenseId: UUID): Unit =
        repository.deleteById(licenseId)
}