package com.mengo.orchestrator.infrastructure.events.mapper

import com.mengo.orchestrator.domain.model.command.OrchestratorCommand
import com.mengo.orchestrator.domain.model.command.SagaCommand
import com.mengo.orchestrator.payload.OrchestratorRequestPaymentPayload
import com.mengo.payment.payload.PaymentCompletedPayload
import com.mengo.payment.payload.PaymentFailedPayload
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.ByteBuffer
import java.util.UUID

fun SagaCommand.RequestPayment.toAvro(): OrchestratorRequestPaymentPayload =
    OrchestratorRequestPaymentPayload(
        bookingId.toString(),
        totalPrice.toAvroDecimal(2),
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

// TODO: Common mapper
fun BigDecimal.toAvroDecimal(scale: Int = 2): ByteBuffer {
    val scaled = this.setScale(scale, RoundingMode.HALF_UP)
    val unscaled = scaled.unscaledValue().toByteArray()
    return ByteBuffer.wrap(unscaled)
}
