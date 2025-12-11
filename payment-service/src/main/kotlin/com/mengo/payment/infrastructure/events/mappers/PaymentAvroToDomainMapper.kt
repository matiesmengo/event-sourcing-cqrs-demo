package com.mengo.payment.infrastructure.events.mappers

import com.mengo.payload.orchestrator.OrchestratorRequestPaymentPayload
import com.mengo.payload.payment.PaymentInitiatedPayload
import com.mengo.payment.domain.model.command.PaymentCommand
import java.util.UUID

fun OrchestratorRequestPaymentPayload.toDomain(): PaymentCommand.BookingPayment =
    PaymentCommand.BookingPayment(
        bookingId = UUID.fromString(bookingId),
        totalPrice = totalPrice,
    )

fun PaymentInitiatedPayload.toDomain(): PaymentCommand.PaymentInitiated =
    PaymentCommand.PaymentInitiated(
        paymentId = UUID.fromString(paymentId),
        bookingId = UUID.fromString(bookingId),
        totalPrice = price,
    )
