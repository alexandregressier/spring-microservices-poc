package dev.gressier.osp.commons.context

object UserContextHolder {

    private val localContext = object : ThreadLocal<UserContext>() {
        override fun initialValue() = UserContext.empty
    }

    var context: UserContext
        get() = localContext.get()
        set(context) = localContext.set(context)
}