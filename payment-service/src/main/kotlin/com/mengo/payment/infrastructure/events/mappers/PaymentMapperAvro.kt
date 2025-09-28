package com.mengo.payment.infrastructure.events.mappers

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.events.PaymentCompletedEvent
import com.mengo.payment.events.PaymentFailedEvent

fun CompletedPayment.toAvro(): PaymentCompletedEvent =
    PaymentCompletedEvent(
        paymentId.toString(),
        bookingId.toString(),
        reference,
    )

fun FailedPayment.toAvro(): PaymentFailedEvent =
    PaymentFailedEvent(
        paymentId.toString(),
        bookingId.toString(),
        reason,
    )
