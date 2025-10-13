package com.mengo.booking.infrastructure.api.mappers

import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.model.BookingProduct
import com.mengo.booking.model.BookingResponse
import com.mengo.booking.model.CreateBookingRequest

fun CreateBookingRequest.toDomain(): BookingCreatedEvent =
    BookingCreatedEvent(
        userId = userId,
        products = products.map { it.toDomain() },
    )

private fun BookingProduct.toDomain(): BookingItem =
    BookingItem(
        productId = productId,
        quantity = quantity,
    )

fun BookingCreatedEvent.toApi(): BookingResponse =
    BookingResponse()
        .bookingId(bookingId)
        .status(BookingResponse.StatusEnum.CREATED)
