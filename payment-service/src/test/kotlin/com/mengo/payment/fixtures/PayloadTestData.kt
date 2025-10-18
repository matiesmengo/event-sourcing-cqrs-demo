package com.mengo.payment.fixtures

import com.mengo.orchestrator.payload.OrchestratorRequestPaymentPayload
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.ByteBuffer

object PayloadTestData {
    fun buildOrchestratorRequestPaymentPayload() =
        OrchestratorRequestPaymentPayload(
            BOOKING_ID.toString(),
            BigDecimal.TEN.toAvroDecimal(),
        )
}

// TODO: migrate
fun BigDecimal.toAvroDecimal(scale: Int = 2): ByteBuffer {
    val scaled = this.setScale(scale, RoundingMode.HALF_UP)
    val unscaled = scaled.unscaledValue().toByteArray()
    return ByteBuffer.wrap(unscaled)
}
