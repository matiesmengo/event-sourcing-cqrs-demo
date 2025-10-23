package com.mengo.booking.infrastructure.events.mappers

import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.command.SagaCommand
import com.mengo.payload.booking.BookingCancelledPayload
import com.mengo.payload.booking.BookingConfirmedPayload
import com.mengo.payload.booking.BookingCreatedPayload
import com.mengo.payload.booking.BookingProduct

fun SagaCommand.BookingCreated.toAvro(): BookingCreatedPayload =
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

fun SagaCommand.BookingConfirmed.toAvro(): BookingConfirmedPayload =
    BookingConfirmedPayload(
        bookingId.toString(),
    )

fun SagaCommand.BookingFailed.toAvro(): BookingCancelledPayload =
    BookingCancelledPayload(
        bookingId.toString(),
        "reason",
    )

// TODO: check BookingCancelledPayload reason is needed
