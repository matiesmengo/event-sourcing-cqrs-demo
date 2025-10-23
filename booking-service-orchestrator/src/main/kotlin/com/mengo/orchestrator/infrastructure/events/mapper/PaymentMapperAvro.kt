package com.mengo.orchestrator.infrastructure.events.mapper

import com.mengo.orchestrator.domain.model.command.OrchestratorCommand
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.payload.orchestrator.OrchestratorRequestPaymentPayload
import com.mengo.payload.payment.PaymentCompletedPayload
import com.mengo.payload.payment.PaymentFailedPayload
import java.util.UUID

fun SagaCommand.RequestPayment.toAvro(): OrchestratorRequestPaymentPayload =
    OrchestratorRequestPaymentPayload(
        bookingId.toString(),
        totalPrice,
    )

fun PaymentCompletedPayload.toDomain(): OrchestratorCommand.PaymentCompleted =
    OrchestratorCommand.PaymentCompleted(
        bookingId = UUID.fromString(bookingId),
        paymentId = UUID.fromString(paymentId),
        reference = reference,
    )

fun PaymentFailedPayload.toDomain(): OrchestratorCommand.PaymentFailed =
    OrchestratorCommand.PaymentFailed(
        bookingId = UUID.fromString(bookingId),
        paymentId = UUID.fromString(paymentId),
        reason = reason,
    )
