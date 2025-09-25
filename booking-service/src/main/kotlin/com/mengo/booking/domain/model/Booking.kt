package com.mengo.booking.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Booking(
    val bookingId: UUID,
    val userId: UUID,
    val resourceId: UUID,
    var bookingStatus: BookingStatus,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime = OffsetDateTime.now(),
) {
    fun confirm(): Booking = this.copy(bookingStatus = BookingStatus.PAID, updatedAt = OffsetDateTime.now())

    fun cancel(): Booking = this.copy(bookingStatus = BookingStatus.CANCELLED, updatedAt = OffsetDateTime.now())
}
