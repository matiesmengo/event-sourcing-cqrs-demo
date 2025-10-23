package com.mengo.product.infrastructure.events.mappers

import com.mengo.payload.product.ProductReservationFailedPayload
import com.mengo.payload.product.ProductReservedPayload
import com.mengo.product.domain.model.command.BookingCommand

fun BookingCommand.Reserved.toAvro(): ProductReservedPayload =
    ProductReservedPayload(
        productId.toString(),
        bookingId.toString(),
        quantity,
        price,
    )

fun BookingCommand.ReservedFailed.toAvro(): ProductReservationFailedPayload =
    ProductReservationFailedPayload(
        productId.toString(),
        bookingId.toString(),
    )
