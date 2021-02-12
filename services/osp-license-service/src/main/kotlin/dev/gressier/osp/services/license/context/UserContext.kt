package dev.gressier.osp.services.license.context

data class UserContext(
    val correlationId: String?,
    val authToken: String?,
    val userId: String?,
    val organizationId: String?,
) {
    object Header {
        const val correlationId = "osp-correlation-id"
        const val authToken = "osp-auth-token"
        const val userId = "osp-user-id"
        const val organizationId = "osp-organization-id"
    }
}