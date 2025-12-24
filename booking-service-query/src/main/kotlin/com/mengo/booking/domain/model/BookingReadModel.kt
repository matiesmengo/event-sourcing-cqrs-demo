package com.mengo.booking.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BookingReadModel(
    val bookingId: UUID,
    val userId: UUID? = null,
    val status: BookingStatus,
    val items: MutableList<BookingItem> = mutableListOf(),
    var totalPrice: BigDecimal = BigDecimal.ZERO,
    val paymentReference: String? = null,
    val cancelReason: String? = null,
    val updatedAt: Instant = Instant.now(),
)
