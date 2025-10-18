package com.mengo.orchestrator.infrastructure.persist.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.infrastructure.persist.OrchestratorEventEntity
import org.springframework.stereotype.Component

@Component
class OrchestratorEventEntityMapper(
    private val objectMapper: ObjectMapper,
) {
    fun toEntity(
        event: OrchestratorEvent,
        version: Int,
    ): OrchestratorEventEntity =
        OrchestratorEventEntity(
            bookingId = event.bookingId,
            eventType = event::class.simpleName ?: "UnknownEvent",
            eventData = objectMapper.writeValueAsString(event),
            aggregateVersion = version,
        )

    fun toDomain(entity: OrchestratorEventEntity): OrchestratorEvent =
        objectMapper.readValue(entity.eventData, resolveDomainClass(entity.eventType))

    private fun resolveDomainClass(eventType: String): Class<out OrchestratorEvent> =
        when (eventType) {
            "Created" -> OrchestratorEvent.Created::class.java
            "WaitingStock" -> OrchestratorEvent.WaitingStock::class.java
            "WaitingPayment" -> OrchestratorEvent.WaitingPayment::class.java
            "Completed" -> OrchestratorEvent.Completed::class.java
            "Compensating" -> OrchestratorEvent.Compensating::class.java
            else -> throw IllegalArgumentException("Unknown event type: $eventType")
        }
}
