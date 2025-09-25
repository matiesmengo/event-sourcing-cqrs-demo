package com.mengo.booking.fixtures

import com.mengo.booking.domain.model.Booking
import com.mengo.booking.domain.model.BookingStatus
import java.time.OffsetDateTime
import java.util.UUID

object BookingTestData {
    val BOOKING_ID = UUID.randomUUID()
    val PAYMENT_ID = UUID.randomUUID()
    val USER_ID = UUID.randomUUID()
    val RESOURCE_ID = UUID.randomUUID()

    fun buildBookingDomain(
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
