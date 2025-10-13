package com.mengo.booking.domain.model

import java.time.Instant
import java.util.UUID

sealed class BookingEvent {
    abstract val bookingId: UUID
    abstract val aggregateVersion: Int
    abstract val createdAt: Instant
}

data class BookingCreatedEvent(
    override val bookingId: UUID = UUID.randomUUID(),
    val userId: UUID,
    val products: List<BookingItem>,
    override val aggregateVersion: Int = 0,
    override val createdAt: Instant = Instant.now(),
) : BookingEvent()

data class BookingPaymentConfirmedEvent(
    override val bookingId: UUID,
    val paymentId: UUID,
    val reference: String,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : BookingEvent()

data class BookingPaymentFailedEvent(
    override val bookingId: UUID,
    val paymentId: UUID,
    val reason: String,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : BookingEvent()
