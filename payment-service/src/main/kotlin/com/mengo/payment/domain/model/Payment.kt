package com.mengo.payment.domain.model

import java.time.Instant
import java.util.UUID

sealed class Payment(
    open val paymentId: UUID = UUID.randomUUID(),
    open val bookingId: UUID,
    open val createdAt: Instant = Instant.now(),
)

data class PendingPayment(
    override val paymentId: UUID = UUID.randomUUID(),
    override val bookingId: UUID,
    override val createdAt: Instant = Instant.now(),
) : Payment(paymentId, bookingId, createdAt)

data class CompletedPayment(
    override val paymentId: UUID,
    override val bookingId: UUID,
    val reference: String,
    override val createdAt: Instant = Instant.now(),
) : Payment(paymentId, bookingId, createdAt)

data class FailedPayment(
    override val paymentId: UUID,
    override val bookingId: UUID,
    val reason: String,
    override val createdAt: Instant = Instant.now(),
) : Payment(paymentId, bookingId, createdAt)
