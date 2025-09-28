package com.mengo.booking.fixtures

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.BookingStatus
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.RESOURCE_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import java.time.OffsetDateTime
import java.util.UUID

object BookingTestData {
    fun buildBooking(
        bookingId: UUID = BOOKING_ID,
        userId: UUID = USER_ID,
        resourceId: UUID = RESOURCE_ID,
        bookingStatus: BookingStatus = BookingStatus.CREATED,
        createdAt: OffsetDateTime = OffsetDateTime.now(),
    ): Booking =
        Booking(
            bookingId = bookingId,
            userId = userId,
            resourceId = resourceId,
            bookingStatus = bookingStatus,
            createdAt = createdAt,
        )
}
