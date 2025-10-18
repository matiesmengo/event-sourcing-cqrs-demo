package com.mengo.product.fixtures

import java.math.BigDecimal
import java.util.UUID

object ProductConstants {
    val BOOKING_ID: UUID = UUID.randomUUID()
    val PRODUCT_ID: UUID = UUID.randomUUID()

    val PRODUCT_QUANTITY = (5..50).random()
    val PRODUCT_PRICE = BigDecimal("${(10..999).random()}.${(0..99).random()}")
}
