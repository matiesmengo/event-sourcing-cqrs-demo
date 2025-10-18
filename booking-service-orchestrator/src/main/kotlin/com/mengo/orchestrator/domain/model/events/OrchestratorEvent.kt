package com.mengo.orchestrator.domain.model.events

import com.mengo.orchestrator.domain.model.Product
import java.util.UUID

sealed class OrchestratorEvent {
    abstract val bookingId: UUID
    abstract val expectedProducts: Set<Product>

    data class Created(
        override val bookingId: UUID,
        override val expectedProducts: Set<Product>,
    ) : OrchestratorEvent() {
        fun startStockReservation(): WaitingStock = WaitingStock(bookingId, expectedProducts, mutableSetOf())
    }

    data class WaitingStock(
        override val bookingId: UUID,
        override val expectedProducts: Set<Product>,
        val reservedProducts: MutableSet<Product>,
    ) : OrchestratorEvent() {
        fun markProductReserved(product: Product): OrchestratorEvent {
            val newReserved = reservedProducts + product
            return if (newReserved.size == expectedProducts.size) {
                WaitingPayment(bookingId, expectedProducts, newReserved)
            } else {
                copy(reservedProducts = newReserved.toMutableSet())
            }
        }
    }

    data class WaitingPayment(
        override val bookingId: UUID,
        override val expectedProducts: Set<Product>,
        val reservedProducts: Set<Product>,
    ) : OrchestratorEvent() {
        fun completePayment(): Completed = Completed(bookingId, expectedProducts)
    }

    data class Completed(
        override val bookingId: UUID,
        override val expectedProducts: Set<Product>,
    ) : OrchestratorEvent()

    data class Compensating(
        override val bookingId: UUID,
        override val expectedProducts: Set<Product>,
    ) : OrchestratorEvent()
}
