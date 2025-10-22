package com.mengo.booking.infrastructure.api.mappers

import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.command.BookingCommand
import com.mengo.booking.model.BookingProduct
import com.mengo.booking.model.BookingResponse
import com.mengo.booking.model.CreateBookingRequest
import java.util.UUID

fun CreateBookingRequest.toDomain(): BookingCommand.CreateBooking =
    BookingCommand.CreateBooking(
        bookingId = UUID.randomUUID(),
        userId = userId,
        products = products.map { it.toDomain() },
    )

private fun BookingProduct.toDomain(): BookingItem =
    BookingItem(
        productId = productId,
        quantity = quantity,
    )

fun BookingCommand.CreateBooking.toApi(): BookingResponse =
    BookingResponse()
        .bookingId(bookingId)
        .status(BookingResponse.StatusEnum.CREATED)
