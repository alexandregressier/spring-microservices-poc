package dev.gressier.osp.services.license.context

object UserContextHolder {

    private val localContext = ThreadLocal<UserContext>()

    var context: UserContext?
        get() = localContext.get()
        set(context) { context?.let { localContext.set(it) } }
}