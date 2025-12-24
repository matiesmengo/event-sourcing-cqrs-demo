package com.mengo.booking.domain.model.eventstore

import com.mengo.booking.domain.model.BookingItem
import java.time.Instant
import java.util.UUID

sealed class BookingEvent {
    abstract val bookingId: UUID
    abstract val aggregateVersion: Int
    abstract val createdAt: Instant
}

data class BookingCreatedEvent(
    override val bookingId: UUID,
    val userId: UUID,
    val products: List<BookingItem>,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : BookingEvent()

data class BookingConfirmedEvent(
    override val bookingId: UUID,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : BookingEvent()

data class BookingFailedEvent(
    override val bookingId: UUID,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : BookingEvent()
