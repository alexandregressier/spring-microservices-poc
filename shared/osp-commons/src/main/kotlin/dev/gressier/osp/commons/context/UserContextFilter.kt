package dev.gressier.osp.commons.context

import mu.KotlinLogging
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

private val log = KotlinLogging.logger {}

class UserContextFilter : Filter {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        if (request is HttpServletRequest)
            UserContextHolder.context = UserContext(
                request.getHeader(UserContext.Header.correlationId),
            )
        UserContextHolder.context.let { if (!it.isEmpty()) log.debug("User context: $it") }
        chain?.doFilter(request, response)
    }
}