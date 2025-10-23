package com.mengo.payment.infrastructure.events.mappers

import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import com.mengo.payload.payment.PaymentInitiatedPayload
import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent

fun PaymentInitiatedEvent.toAvro(): PaymentInitiatedPayload =
    PaymentInitiatedPayload(
        paymentId.toString(),
        bookingId.toString(),
        totalPrice,
    )

fun PaymentCompletedEvent.toAvro(): PaymentCompletedPayload =
    PaymentCompletedPayload(
        paymentId.toString(),
        bookingId.toString(),
        reference,
    )

fun PaymentFailedEvent.toAvro(): PaymentFailedPayload =
    PaymentFailedPayload(
        paymentId.toString(),
        bookingId.toString(),
        reason,
    )
