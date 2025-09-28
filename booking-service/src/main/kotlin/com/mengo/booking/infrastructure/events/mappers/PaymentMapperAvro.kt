package com.mengo.booking.infrastructure.events.mappers

import com.mengo.booking.domain.model.FailedPayment
import com.mengo.booking.domain.model.SuccessPayment
import com.mengo.payment.events.PaymentCompletedEvent
import com.mengo.payment.events.PaymentFailedEvent
import java.util.UUID

fun PaymentCompletedEvent.toDomain(): SuccessPayment =
    SuccessPayment(
        paymentId = UUID.fromString(paymentId),
        bookingId = UUID.fromString(bookingId),
        reference = reference,
    )

fun PaymentFailedEvent.toDomain(): FailedPayment =
    FailedPayment(
        paymentId = UUID.fromString(paymentId),
        bookingId = UUID.fromString(bookingId),
        reason = reason,
    )
