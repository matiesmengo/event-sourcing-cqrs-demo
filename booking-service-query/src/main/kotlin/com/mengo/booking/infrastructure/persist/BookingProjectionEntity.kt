package com.mengo.booking.infrastructure.persist

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal
import java.time.Instant

@Document(collection = "booking_projections")
data class BookingProjectionEntity(
    @Id val id: String,
    val userId: String? = null,
    val status: String? = null,
    val items: List<ItemProjectionEntity> = emptyList(),
    val paymentReference: String? = null,
    val cancelReason: String? = null,
    val updatedAt: Instant = Instant.now(),
    val lastEventTimestamp: Instant,
)

data class ItemProjectionEntity(
    val productId: String,
    val quantity: Int,
    var unitPrice: BigDecimal? = null,
    var lastPriceUpdateTimestamp: Instant? = null,
)
