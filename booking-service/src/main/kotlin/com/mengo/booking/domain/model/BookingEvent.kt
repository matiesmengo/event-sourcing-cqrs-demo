package com.mengo.booking.domain.model

import java.util.UUID

sealed class BookingEvent {
    abstract val bookingId: UUID
    abstract val aggregateVersion: Int
}

data class BookingCreatedEvent(
    override val bookingId: UUID = UUID.randomUUID(),
    val userId: UUID,
    val products: List<BookingItem>,
    override val aggregateVersion: Int = 1,
) : BookingEvent()

data class BookingConfirmedEvent(
    override val bookingId: UUID,
    override val aggregateVersion: Int = 2,
) : BookingEvent()

data class BookingFailedEvent(
    override val bookingId: UUID,
    override val aggregateVersion: Int = 2,
) : BookingEvent()
