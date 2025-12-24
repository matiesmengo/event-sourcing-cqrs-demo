package com.mengo.booking.fixtures

import java.math.BigDecimal
import java.util.UUID

object BookingConstants {
    val BOOKING_ID: UUID = UUID.randomUUID()
    val USER_ID: UUID = UUID.randomUUID()

    val PRODUCT_ID: UUID = UUID.randomUUID()
    val PRODUCT_PRICE: BigDecimal = BigDecimal("${(10..999).random()}.${(0..99).random()}").setScale(2)
    val PRODUCT_QUANTITY = (5..50).random()

    val PAYMENT_ID: UUID = UUID.randomUUID()
    const val PAYMENT_REFERENCE = "ref-1234"
    const val PAYMENT_REASON = "insufficient funds"
}
