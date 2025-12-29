package com.mengo.architecture.metadata

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import io.micrometer.tracing.Span
import io.micrometer.tracing.Tracer
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.header.Headers
import org.slf4j.MDC
import org.springframework.kafka.listener.RecordInterceptor
import java.nio.charset.StandardCharsets
import java.util.UUID

class MetadataRecordInterceptor(
    private val objectMapper: ObjectMapper,
    private val tracer: Tracer,
) : RecordInterceptor<Any, Any> {
    private val scopeThreadLocal = ThreadLocal<Tracer.SpanInScope?>()
    private val spanThreadLocal = ThreadLocal<Span?>()

    override fun intercept(
        record: ConsumerRecord<Any, Any>,
        consumer: Consumer<Any, Any>,
    ): ConsumerRecord<Any, Any> {
        val metadata = extractMetadata(record.headers())
        MetadataContextHolder.set(metadata)

        metadata.traceParent?.let { tp ->
            try {
                val parts = tp.split("-")
                val parentContext =
                    tracer
                        .traceContextBuilder()
                        .traceId(parts[1])
                        .spanId(parts[2])
                        .sampled(true)
                        .build()

                val span =
                    tracer
                        .spanBuilder()
                        .setParent(parentContext)
                        .name("kafka.consume")
                        .tag("kafka.topic", record.topic())
                        .start()

                val scope = tracer.withSpan(span)

                scopeThreadLocal.set(scope)
                spanThreadLocal.set(span)

                MDC.put("traceId", span.context().traceId())
            } catch (e: Exception) {
                // fallback silent
            }
        }
        return record
    }

    private fun extractMetadata(headers: Headers): Metadata {
        fun getString(key: String): String? = headers.lastHeader(key)?.value()?.toString(StandardCharsets.UTF_8)

        val messageId = getString("message-id") ?: error("Message id is lost")
        val correlationId = getString("correlation-id") ?: error("Correlation id is lost")
        val traceParent = getString("traceparent")
        val attrsJson = getString("meta-attributes")

        val attributes: Map<String, String> =
            if (attrsJson.isNullOrEmpty()) {
                emptyMap()
            } else {
                try {
                    objectMapper.readValue(attrsJson, object : TypeReference<Map<String, String>>() {})
                } catch (e: Exception) {
                    emptyMap()
                }
            }

        return Metadata(
            correlationId = UUID.fromString(correlationId),
            causationId = UUID.fromString(messageId),
            attributes = attributes,
            traceParent = traceParent,
        )
    }

    override fun afterRecord(
        record: ConsumerRecord<Any, Any>,
        consumer: Consumer<Any, Any>,
    ) {
        clean()
    }

    override fun success(
        record: ConsumerRecord<Any, Any>,
        consumer: Consumer<Any, Any>,
    ) {
        clean()
    }

    override fun failure(
        record: ConsumerRecord<Any, Any>,
        exception: Exception,
        consumer: Consumer<Any, Any>,
    ) {
        clean()
    }

    private fun clean() {
        MetadataContextHolder.clear()

        scopeThreadLocal.get()?.close()
        scopeThreadLocal.remove()

        spanThreadLocal.get()?.end()
        spanThreadLocal.remove()

        MDC.remove("traceId")
    }
}
