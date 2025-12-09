package com.mengo.orchestrator.infrastructure.persist.eventStore.mapper

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.orchestrator.domain.model.events.OrchestratorEvent
import com.mengo.orchestrator.infrastructure.persist.eventStore.OrchestratorEventEntity
import org.springframework.stereotype.Component

@Component
class OrchestratorEventEntityMapper(
    private val objectMapper: ObjectMapper,
) {
    fun toEntity(event: OrchestratorEvent): OrchestratorEventEntity =
        OrchestratorEventEntity(
            bookingId = event.bookingId,
            eventType = event::class.simpleName ?: "UnknownEvent",
            eventData = objectMapper.writeValueAsString(event),
            aggregateVersion = event.aggregateVersion,
        )

    fun toDomain(entity: OrchestratorEventEntity): OrchestratorEvent =
        objectMapper.readValue(entity.eventData, resolveDomainClass(entity.eventType))

    private fun resolveDomainClass(eventType: String): Class<out OrchestratorEvent> =
        when (eventType) {
            "Created" -> OrchestratorEvent.Created::class.java
            "ProductReserved" -> OrchestratorEvent.ProductReserved::class.java
            "ProductReservationFailed" -> OrchestratorEvent.ProductReservationFailed::class.java
            "CompensatedProduct" -> OrchestratorEvent.CompensatedProduct::class.java
            "PaymentCompleted" -> OrchestratorEvent.PaymentCompleted::class.java
            "PaymentFailed" -> OrchestratorEvent.PaymentFailed::class.java
            else -> throw IllegalArgumentException("Unknown event type: $eventType")
        }
}
