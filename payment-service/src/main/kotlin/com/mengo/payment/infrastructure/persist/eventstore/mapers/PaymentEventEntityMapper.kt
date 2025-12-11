package com.mengo.payment.infrastructure.persist.eventstore.mapers

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.payment.domain.model.events.PaymentEvent
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventEntity
import org.springframework.stereotype.Component

@Component
class PaymentEventEntityMapper(
    private val objectMapper: ObjectMapper,
) {
    fun toEntity(event: PaymentEvent): PaymentEventEntity =
        PaymentEventEntity(
            paymentId = event.paymentId,
            eventType = event::class.simpleName ?: "UnknownEvent",
            eventData = objectMapper.writeValueAsString(event),
            aggregateVersion = event.aggregateVersion,
        )

    fun toDomain(entity: PaymentEventEntity): PaymentEvent = objectMapper.readValue(entity.eventData, resolveDomainClass(entity.eventType))

    private fun resolveDomainClass(eventType: String): Class<out PaymentEvent> =
        when (eventType) {
            "Initiated" -> PaymentEvent.Initiated::class.java
            "Completed" -> PaymentEvent.Completed::class.java
            "Failed" -> PaymentEvent.Failed::class.java
            else -> throw IllegalArgumentException("Unknown event type: $eventType")
        }
}
