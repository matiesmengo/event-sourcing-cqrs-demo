package com.mengo.architecture.metadata

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.consumer.ConsumerInterceptor
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.header.Headers
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.UUID

@Component
class MetadataConsumerInterceptor : ConsumerInterceptor<String, Any> {
    private lateinit var objectMapper: ObjectMapper

    override fun configure(configs: MutableMap<String, *>?) {
        objectMapper = ObjectMapper().findAndRegisterModules()
    }

    override fun onConsume(records: ConsumerRecords<String, Any>): ConsumerRecords<String, Any> {
        records.forEach { record ->
            val headers = record.headers()
            val metadata = extractMetadata(headers)
            MetadataContextHolder.set(metadata)
        }
        return records
    }

    override fun onCommit(offsets: Map<TopicPartition, OffsetAndMetadata>) = Unit

    override fun close() = Unit

    private fun extractMetadata(headers: Headers): Metadata {
        fun getString(key: String): String? = headers.lastHeader(key)?.value()?.toString(StandardCharsets.UTF_8)

        val correlationId = getString("correlation-id") ?: error("Correlation id is lost")
        val causationId = getString("message-id")
        val traceParent = getString("traceparent")
        val attributesJson = getString("meta-attributes")
        val attributes: Map<String, String> =
            if (attributesJson.isNullOrEmpty()) {
                emptyMap()
            } else {
                try {
                    objectMapper.readValue(
                        attributesJson,
                        object : TypeReference<Map<String, String>>() {},
                    )
                } catch (_: Exception) {
                    emptyMap()
                }
            }

        return Metadata(
            correlationId = UUID.fromString(correlationId),
            causationId = UUID.fromString(causationId),
            attributes = attributes,
            traceParent = traceParent,
        )
    }
}
