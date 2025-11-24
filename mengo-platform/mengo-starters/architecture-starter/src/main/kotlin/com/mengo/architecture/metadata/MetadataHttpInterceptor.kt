package com.mengo.architecture.metadata

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.UUID

@Component
class MetadataHttpInterceptor : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val attrs = mutableMapOf<String, String>()
        request.getHeader("x-forced-payment-outcome")?.let { attrs["forced-payment-outcome"] = it }

        val correlationId = request.getHeader("correlation-id") ?: UUID.randomUUID().toString()
        val causationId = request.getHeader("causation-id") ?: UUID.randomUUID().toString()

        val metadata = Metadata(correlationId = correlationId, causationId = causationId, attributes = attrs)
        MetadataContextHolder.set(metadata)
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        MetadataContextHolder.clear()
    }
}
