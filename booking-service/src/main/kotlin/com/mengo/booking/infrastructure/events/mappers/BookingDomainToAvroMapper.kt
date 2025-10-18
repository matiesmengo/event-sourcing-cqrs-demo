package com.mengo.booking.infrastructure.events.mappers

import com.mengo.booking.domain.model.BookingConfirmedEvent
import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingFailedEvent
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.payload.BookingCancelledPayload
import com.mengo.booking.payload.BookingConfirmedPayload
import com.mengo.booking.payload.BookingCreatedPayload
import com.mengo.booking.payload.BookingProduct

fun BookingCreatedEvent.toAvro(): BookingCreatedPayload =
    BookingCreatedPayload(
        bookingId.toString(),
        userId.toString(),
        products.map { it.toAvro() },
    )

fun BookingItem.toAvro(): BookingProduct =
    BookingProduct(
        productId.toString(),
        quantity,
    )

fun BookingConfirmedEvent.toAvro(): BookingConfirmedPayload =
    BookingConfirmedPayload(
        bookingId.toString(),
    )

fun BookingFailedEvent.toAvro(): BookingCancelledPayload =
    BookingCancelledPayload(
        bookingId.toString(),
        "reason",
    )

// TODO: check BookingCancelledPayload reason is needed
