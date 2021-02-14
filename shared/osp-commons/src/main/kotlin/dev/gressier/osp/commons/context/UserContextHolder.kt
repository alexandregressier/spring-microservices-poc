package dev.gressier.osp.commons.context

object UserContextHolder {

    private val localContext = ThreadLocal<UserContext>()

    var context: UserContext
        get() = localContext.get()
        set(context) = localContext.set(context)
}