package com.mengo.orchestrator.domain.model

import java.util.UUID

data class BookingCreated(
    val bookingId: UUID,
    val products: Set<Product>,
)
