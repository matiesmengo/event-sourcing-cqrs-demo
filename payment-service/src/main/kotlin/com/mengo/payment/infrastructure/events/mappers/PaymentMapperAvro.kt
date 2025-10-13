package com.mengo.payment.infrastructure.events.mappers

import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.payload.PaymentCompletedPayload
import com.mengo.payment.payload.PaymentFailedPayload
import com.mengo.payment.payload.PaymentInitiatedPayload

fun PaymentInitiatedEvent.toAvro(): PaymentInitiatedPayload =
    PaymentInitiatedPayload(
        paymentId.toString(),
        bookingId.toString(),
        totalAmount.toPlainString(),
        createdAt.toString(),
    )

fun PaymentCompletedEvent.toAvro(): PaymentCompletedPayload =
    PaymentCompletedPayload(
        paymentId.toString(),
        bookingId.toString(),
        reference,
        createdAt.toString(),
    )

fun PaymentFailedEvent.toAvro(): PaymentFailedPayload =
    PaymentFailedPayload(
        paymentId.toString(),
        bookingId.toString(),
        reason,
        createdAt.toString(),
    )
