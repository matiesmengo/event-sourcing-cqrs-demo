package com.mengo.orchestrator.domain.model.command

import com.mengo.orchestrator.domain.model.Product
import java.math.BigDecimal
import java.util.UUID

sealed class OrchestratorCommand {
    data class BookingCreated(
        val bookingId: UUID,
        val products: Set<Product>,
    ) : OrchestratorCommand()

    data class PaymentCompleted(
        val bookingId: UUID,
        val paymentId: UUID,
        val reference: String,
    ) : OrchestratorCommand()

    data class PaymentFailed(
        val bookingId: UUID,
        val paymentId: UUID,
        val reason: String? = null,
    ) : OrchestratorCommand()

    data class ProductReservationFailed(
        val bookingId: UUID,
        val productId: UUID,
    ) : OrchestratorCommand()

    data class ProductReserved(
        val bookingId: UUID,
        val productId: UUID,
        val quantity: Int,
        val price: BigDecimal,
    ) : OrchestratorCommand()
}
