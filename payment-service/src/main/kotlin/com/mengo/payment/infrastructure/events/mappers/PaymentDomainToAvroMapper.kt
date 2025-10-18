package com.mengo.payment.infrastructure.events.mappers

import com.mengo.payment.domain.model.PaymentCompletedEvent
import com.mengo.payment.domain.model.PaymentFailedEvent
import com.mengo.payment.domain.model.PaymentInitiatedEvent
import com.mengo.payment.payload.PaymentCompletedPayload
import com.mengo.payment.payload.PaymentFailedPayload
import com.mengo.payment.payload.PaymentInitiatedPayload
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.ByteBuffer

fun PaymentInitiatedEvent.toAvro(): PaymentInitiatedPayload =
    PaymentInitiatedPayload(
        paymentId.toString(),
        bookingId.toString(),
        totalPrice.toAvroDecimal(),
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

// TODO: Common mapper
fun BigDecimal.toAvroDecimal(scale: Int = 2): ByteBuffer {
    val scaled = this.setScale(scale, RoundingMode.HALF_UP)
    val unscaled = scaled.unscaledValue().toByteArray()
    return ByteBuffer.wrap(unscaled)
}
