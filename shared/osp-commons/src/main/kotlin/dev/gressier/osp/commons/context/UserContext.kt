package dev.gressier.osp.commons.context

data class UserContext(
    val correlationId: String?,
    val authToken: String?,
    val userId: String?,
    val organizationId: String?,
) {
    companion object {
        val empty = UserContext(null, null, null, null)
    }

    object Header {
        const val correlationId = "osp-correlation-id"
        const val authToken = "osp-auth-token"
        const val userId = "osp-user-id"
        const val organizationId = "osp-organization-id"
    }

    fun isEmpty() = this == empty
}