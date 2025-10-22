package com.mengo.product.domain.model.command

import java.math.BigDecimal
import java.util.UUID

sealed class BookingCommand {
    data class Reserved(
        val productId: UUID,
        val bookingId: UUID,
        val quantity: Int,
        val price: BigDecimal,
    ) : BookingCommand()

    data class ReservedFailed(
        val productId: UUID,
        val bookingId: UUID,
    ) : BookingCommand()
}
