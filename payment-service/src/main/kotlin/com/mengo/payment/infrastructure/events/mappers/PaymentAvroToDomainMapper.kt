package com.mengo.payment.infrastructure.events.mappers

import com.mengo.orchestrator.payload.OrchestratorRequestPaymentPayload
import com.mengo.payment.domain.model.BookingPayment
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.ByteBuffer
import java.util.UUID

fun OrchestratorRequestPaymentPayload.toDomain(): BookingPayment =
    BookingPayment(
        UUID.fromString(bookingId),
        totalPrice.toBigDecimal(),
    )

// TODO: common method
fun ByteBuffer.toBigDecimal(scale: Int = 2): BigDecimal {
    val bytes = ByteArray(this.remaining())
    this.get(bytes)
    return BigDecimal(BigInteger(bytes), scale)
}
