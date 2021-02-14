package dev.gressier.osp.commons.context

data class UserContext(
    val correlationId: String? = null,
    val authToken: String? = null,
    val userId: String? = null,
    val organizationId: String? = null,
) {
    companion object {
        val empty = UserContext()
    }

    object Header {
        const val correlationId = "osp-correlation-id"
        const val authToken = "osp-auth-token"
        const val userId = "osp-user-id"
        const val organizationId = "osp-organization-id"
    }

    fun isEmpty() = this == empty
}