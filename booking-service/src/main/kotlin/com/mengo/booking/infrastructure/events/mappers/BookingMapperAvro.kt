package com.mengo.booking.infrastructure.events.mappers

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.events.BookingCreatedEvent

fun Booking.toAvro() =
    BookingCreatedEvent(
        bookingId.toString(),
        userId.toString(),
        resourceId.toString(),
        bookingStatus.name,
        createdAt.toInstant(),
    )
