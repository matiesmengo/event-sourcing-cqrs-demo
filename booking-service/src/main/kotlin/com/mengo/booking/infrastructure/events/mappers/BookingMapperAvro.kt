package com.mengo.booking.infrastructure.events.mappers

import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.BookingPaymentConfirmedEvent
import com.mengo.booking.domain.model.BookingPaymentFailedEvent
import com.mengo.booking.payload.BookingCancelledPayload
import com.mengo.booking.payload.BookingConfirmedPayload
import com.mengo.booking.payload.BookingCreatedPayload
import com.mengo.booking.payload.BookingProduct

fun BookingCreatedEvent.toAvro(): BookingCreatedPayload =
    BookingCreatedPayload(
        bookingId.toString(),
        userId.toString(),
        products.map { it.toAvro() },
        createdAt.toString(),
    )

fun BookingItem.toAvro(): BookingProduct =
    BookingProduct(
        productId.toString(),
        quantity,
    )

fun BookingPaymentConfirmedEvent.toAvro(): BookingConfirmedPayload =
    BookingConfirmedPayload(
        bookingId.toString(),
        createdAt.toString(),
    )

fun BookingPaymentFailedEvent.toAvro(): BookingCancelledPayload =
    BookingCancelledPayload(
        bookingId.toString(),
        createdAt.toString(),
        reason,
    )
