package com.mengo.payment.domain.model.command

import java.math.BigDecimal
import java.util.UUID

sealed class PaymentCommand {
    data class BookingPayment(
        val bookingId: UUID,
        val totalPrice: BigDecimal,
    ) : PaymentCommand()

    data class PaymentInitiated(
        val paymentId: UUID,
        val bookingId: UUID,
        val totalPrice: BigDecimal,
    ) : SagaCommand()
}
