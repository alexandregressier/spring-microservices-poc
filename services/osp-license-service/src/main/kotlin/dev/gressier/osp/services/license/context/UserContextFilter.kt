package dev.gressier.osp.services.license.context

import mu.KotlinLogging
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

private val log = KotlinLogging.logger {}

@Component
class UserContextFilter : Filter {

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
        if (request is HttpServletRequest)
            UserContextHolder.context = UserContext(
                request.getHeader(UserContext.Header.correlationId),
                request.getHeader(UserContext.Header.userId),
                request.getHeader(UserContext.Header.authToken),
                request.getHeader(UserContext.Header.organizationId),
            )
        log.debug("User context: ${UserContextHolder.context}")
        chain?.doFilter(request, response)
    }
}