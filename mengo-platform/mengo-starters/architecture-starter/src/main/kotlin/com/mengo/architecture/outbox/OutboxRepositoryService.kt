package com.mengo.architecture.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.architecture.metadata.Metadata
import com.mengo.architecture.metadata.MetadataContextHolder
import org.apache.avro.specific.SpecificRecord
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

open class OutboxRepositoryService(
    private val repository: OutboxJpaRepository,
    private val objectMapper: ObjectMapper,
) : OutboxRepository {
    @Transactional
    override fun persistOutboxEvent(
        topic: String,
        key: String?,
        payloadJson: SpecificRecord,
    ) {
        val metadata =
            MetadataContextHolder.get()
                ?: error("MetadataContextHolder lost during Outbox persist")

        val headersJson = objectMapper.writeValueAsString(metadata.toHeaderMap())

        repository.save(
            OutboxEntity(
                topic = topic,
                key = key,
                payloadType = payloadJson::class.java.name,
                payload = payloadJson.toString(),
                headers = headersJson,
            ),
        )
    }

    // TODO: Move to Metadata directory
    private fun Metadata.toHeaderMap(): Map<String, String> =
        buildMap {
            put("message-id", UUID.randomUUID().toString())
            put("correlation-id", correlationId.toString())
            causationId?.let { put("causation-id", it.toString()) }
            if (attributes.isNotEmpty()) put("meta-attributes", objectMapper.writeValueAsString(attributes))
            traceParent?.let { put("traceparent", it) }
        }
}
