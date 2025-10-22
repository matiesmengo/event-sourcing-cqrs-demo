package com.mengo.orchestrator.domain.model.events

import OrchestratorEvent
import com.mengo.orchestrator.domain.model.Product
import java.util.UUID

enum class OrchestratorState {
    CREATED,
    WAITING_STOCK,
    WAITING_PAYMENT,
    COMPLETED,
    COMPENSATING,
    CANCELLED,
}

data class OrchestratorAggregate(
    val bookingId: UUID,
    val expectedProducts: Set<Product>,
    val reservedProducts: Set<Product> = emptySet(),
    val lastEventVersion: Int,
    val state: OrchestratorState = OrchestratorState.CREATED,
) {
    companion object {
        fun createBookingEvent(
            bookingId: UUID,
            expectedProducts: Set<Product>,
        ): OrchestratorEvent =
            OrchestratorEvent.Created(
                bookingId = bookingId,
                expectedProducts = expectedProducts,
                aggregateVersion = 0,
            )

        fun rehydrate(events: List<OrchestratorEvent>): OrchestratorAggregate {
            require(events.isNotEmpty()) { "No events provided for rehydration" }

            return events
                .sortedBy { it.aggregateVersion }
                .fold(null as OrchestratorAggregate?) { current, event ->
                    current?.applyEvent(event) ?: fromEvent(event)
                }
                ?: throw IllegalStateException("Failed to rehydrate OrchestratorEvent")
        }

        private fun fromEvent(event: OrchestratorEvent): OrchestratorAggregate =
            when (event) {
                is OrchestratorEvent.Created ->
                    OrchestratorAggregate(
                        bookingId = event.bookingId,
                        expectedProducts = event.expectedProducts,
                        reservedProducts = emptySet(),
                        lastEventVersion = event.aggregateVersion,
                        state = OrchestratorState.CREATED,
                    )
                else -> error("Unsupported initial event type: ${event::class.simpleName}")
            }
    }

    fun applyEventSafely(event: OrchestratorEvent): OrchestratorAggregate {
        require(event.bookingId == bookingId) {
            "Event bookingId ${event.bookingId} does not match aggregate $bookingId"
        }
        require(event.aggregateVersion == lastEventVersion + 1) {
            "Invalid event version ${event.aggregateVersion}, expected ${lastEventVersion + 1}"
        }
        return applyEvent(event)
    }

    private fun applyEvent(event: OrchestratorEvent): OrchestratorAggregate =
        when (event) {
            is OrchestratorEvent.Created -> fromEvent(event)

            is OrchestratorEvent.ProductReserved ->
                copy(
                    reservedProducts = reservedProducts + event.product,
                    lastEventVersion = event.aggregateVersion,
                    state =
                        if (reservedProducts.size + 1 == expectedProducts.size) {
                            OrchestratorState.WAITING_PAYMENT
                        } else {
                            OrchestratorState.WAITING_STOCK
                        },
                )

            is OrchestratorEvent.CompensatedProduct ->
                copy(
                    reservedProducts = reservedProducts - event.product,
                    lastEventVersion = event.aggregateVersion,
                )

            is OrchestratorEvent.PaymentCompleted ->
                copy(
                    lastEventVersion = event.aggregateVersion,
                    state = OrchestratorState.COMPLETED,
                )

            is OrchestratorEvent.ProductReservationFailed,
            is OrchestratorEvent.PaymentFailed,
            ->
                copy(
                    lastEventVersion = event.aggregateVersion,
                    state = OrchestratorState.COMPENSATING,
                )
        }

    fun reserveProduct(product: Product): OrchestratorEvent =
        when (state) {
            OrchestratorState.CREATED, OrchestratorState.WAITING_STOCK ->
                OrchestratorEvent.ProductReserved(
                    bookingId,
                    product,
                    lastEventVersion + 1,
                )
            OrchestratorState.COMPENSATING, OrchestratorState.CANCELLED ->
                OrchestratorEvent.CompensatedProduct(
                    bookingId,
                    product,
                    lastEventVersion + 1,
                )
            else -> error("Cannot reserve product in state $state")
        }

    fun failProductReservation(productId: UUID): OrchestratorEvent {
        if (state in setOf(OrchestratorState.CREATED, OrchestratorState.WAITING_STOCK, OrchestratorState.WAITING_PAYMENT)) {
            return OrchestratorEvent.ProductReservationFailed(bookingId, productId, lastEventVersion + 1)
        }
        throw IllegalStateException("Cannot fail reservation in state $state")
    }

    fun completePayment(): OrchestratorEvent {
        if (state == OrchestratorState.WAITING_PAYMENT) {
            return OrchestratorEvent.PaymentCompleted(bookingId, lastEventVersion + 1)
        }
        throw IllegalStateException("Cannot complete payment in state $state")
    }

    fun failPayment(): OrchestratorEvent {
        if (state == OrchestratorState.WAITING_PAYMENT) {
            return OrchestratorEvent.PaymentFailed(bookingId, lastEventVersion + 1)
        }
        throw IllegalStateException("Cannot fail payment in state $state")
    }
}
