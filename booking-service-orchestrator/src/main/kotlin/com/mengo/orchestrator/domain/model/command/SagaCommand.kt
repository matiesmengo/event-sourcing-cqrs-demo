package com.mengo.orchestrator.domain.model.command

import java.math.BigDecimal
import java.util.UUID

sealed class SagaCommand {
    data class RequestStock(
        val bookingId: UUID,
        val productId: UUID,
        val quantity: Int,
    ) : SagaCommand()

    data class ReleaseStock(
        val bookingId: UUID,
        val productId: UUID,
        val quantity: Int,
    ) : SagaCommand()

    data class RequestPayment(
        val bookingId: UUID,
        val totalPrice: BigDecimal,
    ) : SagaCommand()

    data class ConfirmBooking(
        val bookingId: UUID,
    ) : SagaCommand()

    data class CancelBooking(
        val bookingId: UUID,
    ) : SagaCommand()
}
