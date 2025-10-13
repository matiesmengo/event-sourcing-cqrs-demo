package com.mengo.product.domain.model

import java.time.Instant
import java.util.UUID

sealed class ProductEvent {
    abstract val productId: UUID
    abstract val aggregateVersion: Int
    abstract val createdAt: Instant
}

data class ProductCreatedEvent(
    override val productId: UUID,
    val stockTotal: Int,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : ProductEvent()

data class ProductReservedEvent(
    override val productId: UUID,
    val bookingId: UUID,
    val quantity: Int,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : ProductEvent()

data class ProductReleasedEvent(
    override val productId: UUID,
    val bookingId: UUID,
    val quantity: Int,
    override val aggregateVersion: Int,
    override val createdAt: Instant = Instant.now(),
) : ProductEvent()
