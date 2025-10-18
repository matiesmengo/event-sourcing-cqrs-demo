package com.mengo.product.domain.model

import java.util.UUID

data class BookingProduct(
    val bookingId: UUID,
    val productId: UUID,
    val quantity: Int,
)
