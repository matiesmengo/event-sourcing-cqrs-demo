package com.mengo.payment.infrastructure.events.mappers

import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import com.mengo.payload.payment.PaymentInitiatedPayload
import com.mengo.payment.domain.model.command.PaymentCommand
import com.mengo.payment.domain.model.command.SagaCommand

fun PaymentCommand.PaymentInitiated.toAvro(): PaymentInitiatedPayload =
    PaymentInitiatedPayload(
        paymentId.toString(),
        bookingId.toString(),
        totalPrice,
    )

fun SagaCommand.PaymentCompleted.toAvro(): PaymentCompletedPayload =
    PaymentCompletedPayload(
        paymentId.toString(),
        bookingId.toString(),
        reference,
    )

fun SagaCommand.PaymentFailed.toAvro(): PaymentFailedPayload =
    PaymentFailedPayload(
        paymentId.toString(),
        bookingId.toString(),
        reason,
    )
