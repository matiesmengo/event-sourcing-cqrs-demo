package com.mengo.booking.domain.model

import java.util.UUID

sealed class PaidBooking(
    open val paymentId: UUID,
    open val bookingId: UUID,
)

data class SuccessPayment(
    override val paymentId: UUID,
    override val bookingId: UUID,
    val reference: String,
) : PaidBooking(paymentId, bookingId)

data class FailedPayment(
    override val paymentId: UUID,
    override val bookingId: UUID,
    val reason: String,
) : PaidBooking(paymentId, bookingId)
