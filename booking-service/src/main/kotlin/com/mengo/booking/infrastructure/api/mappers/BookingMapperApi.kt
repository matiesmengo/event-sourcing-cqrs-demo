package com.mengo.booking.infrastructure.api.mappers

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.domain.model.CreateBooking
import com.mengo.booking.model.BookingResponse
import com.mengo.booking.model.CreateBookingRequest

fun CreateBookingRequest.toDomain(): CreateBooking =
    CreateBooking(
        userId = userId,
        resourceId = resourceId,
    )

fun Booking.toApi(): BookingResponse =
    BookingResponse()
        .bookingId(bookingId)
        .userId(userId)
        .resourceId(resourceId)
        .status(bookingStatus.toApi())

private fun BookingStatus.toApi(): BookingResponse.StatusEnum =
    when (this) {
        BookingStatus.CREATED -> BookingResponse.StatusEnum.CREATED
        BookingStatus.CANCELLED -> BookingResponse.StatusEnum.CANCELLED
        BookingStatus.PAID -> BookingResponse.StatusEnum.PAID
    }
