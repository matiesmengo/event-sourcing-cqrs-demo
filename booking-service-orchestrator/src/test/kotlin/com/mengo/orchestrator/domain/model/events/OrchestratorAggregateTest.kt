package com.mengo.orchestrator.domain.model.events

import com.mengo.orchestrator.domain.model.Product
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class OrchestratorAggregateTest {
    private val bookingId = UUID.randomUUID()
    private val product1 = Product(UUID.randomUUID(), 2, BigDecimal.TEN)
    private val product2 = Product(UUID.randomUUID(), 1, BigDecimal.ONE)

    @Test
    fun `rehydrate should return aggregate from single Created event`() {
        val created = OrchestratorEvent.Created(bookingId, setOf(product1), 0)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(created))

        assertEquals(bookingId, aggregate.bookingId)
        assertEquals(setOf(product1), aggregate.expectedProducts)
        assertEquals(0, aggregate.lastEventVersion)
        assertEquals(OrchestratorState.CREATED, aggregate.state)
    }

    @Test
    fun `rehydrate should throw when events list is empty`() {
        assertThrows<IllegalArgumentException> {
            OrchestratorAggregate.rehydrate(emptyList())
        }
    }

    @Test
    fun `reserveProduct moves state correctly`() {
        val created = OrchestratorEvent.Created(bookingId, setOf(product1, product2), 0)
        var aggregate = OrchestratorAggregate.rehydrate(listOf(created))

        // reserve first product
        val event1 = aggregate.reserveProduct(product1)
        aggregate = OrchestratorAggregate.rehydrate(listOf(created, event1))
        assertEquals(setOf(product1), aggregate.reservedProducts)
        assertEquals(OrchestratorState.WAITING_STOCK, aggregate.state)

        // reserve second product
        val event2 = aggregate.reserveProduct(product2)
        aggregate = OrchestratorAggregate.rehydrate(listOf(created, event1, event2))
        assertEquals(setOf(product1, product2), aggregate.reservedProducts)
        assertEquals(OrchestratorState.WAITING_PAYMENT, aggregate.state)
    }

    @Test
    fun `reserveProduct returns CompensatedProduct in COMPENSATING or CANCELLED state`() {
        val created = OrchestratorEvent.Created(bookingId, setOf(product1), 0)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(created)).copy(state = OrchestratorState.COMPENSATING)

        val event = aggregate.reserveProduct(product1)
        assertTrue(event is OrchestratorEvent.CompensatedProduct)
    }

    @Test
    fun `failProductReservation works in valid states`() {
        val created = OrchestratorEvent.Created(bookingId, setOf(product1), 0)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(created))

        val event = aggregate.failProductReservation(product1.productId)
        assertTrue(event is OrchestratorEvent.ProductReservationFailed)
        assertEquals(1, event.aggregateVersion)
    }

    @Test
    fun `failProductReservation works if the first product was already failed`() {
        val created = OrchestratorEvent.Created(bookingId, setOf(product1, product2), 0)
        val productFailed = OrchestratorEvent.ProductReservationFailed(bookingId, product1.productId, 1)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(created, productFailed))

        val event = aggregate.failProductReservation(product2.productId)
        assertTrue(event is OrchestratorEvent.ProductReservationFailed)
        assertEquals(2, event.aggregateVersion)
    }

    @Test
    fun `failProductReservation throws in invalid state`() {
        val created = OrchestratorEvent.Created(bookingId, setOf(product1), 0)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(created)).copy(state = OrchestratorState.COMPLETED)

        assertThrows<IllegalStateException> {
            aggregate.failProductReservation(product1.productId)
        }
    }

    @Test
    fun `completePayment works in WAITING_PAYMENT state`() {
        val created = OrchestratorEvent.Created(bookingId, setOf(product1), 0)
        val reservedEvent = OrchestratorEvent.ProductReserved(bookingId, product1, 1)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(created, reservedEvent))

        assertEquals(OrchestratorState.WAITING_PAYMENT, aggregate.state)

        val event = aggregate.completePayment()
        assertTrue(event is OrchestratorEvent.PaymentCompleted)
        assertEquals(2, event.aggregateVersion)
    }

    @Test
    fun `completePayment throws in invalid state`() {
        val created = OrchestratorEvent.Created(bookingId, setOf(product1), 0)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(created))

        assertThrows<IllegalStateException> {
            aggregate.completePayment()
        }
    }

    @Test
    fun `failPayment works in WAITING_PAYMENT state`() {
        val created = OrchestratorEvent.Created(bookingId, setOf(product1), 0)
        val reservedEvent = OrchestratorEvent.ProductReserved(bookingId, product1, 1)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(created, reservedEvent))

        val event = aggregate.failPayment()
        assertTrue(event is OrchestratorEvent.PaymentFailed)
        assertEquals(2, event.aggregateVersion)
    }

    @Test
    fun `failPayment throws in invalid state`() {
        val created = OrchestratorEvent.Created(bookingId, setOf(product1), 0)
        val aggregate = OrchestratorAggregate.rehydrate(listOf(created))

        assertThrows<IllegalStateException> {
            aggregate.failPayment()
        }
    }
}
