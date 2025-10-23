package com.mengo.payment.fixtures

import com.mengo.payload.orchestrator.OrchestratorRequestPaymentPayload
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PRODUCT_PRICE

object PayloadTestData {
    fun buildOrchestratorRequestPaymentPayload() =
        OrchestratorRequestPaymentPayload(
            BOOKING_ID.toString(),
            PRODUCT_PRICE,
        )
}
