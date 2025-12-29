package com.mengo.architecture.metadata

import io.micrometer.tracing.Tracer
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import java.util.UUID

@Component
class MetadataHttpInterceptor(
    private val tracer: Tracer,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val correlationId = request.getHeader("correlation-id") ?: UUID.randomUUID().toString()
        val causationId = request.getHeader("causationId-id") ?: UUID.randomUUID().toString()
        val traceParent =
            request.getHeader("traceparent")
                ?: tracer.currentSpan()?.context()?.let { ctx ->
                    "00-${ctx.traceId()}-${ctx.spanId()}-01"
                }

        val attrs = mutableMapOf<String, String>()
        request.getHeader("x-forced-payment-outcome")?.let { attrs["forced-payment-outcome"] = it }

        val metadata =
            Metadata(
                correlationId = UUID.fromString(correlationId),
                causationId = UUID.fromString(causationId),
                attributes = attrs,
                traceParent = traceParent,
            )
        MetadataContextHolder.set(metadata)
        traceParent?.let { MDC.put("traceId", it) }
        return true
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?,
    ) {
        MetadataContextHolder.clear()
        MDC.remove("traceId")
    }
}
