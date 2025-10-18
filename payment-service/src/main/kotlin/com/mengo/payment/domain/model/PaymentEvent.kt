package com.mengo.payment.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

sealed interface PaymentEvent {
    val paymentId: UUID
    val bookingId: UUID
    val aggregateVersion: Int
    val createdAt: Instant
}

data class PaymentInitiatedEvent(
    override val paymentId: UUID = UUID.randomUUID(),
    override val bookingId: UUID,
    val totalPrice: BigDecimal,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : PaymentEvent

data class PaymentCompletedEvent(
    override val paymentId: UUID,
    override val bookingId: UUID,
    val reference: String,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : PaymentEvent

data class PaymentFailedEvent(
    override val paymentId: UUID,
    override val bookingId: UUID,
    val reason: String,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : PaymentEvent
