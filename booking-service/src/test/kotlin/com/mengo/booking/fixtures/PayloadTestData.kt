package com.mengo.booking.fixtures

import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PAYMENT_ID
import com.mengo.payment.payload.PaymentCompletedPayload
import com.mengo.payment.payload.PaymentFailedPayload
import java.time.Instant

object PayloadTestData {
    fun buildPaymentCompletedPayload() =
        PaymentCompletedPayload(
            PAYMENT_ID.toString(),
            BOOKING_ID.toString(),
            "reference-147",
            Instant.now().toString(),
        )

    fun buildPaymentFailedPayload() =
        PaymentFailedPayload(
            PAYMENT_ID.toString(),
            BOOKING_ID.toString(),
            "Card declined",
            Instant.now().toString(),
        )
}
