package dev.gressier.osp.commons.context

import mu.KotlinLogging
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse

private val log = KotlinLogging.logger {}

class UserContextInterceptor: ClientHttpRequestInterceptor {

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution,
    ): ClientHttpResponse {
        request.headers.apply {
            UserContextHolder.context.correlationId?.let {
                log.debug("Propagating OSP correlation ID = $it")
                add(UserContext.Header.correlationId, it)
            }
            UserContextHolder.context.authToken?.let {
                log.debug("Propagating OSP auth token = $it")
                add(UserContext.Header.authToken, it)
            }
        }
        return execution.execute(request, body)
    }
}