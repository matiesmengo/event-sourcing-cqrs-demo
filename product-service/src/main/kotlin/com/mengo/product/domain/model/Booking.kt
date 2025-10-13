package com.mengo.product.domain.model

import java.util.UUID

data class Booking(
    val bookingId: UUID,
    val products: List<BookingProduct>,
)
