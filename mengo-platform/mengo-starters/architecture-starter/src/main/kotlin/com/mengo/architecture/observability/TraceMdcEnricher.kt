package com.mengo.architecture.observability

import io.micrometer.tracing.Tracer
import org.slf4j.MDC
import org.springframework.stereotype.Component

@Component
class TraceMdcEnricher(
    private val tracer: Tracer,
) {
    fun enrich() {
        val span = tracer.currentSpan()
        val traceId = span?.context()?.traceId() ?: "no-trace"
        MDC.put("traceId", traceId)
    }
}
