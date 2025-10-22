package com.mengo.product.domain.model

import com.mengo.product.domain.model.eventstore.ProductAggregate
import com.mengo.product.domain.model.eventstore.ProductReleasedEvent
import com.mengo.product.domain.model.eventstore.ProductReservedEvent
import com.mengo.product.fixtures.ProductConstants.BOOKING_ID
import com.mengo.product.fixtures.ProductConstants.PRODUCT_ID
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import kotlin.test.assertEquals

class ProductAggregateTest {
    @Test
    fun `create should return ProductCreatedEvent`() {
        val event = ProductAggregate.create(PRODUCT_ID, stockTotal = 10, price = BigDecimal(100))
        assertEquals(PRODUCT_ID, event.productId)
        assertEquals(10, event.stockTotal)
        assertEquals(BigDecimal(100), event.price)
        assertEquals(0, event.aggregateVersion)
    }

    @Test
    fun `rehydrate should build aggregate from events`() {
        val created = ProductAggregate.create(PRODUCT_ID, stockTotal = 10, price = BigDecimal(100))
        val reserved = ProductReservedEvent(PRODUCT_ID, BOOKING_ID, quantity = 3, aggregateVersion = 1)

        val aggregate = ProductAggregate.rehydrate(listOf(created, reserved))

        assertEquals(PRODUCT_ID, aggregate.productId)
        assertEquals(10, aggregate.stockTotal)
        assertEquals(3, aggregate.reserved)
        assertEquals(1, aggregate.lastEventVersion)
        assertEquals(7, aggregate.availableStock)
    }

    @Test
    fun `rehydrate should throw on empty events`() {
        assertThrows<IllegalArgumentException> {
            ProductAggregate.rehydrate(emptyList())
        }
    }

    @Test
    fun `reserveProduct should return ProductReservedEvent with incremented version`() {
        val created = ProductAggregate.create(PRODUCT_ID, stockTotal = 10, price = BigDecimal(100))
        val aggregate = ProductAggregate.rehydrate(listOf(created))

        val event = aggregate.reserveProduct(PRODUCT_ID, BOOKING_ID, quantity = 2)

        assertEquals(1, event.aggregateVersion)
        assertEquals(2, event.quantity)
        assertEquals(PRODUCT_ID, event.productId)
        assertEquals(BOOKING_ID, event.bookingId)
    }

    @Test
    fun `apply ProductReservedEvent updates reserved and availableStock`() {
        val created = ProductAggregate.create(PRODUCT_ID, stockTotal = 10, price = BigDecimal(100))
        val reservedEvent = ProductReservedEvent(PRODUCT_ID, BOOKING_ID, quantity = 4, aggregateVersion = 1)

        val aggregate = ProductAggregate.rehydrate(listOf(created, reservedEvent))
        assertEquals(4, aggregate.reserved)
        assertEquals(6, aggregate.availableStock)
        assertEquals(1, aggregate.lastEventVersion)
    }

    @Test
    fun `releaseProduct should return ProductReleasedEvent with incremented version`() {
        val created = ProductAggregate.create(PRODUCT_ID, stockTotal = 10, price = BigDecimal(100))
        val aggregate = ProductAggregate.rehydrate(listOf(created))

        val event = aggregate.releaseProduct(PRODUCT_ID, BOOKING_ID, quantity = 3)
        assertEquals(1, event.aggregateVersion)
        assertEquals(3, event.quantity)
        assertEquals(PRODUCT_ID, event.productId)
        assertEquals(BOOKING_ID, event.bookingId)
    }

    @Test
    fun `apply ProductReleasedEvent decreases reserved but never below zero`() {
        val created = ProductAggregate.create(PRODUCT_ID, stockTotal = 10, price = BigDecimal(100))
        val reserved = ProductReservedEvent(PRODUCT_ID, BOOKING_ID, quantity = 5, aggregateVersion = 1)
        val released = ProductReleasedEvent(PRODUCT_ID, BOOKING_ID, quantity = 7, aggregateVersion = 2)

        val aggregate = ProductAggregate.rehydrate(listOf(created, reserved, released))
        assertEquals(0, aggregate.reserved) // cannot go below 0
        assertEquals(10, aggregate.availableStock)
        assertEquals(2, aggregate.lastEventVersion)
    }
}
