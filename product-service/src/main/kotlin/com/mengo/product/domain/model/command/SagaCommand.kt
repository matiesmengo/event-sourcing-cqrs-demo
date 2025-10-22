package com.mengo.product.domain.model.command

import java.util.UUID

sealed class SagaCommand {
    data class ReserveProduct(
        val bookingId: UUID,
        val productId: UUID,
        val quantity: Int,
    ) : SagaCommand()

    data class ReleaseProduct(
        val bookingId: UUID,
        val productId: UUID,
        val quantity: Int,
    ) : SagaCommand()
}
