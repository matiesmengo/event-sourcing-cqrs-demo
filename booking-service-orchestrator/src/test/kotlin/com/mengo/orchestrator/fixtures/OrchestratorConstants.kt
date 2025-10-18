package com.mengo.orchestrator.fixtures

import java.math.BigDecimal
import java.util.UUID

object OrchestratorConstants {
    val BOOKING_ID: UUID = UUID.randomUUID()
    val USER_ID: UUID = UUID.randomUUID()
    val PRODUCT_ID: UUID = UUID.randomUUID()
    val PAYMENT_ID: UUID = UUID.randomUUID()

    val PRODUCT_QUANTITY = (5..50).random()
    val PRODUCT_PRICE = BigDecimal("${(10..999).random()}.${(0..99).random()}")

    const val PAYMENT_REFERENCE = "ref-1234"
    const val PAYMENT_REASON = "insufficient funds"
}
