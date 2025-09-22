package com.mengo.booking.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Booking(
    val bookingId: UUID,
    val userId: UUID,
    val resourceId: UUID,
    var bookingStatus: BookingStatus,
    val createdAt: OffsetDateTime,
)
