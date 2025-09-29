package com.mengo.booking.domain.model

import java.time.Instant
import java.util.UUID

data class Booking(
    val bookingId: UUID,
    val userId: UUID,
    val resourceId: UUID,
    var bookingStatus: BookingStatus,
    val createdAt: Instant,
    val updatedAt: Instant = Instant.now(),
) {
    fun confirm(): Booking = this.copy(bookingStatus = BookingStatus.PAID, updatedAt = Instant.now())

    fun cancel(): Booking = this.copy(bookingStatus = BookingStatus.CANCELLED, updatedAt = Instant.now())
}
