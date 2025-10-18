package com.mengo.booking.fixtures

import com.mengo.booking.domain.model.BookingConfirmedEvent
import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingFailedEvent
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import java.util.UUID

object BookingTestData {
    // BASE ENTITIES
    fun buildBookingItem(
        productId: UUID = UUID.randomUUID(),
        quantity: Int = 1,
    ): BookingItem =
        BookingItem(
            productId = productId,
            quantity = quantity,
        )

    // DOMAIN EVENTS
    fun buildBookingCreatedEvent(
        bookingId: UUID = BOOKING_ID,
        userId: UUID = USER_ID,
        items: List<BookingItem> = listOf(buildBookingItem()),
        version: Int = 1,
    ): BookingCreatedEvent =
        BookingCreatedEvent(
            bookingId = bookingId,
            userId = userId,
            products = items,
            aggregateVersion = version,
        )

    fun buildBookingPaymentConfirmedEvent(
        bookingId: UUID = BOOKING_ID,
        version: Int = 2,
    ): BookingConfirmedEvent =
        BookingConfirmedEvent(
            bookingId = bookingId,
            aggregateVersion = version,
        )

    fun buildBookingPaymentFailedEvent(
        bookingId: UUID = BOOKING_ID,
        version: Int = 2,
    ): BookingFailedEvent =
        BookingFailedEvent(
            bookingId = bookingId,
            aggregateVersion = version,
        )
}
