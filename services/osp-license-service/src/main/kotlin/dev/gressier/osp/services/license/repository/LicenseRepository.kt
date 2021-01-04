package dev.gressier.osp.services.license.repository

import dev.gressier.osp.services.license.model.License
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface LicenseRepository : JpaRepository<License, UUID>