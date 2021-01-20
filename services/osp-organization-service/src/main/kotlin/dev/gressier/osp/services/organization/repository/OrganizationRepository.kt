package dev.gressier.osp.services.organization.repository

import dev.gressier.osp.services.organization.model.Organization
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface OrganizationRepository : JpaRepository<Organization, UUID>