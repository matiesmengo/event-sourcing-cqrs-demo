package com.mengo.booking.infrastructure.events.mappers

import com.example.booking.events.BookingCreated
import com.mengo.booking.domain.model.Booking

fun Booking.toAvro() =
    BookingCreated(
        bookingId.toString(),
        userId.toString(),
        resourceId.toString(),
        Double.MAX_VALUE,
        bookingStatus.toString(),
        createdAt.toString(),
    )
