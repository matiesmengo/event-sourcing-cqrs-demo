package com.mengo.payment.fixtures

import com.mengo.payment.domain.model.CompletedPayment
import com.mengo.payment.domain.model.FailedPayment
import com.mengo.payment.domain.model.PendingPayment
import com.mengo.payment.fixtures.PaymentConstants.BOOKING_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_ID
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_REASON
import com.mengo.payment.fixtures.PaymentConstants.PAYMENT_REFERENCE
import java.time.Instant
import java.util.UUID

object PaymentTestData {
    fun buildPendingPayment(
        paymentId: UUID = PAYMENT_ID,
        bookingId: UUID = BOOKING_ID,
        createdAt: Instant = Instant.now(),
    ): PendingPayment =
        PendingPayment(
            paymentId = paymentId,
            bookingId = bookingId,
            createdAt = createdAt,
        )

    fun buildCompletedPayment(
        paymentId: UUID = PAYMENT_ID,
        bookingId: UUID = BOOKING_ID,
        reference: String = PAYMENT_REFERENCE,
        createdAt: Instant = Instant.now(),
    ): CompletedPayment =
        CompletedPayment(
            paymentId = paymentId,
            bookingId = bookingId,
            reference = reference,
            createdAt = createdAt,
        )

    fun buildFailedPayment(
        paymentId: UUID = PAYMENT_ID,
        bookingId: UUID = BOOKING_ID,
        reason: String = PAYMENT_REASON,
        createdAt: Instant = Instant.now(),
    ): FailedPayment =
        FailedPayment(
            paymentId = paymentId,
            bookingId = bookingId,
            reason = reason,
            createdAt = createdAt,
        )
}
