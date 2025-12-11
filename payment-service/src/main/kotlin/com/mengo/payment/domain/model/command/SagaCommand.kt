package com.mengo.payment.domain.model.command

import java.util.UUID

sealed class SagaCommand {
    data class PaymentCompleted(
        val paymentId: UUID,
        val bookingId: UUID,
        val reference: String,
    ) : SagaCommand()

    data class PaymentFailed(
        val paymentId: UUID,
        val bookingId: UUID,
        val reason: String,
    ) : SagaCommand()
}
