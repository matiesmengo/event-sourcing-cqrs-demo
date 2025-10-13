package com.mengo.booking.infrastructure.events.mappers

import com.mengo.booking.domain.model.BookingPaymentConfirmedEvent
import com.mengo.booking.domain.model.BookingPaymentFailedEvent
import com.mengo.payment.payload.PaymentCompletedPayload
import com.mengo.payment.payload.PaymentFailedPayload
import java.time.Instant
import java.util.UUID

fun PaymentCompletedPayload.toDomain(): BookingPaymentConfirmedEvent =
    BookingPaymentConfirmedEvent(
        paymentId = UUID.fromString(paymentId),
        bookingId = UUID.fromString(bookingId),
        reference = reference,
        aggregateVersion = 2,
        createdAt = Instant.parse(createdAt),
    )

fun PaymentFailedPayload.toDomain(): BookingPaymentFailedEvent =
    BookingPaymentFailedEvent(
        paymentId = UUID.fromString(paymentId),
        bookingId = UUID.fromString(bookingId),
        reason = reason,
        aggregateVersion = 2,
        createdAt = Instant.parse(createdAt),
    )
