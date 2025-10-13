package com.mengo.payment.infrastructure.persist.eventstore.mapers

import com.fasterxml.jackson.databind.ObjectMapper
import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.infrastructure.persist.eventstore.PaymentEventEntity
import org.springframework.stereotype.Component

@Component
class PaymentEventEntityMapper(
    private val objectMapper: ObjectMapper,
) {
    fun toEntity(event: PaymentEvent): PaymentEventEntity =
        when (event) {
            is PaymentInitiatedEvent -> event.toEntity()
            is PaymentCompletedEvent -> event.toEntity()
            is PaymentFailedEvent -> event.toEntity()
        }

    fun PaymentInitiatedEvent.toEntity(): PaymentEventEntity =
        PaymentEventEntity(
            paymentId = this.paymentId,
            eventType = "PaymentInitiatedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = this.aggregateVersion,
            createdAt = this.createdAt,
        )

    fun PaymentCompletedEvent.toEntity(): PaymentEventEntity =
        PaymentEventEntity(
            paymentId = this.paymentId,
            eventType = "PaymentCompletedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = this.aggregateVersion,
            createdAt = this.createdAt,
        )

    fun PaymentFailedEvent.toEntity(): PaymentEventEntity =
        PaymentEventEntity(
            paymentId = this.paymentId,
            eventType = "PaymentFailedEvent",
            eventData = objectMapper.writeValueAsString(this),
            aggregateVersion = this.aggregateVersion,
            createdAt = this.createdAt,
        )

    fun toDomain(event: PaymentEventEntity): PaymentEvent =
        when (event.eventType) {
            "PaymentInitiatedEvent" -> objectMapper.readValue(event.eventData, PaymentInitiatedEvent::class.java)
            "PaymentCompletedEvent" -> objectMapper.readValue(event.eventData, PaymentCompletedEvent::class.java)
            "PaymentFailedEvent" -> objectMapper.readValue(event.eventData, PaymentFailedEvent::class.java)
            else -> throw IllegalArgumentException("Unknown PaymentEvent type: ${event.eventType}")
        }
}
