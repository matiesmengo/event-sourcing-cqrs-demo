package com.mengo.booking.domain.model.eventstore

import com.mengo.booking.domain.model.BookingItem
import com.mengo.booking.fixtures.BookingConstants.BOOKING_ID
import com.mengo.booking.fixtures.BookingConstants.PRODUCT_QUANTITY
import com.mengo.booking.fixtures.BookingConstants.USER_ID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class BookingAggregateTest {
    private val products = listOf(BookingItem(productId = UUID.randomUUID(), quantity = PRODUCT_QUANTITY))

    @Test
    fun `create should return BookingCreatedEvent with correct data`() {
        val event = BookingAggregate.create(BOOKING_ID, USER_ID, products)

        assertEquals(BOOKING_ID, event.bookingId)
        assertEquals(USER_ID, event.userId)
        assertEquals(products, event.products)
        assertEquals(0, event.aggregateVersion)
    }

    @Test
    fun `rehydrate should throw when event list is empty`() {
        val ex =
            assertThrows<IllegalArgumentException> {
                BookingAggregate.rehydrate(emptyList())
            }
        assertTrue(ex.message!!.contains("No events provided"))
    }

    @Test
    fun `rehydrate should rebuild aggregate with correct status and version`() {
        val created =
            BookingCreatedEvent(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                products = products,
                aggregateVersion = 0,
            )
        val confirmed =
            BookingConfirmedEvent(
                bookingId = BOOKING_ID,
                aggregateVersion = 1,
            )

        val aggregate = BookingAggregate.rehydrate(listOf(confirmed, created)) // intentionally unordered

        assertEquals(BOOKING_ID, aggregate.bookingId)
        assertEquals(USER_ID, aggregate.userId)
        assertEquals(BookingAggregateStatus.CONFIRMED, aggregate.status)
        assertEquals(1, aggregate.lastEventVersion)
        assertEquals(products, aggregate.products)
    }

    @Test
    fun `rehydrate should handle failed booking event correctly`() {
        val created = BookingCreatedEvent(BOOKING_ID, USER_ID, products, 0)
        val failed = BookingFailedEvent(BOOKING_ID, 1)
        val aggregate = BookingAggregate.rehydrate(listOf(created, failed))

        assertEquals(BookingAggregateStatus.FAILED, aggregate.status)
        assertEquals(1, aggregate.lastEventVersion)
    }

    @Test
    fun `confirmedBooking should create BookingConfirmedEvent when status is CREATED`() {
        val aggregate =
            BookingAggregate(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                products = products,
                status = BookingAggregateStatus.CREATED,
                lastEventVersion = 5,
            )

        val event = aggregate.confirmedBooking()
        assertEquals(6, event.aggregateVersion)
    }

    @Test
    fun `confirmedBooking should throw when status is CONFIRMED`() {
        val aggregate =
            BookingAggregate(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                products = products,
                status = BookingAggregateStatus.CONFIRMED,
                lastEventVersion = 2,
            )

        val ex = assertThrows<IllegalStateException> { aggregate.confirmedBooking() }
        assertTrue(ex.message!!.contains("Cannot mark as confirmed"))
    }

    @Test
    fun `confirmedBooking should throw when status is FAILED`() {
        val aggregate =
            BookingAggregate(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                products = products,
                status = BookingAggregateStatus.FAILED,
                lastEventVersion = 2,
            )

        val ex = assertThrows<IllegalStateException> { aggregate.confirmedBooking() }
        assertTrue(ex.message!!.contains("Cannot mark as confirmed"))
    }

    @Test
    fun `failedBooking should create BookingFailedEvent when status is CREATED`() {
        val aggregate =
            BookingAggregate(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                products = products,
                status = BookingAggregateStatus.CREATED,
                lastEventVersion = 3,
            )

        val event = aggregate.failedBooking()
        assertEquals(4, event.aggregateVersion)
    }

    @Test
    fun `failedBooking should throw when status is CONFIRMED`() {
        val aggregate =
            BookingAggregate(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                products = products,
                status = BookingAggregateStatus.CONFIRMED,
                lastEventVersion = 3,
            )

        val ex = assertThrows<IllegalStateException> { aggregate.failedBooking() }
        assertTrue(ex.message!!.contains("Cannot mark as failed"))
    }

    @Test
    fun `failedBooking should throw when status is FAILED`() {
        val aggregate =
            BookingAggregate(
                bookingId = BOOKING_ID,
                userId = USER_ID,
                products = products,
                status = BookingAggregateStatus.FAILED,
                lastEventVersion = 3,
            )

        val ex = assertThrows<IllegalStateException> { aggregate.failedBooking() }
        assertTrue(ex.message!!.contains("Cannot mark as failed"))
    }
}
