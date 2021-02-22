package dev.gressier.osp.services.license.repository

import dev.gressier.osp.services.license.model.Organization
import org.springframework.data.repository.CrudRepository
import java.util.*

interface OrganizationCacheRepository : CrudRepository<Organization, UUID>