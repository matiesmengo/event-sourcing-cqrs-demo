package com.mengo.orchestrator.infrastructure.persist.outbox

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.architecture.metadata.Metadata
import com.mengo.architecture.metadata.MetadataContextHolder
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.orchestrator.domain.service.OutboxRepository
import com.mengo.orchestrator.infrastructure.events.mapper.toAvro
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
open class OutboxRepositoryService(
    private val repository: OutboxJpaRepository,
    private val objectMapper: ObjectMapper,
) : OutboxRepository {
    @Transactional
    override fun persistOutboxEvent(
        topic: String,
        payloadType: Class<*>,
        key: String?,
        message: SagaCommand,
    ) {
        val metadata =
            MetadataContextHolder.get()
                ?: error("MetadataContextHolder lost during Outbox persist")

        val payloadJson = message.toAvro().toString()
        val headersJson = objectMapper.writeValueAsString(metadata.toHeaderMap())

        repository.save(
            OutboxEntity(
                topic = topic,
                key = key,
                payloadType = payloadType.name,
                payload = payloadJson,
                headers = headersJson,
            ),
        )
    }

    private fun Metadata.toHeaderMap(): Map<String, String> =
        buildMap {
            put("message-id", UUID.randomUUID().toString())
            put("correlation-id", correlationId.toString())
            causationId?.let { put("causation-id", it.toString()) }
            if (attributes.isNotEmpty()) put("meta-attributes", objectMapper.writeValueAsString(attributes))
            traceParent?.let { put("traceparent", it) }
        }
}
