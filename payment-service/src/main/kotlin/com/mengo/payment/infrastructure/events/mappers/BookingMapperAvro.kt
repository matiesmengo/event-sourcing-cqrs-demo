package com.mengo.payment.infrastructure.events.mappers

import com.mengo.booking.events.BookingCreatedEvent
import com.mengo.payment.domain.model.BookingPayment
import java.util.UUID

fun BookingCreatedEvent.toDomain(): BookingPayment = BookingPayment(UUID.fromString(bookingId))
