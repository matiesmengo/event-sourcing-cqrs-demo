package com.mengo.booking.fixtures

import com.mengo.booking.domain.model.BookingCreatedEvent
import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.domain.model.BookingPaymentConfirmedEvent
import com.mengo.booking.domain.model.BookingPaymentFailedEvent
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_ID
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import java.time.Instant
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
        createdAt: Instant = Instant.now(),
    ): BookingCreatedEvent =
        BookingCreatedEvent(
            bookingId = bookingId,
            userId = userId,
            products = items,
            aggregateVersion = version,
            createdAt = createdAt,
        )

    fun buildBookingPaymentConfirmedEvent(
        bookingId: UUID = BOOKING_ID,
        paymentId: UUID = PAYMENT_ID,
        reference: String = "ref-123",
        version: Int = 2,
        createdAt: Instant = Instant.now(),
    ): BookingPaymentConfirmedEvent =
        BookingPaymentConfirmedEvent(
            bookingId = bookingId,
            paymentId = paymentId,
            reference = reference,
            aggregateVersion = version,
            createdAt = createdAt,
        )

    fun buildBookingPaymentFailedEvent(
        bookingId: UUID = BOOKING_ID,
        paymentId: UUID = PAYMENT_ID,
        reason: String = "Insufficient funds",
        version: Int = 2,
        createdAt: Instant = Instant.now(),
    ): BookingPaymentFailedEvent =
        BookingPaymentFailedEvent(
            bookingId = bookingId,
            paymentId = paymentId,
            reason = reason,
            aggregateVersion = version,
            createdAt = createdAt,
        )
}
