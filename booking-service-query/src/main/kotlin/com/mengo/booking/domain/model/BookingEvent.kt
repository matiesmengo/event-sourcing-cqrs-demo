package com.mengo.booking.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

sealed class BookingQueryEvent {
    abstract val timestamp: Instant

    data class Created(
        val bookingId: UUID,
        val userId: UUID,
        val items: List<BookingEventItem>,
        override val timestamp: Instant = Instant.now(),
    ) : BookingQueryEvent()

    data class ProductReserved(
        val bookingId: UUID,
        val productId: UUID,
        val price: BigDecimal,
        override val timestamp: Instant,
    ) : BookingQueryEvent()

    data class PaymentProcessed(
        val bookingId: UUID,
        val reference: String,
        override val timestamp: Instant,
    ) : BookingQueryEvent()

    data class StatusChanged(
        val bookingId: UUID,
        val status: BookingStatus,
        val reason: String? = null,
        override val timestamp: Instant,
    ) : BookingQueryEvent()
}

data class BookingEventItem(
    val productId: UUID,
    val quantity: Int,
)
