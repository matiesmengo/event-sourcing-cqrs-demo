package com.mengo.product.domain.model.eventstore

import java.math.BigDecimal
import java.util.UUID

data class ProductAggregate(
    val productId: UUID,
    val stockTotal: Int,
    val reserved: Int,
    val price: BigDecimal,
    val lastEventVersion: Int,
) {
    companion object {
        fun create(
            productId: UUID,
            stockTotal: Int,
            price: BigDecimal,
        ): ProductCreatedEvent =
            ProductCreatedEvent(
                productId = productId,
                stockTotal = stockTotal,
                price = price,
                aggregateVersion = 0,
            )

        fun rehydrate(events: List<ProductEvent>): ProductAggregate {
            require(events.isNotEmpty()) { "No events provided for rehydration" }

            return events
                .sortedBy { it.aggregateVersion }
                .fold(null as ProductAggregate?) { current, event -> current?.applyEvent(event) ?: fromEvent(event) }
                ?: throw IllegalStateException("Failed to rehydrate BookingAggregate")
        }

        private fun fromEvent(event: ProductEvent): ProductAggregate =
            when (event) {
                is ProductCreatedEvent -> {
                    ProductAggregate(
                        productId = event.productId,
                        stockTotal = event.stockTotal,
                        price = event.price,
                        lastEventVersion = event.aggregateVersion,
                        reserved = 0,
                    )
                }

                else -> {
                    error("Unsupported initial event type: ${event::class.simpleName}")
                }
            }
    }

    private fun applyEvent(event: ProductEvent): ProductAggregate =
        when (event) {
            is ProductCreatedEvent -> {
                fromEvent(event)
            }

            is ProductReservedEvent -> {
                copy(reserved = reserved + event.quantity, lastEventVersion = event.aggregateVersion)
            }

            is ProductReleasedEvent -> {
                copy(reserved = (reserved - event.quantity).coerceAtLeast(0), lastEventVersion = event.aggregateVersion)
            }
        }

    val availableStock: Int
        get() = stockTotal - reserved

    fun reserveProduct(
        productId: UUID,
        bookingId: UUID,
        quantity: Int,
    ): ProductReservedEvent =
        ProductReservedEvent(
            productId = productId,
            bookingId = bookingId,
            quantity = quantity,
            aggregateVersion = lastEventVersion + 1,
        )

    fun releaseProduct(
        productId: UUID,
        bookingId: UUID,
        quantity: Int,
    ): ProductReleasedEvent =
        ProductReleasedEvent(
            productId = productId,
            bookingId = bookingId,
            quantity = quantity,
            aggregateVersion = lastEventVersion + 1,
        )
}
