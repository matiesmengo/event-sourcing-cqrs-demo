package com.mengo.payment.fixtures

import java.math.BigDecimal
import java.util.UUID

object PaymentConstants {
    val BOOKING_ID: UUID = UUID.randomUUID()
    val PAYMENT_ID: UUID = UUID.randomUUID()

    const val PAYMENT_REFERENCE: String = "reference"
    const val PAYMENT_REASON: String = "reason"

    val TOTAL_PRICE: BigDecimal = BigDecimal("${(10..999).random()}.${(0..99).random()}").setScale(2)
    val PRODUCT_PRICE: BigDecimal = BigDecimal("${(10..999).random()}.${(0..99).random()}").setScale(2)
}
