package com.mengo.orchestrator.domain.model

import java.util.UUID

data class ProductReservationFailed(
    val bookingId: UUID,
    val productId: UUID,
)
