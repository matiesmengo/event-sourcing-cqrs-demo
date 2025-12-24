package com.mengo.booking.domain.model

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

sealed class BookingCommand {
    abstract val timestamp: Instant

    data class Create(
        val bookingId: UUID,
        val userId: UUID,
        val items: List<BookingItem>,
        val status: BookingStatus = BookingStatus.CREATED,
        override val timestamp: Instant,
    ) : BookingCommand()

    data class Price(
        val bookingId: UUID,
        val productId: UUID,
        val price: BigDecimal,
        override val timestamp: Instant,
    ) : BookingCommand()

    data class Payment(
        val bookingId: UUID,
        val reference: String,
        val status: BookingStatus,
        override val timestamp: Instant,
    ) : BookingCommand()

    data class Status(
        val bookingId: UUID,
        val status: BookingStatus,
        val reason: String? = null,
        override val timestamp: Instant,
    ) : BookingCommand()
}
