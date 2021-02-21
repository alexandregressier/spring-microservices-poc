package dev.gressier.osp.commons.context

data class UserContext(
    val correlationId: String? = null,
) {
    companion object {
        val empty = UserContext()
    }

    object Header {
        const val CORRELATION_ID = "osp-correlation-id"
    }

    fun isEmpty() = this == empty
}