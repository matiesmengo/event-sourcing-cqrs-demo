package com.mengo.payment.domain.model.events

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

sealed class PaymentEvent {
    abstract val paymentId: UUID
    abstract val bookingId: UUID
    abstract val aggregateVersion: Int
    abstract val createdAt: Instant

    data class Initiated(
        override val paymentId: UUID,
        override val bookingId: UUID,
        val totalPrice: BigDecimal,
        override val aggregateVersion: Int,
        override val createdAt: Instant = Instant.now(),
    ) : PaymentEvent()

    data class Completed(
        override val paymentId: UUID,
        override val bookingId: UUID,
        val reference: String,
        override val aggregateVersion: Int,
        override val createdAt: Instant = Instant.now(),
    ) : PaymentEvent()

    data class Failed(
        override val paymentId: UUID,
        override val bookingId: UUID,
        val reason: String,
        override val aggregateVersion: Int,
        override val createdAt: Instant = Instant.now(),
    ) : PaymentEvent()
}
