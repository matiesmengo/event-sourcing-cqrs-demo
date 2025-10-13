package com.mengo.booking.domain.model

import java.util.UUID

data class BookingItem(
    val productId: UUID,
    val quantity: Int,
)
