package com.mengo.payment.fixtures

import java.util.UUID

object PaymentConstants {
    val BOOKING_ID: UUID = UUID.randomUUID()
    val PAYMENT_ID: UUID = UUID.randomUUID()
    val USER_ID: UUID = UUID.randomUUID()
    val RESOURCE_ID: UUID = UUID.randomUUID()

    const val PAYMENT_REFERENCE: String = "reference"
    const val PAYMENT_REASON: String = "reason"
}
