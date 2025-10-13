package com.mengo.product.domain.model

import java.util.UUID

data class ProductAggregate(
    val productId: UUID,
    val stockTotal: Int,
    val reserved: Int,
    val lastEventVersion: Int,
) {
    companion object {
        fun rehydrate(events: List<ProductEvent>): ProductAggregate {
            require(events.isNotEmpty()) { "No events provided for ProductAggregate rehydration" }

            return events
                .sortedBy { it.aggregateVersion }
                .fold(null as ProductAggregate?) { current, event -> applyEvent(current, event) }
                ?: throw IllegalStateException("Unable to rehydrate ProductAggregate from events")
        }

        private fun applyEvent(
            current: ProductAggregate?,
            event: ProductEvent,
        ): ProductAggregate =
            when (event) {
                is ProductCreatedEvent ->
                    ProductAggregate(
                        productId = event.productId,
                        stockTotal = event.stockTotal,
                        reserved = 0,
                        lastEventVersion = event.aggregateVersion,
                    )

                is ProductReservedEvent -> {
                    val state = current.requireInitialized(event)
                    state.copy(
                        reserved = state.reserved + event.quantity,
                        lastEventVersion = event.aggregateVersion,
                    )
                }

                is ProductReleasedEvent -> {
                    val state = current.requireInitialized(event)
                    state.copy(
                        reserved = (state.reserved - event.quantity).coerceAtLeast(0),
                        lastEventVersion = event.aggregateVersion,
                    )
                }
            }

        private fun ProductAggregate?.requireInitialized(event: ProductEvent): ProductAggregate =
            this ?: throw IllegalStateException(
                "Cannot apply ${event::class.simpleName} before ProductCreatedEvent",
            )
    }

    val availableStock: Int
        get() = stockTotal - reserved
}
